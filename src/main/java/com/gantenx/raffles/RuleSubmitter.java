package com.gantenx.raffles;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.Category;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.ConfigManager;
import com.gantenx.raffles.enums.DataType;
import com.gantenx.raffles.model.FlinkRule;
import com.gantenx.raffles.service.RuleService;
import com.gantenx.raffles.service.RuleStatusCache;
import com.gantenx.raffles.sink.adapter.SinkAdapter;
import com.gantenx.raffles.source.adapter.SourceAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RuleSubmitter {

    @Resource
    private RuleStatusCache ruleStatusCache;
    @Resource
    private FlinkSubmitter flinkSubmitter;
    @Resource
    private RuleService ruleService;
    @Autowired
    ConfigManager configManager;


    private final Map<Category, SourceAdapter> sourceMap = new HashMap<>();
    private final Map<Category, SinkAdapter> sinkMap = new HashMap<>();

    @Autowired
    private void sourceAndSinkServices(Set<SourceAdapter> sourcesSet, Set<SinkAdapter> sinksSet) {
        Map<DataType, SourceAdapter> sources = this.buildServiceMap(sourcesSet, SourceAdapter::getDataType);
        Map<DataType, SinkAdapter> sinks = this.buildServiceMap(sinksSet, SinkAdapter::getDataType);

        for (Category category : Category.values()) {
            CategoryConfig categoryConfig = configManager.getCategoryConfig(category);
            if (categoryConfig == null) {
                log.warn("No configuration found for category: {}", category);
                continue;
            }

            if (categoryConfig.getSourceConfig() == null || categoryConfig.getSinkConfig() == null) {
                log.warn("Incomplete configuration for category: {}", category);
                continue;
            }

            DataType sourceType = categoryConfig.getSourceConfig().getDataType();
            DataType sinkType = categoryConfig.getSinkConfig().getDataType();

            Optional.ofNullable(sources.get(sourceType)).ifPresent(source -> sourceMap.put(category, source));
            Optional.ofNullable(sinks.get(sinkType)).ifPresent(sink -> sinkMap.put(category, sink));
        }
    }

    private <T, K> Map<K, T> buildServiceMap(Set<T> services, Function<T, K> keyExtractor) {
        return services.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
    }

    /**
     * 清理孤立任务：检查 Flink 集群中运行的任务是否在数据库规则中存在，不存在则取消
     * 每10秒执行一次
     */
    @Scheduled(fixedDelay = 10_000)
    private void cleanupJobs() {
        Set<String> validRules = ruleService.getRules().stream().map(FlinkRule::getName).collect(Collectors.toSet());
        flinkSubmitter.getActiveJobs().stream().filter(job -> !validRules.contains(job.getJobName())).forEach(job -> {
            log.info("Found orphaned job, cancelling - name: {}, id: {}", job.getJobName(), job.getJobId());
            flinkSubmitter.cancelJob(job.getJobId());
        });
    }

    public void submit(Category category) {
        // 获取当前类别的所有规则
        List<FlinkRule> rules = ruleService.getRules(category);
        // 获取当前正在运行的任务名称列表
        Set<String> actives =
                flinkSubmitter.getActiveJobs().stream().map(JobStatusMessage::getJobName).collect(Collectors.toSet());;
        log.info("Active jobs: {}", actives);
        for (FlinkRule rule : rules) {
            // 批处理任务无需关心规则是否变更或者在运行中，每次都从头运行
            // 任务没有在运行中，需要提交。或者
            // 在规则变更时提交，避免重复提交
            if (category.isBatch() || !actives.contains(rule.getName()) || ruleService.isDuplicateRule(rule)) {
                this.submitRule(rule);
            }
        }
    }

    /**
     * 提交单个任务
     * 1. 取消正在执行的任务
     * 2. 提交任务
     * 3. 更新缓存
     */
    private void submitRule(FlinkRule rule) {
        try {
            log.info("Submitting rule: {}", rule.getName());

            String savepoint = this.cancel(rule.getName());
            Category category = rule.getCategory();

            SinkAdapter sink = sinkMap.get(category);
            SourceAdapter source = sourceMap.get(category);
            if (sink == null || source == null) {
                log.error("No sink or source configured for category: {}", category);
                return;
            }
            boolean success = flinkSubmitter.submit(rule, savepoint, sink, source);
            if (success) {
                ruleStatusCache.updateRuleState(rule);
                log.info("Successfully submitted rule: {}", rule.getName());
            } else {
                log.error("Failed to submit rule: {}", rule.getName());
            }
        } catch (Exception e) {
            log.error("Error submitting rule: {}", rule.getName(), e);
        }
    }

    /**
     * 按照 ruleName 取消任务，并返回 savepoint
     * RUNNING 状态：取消并保存 savepoint
     * 其他状态：直接取消
     */
    private String cancel(String ruleName) {
        List<JobStatusMessage> matchingJobs = flinkSubmitter.getActiveJobs().stream()
                .filter(job -> job.getJobName().equals(ruleName)).collect(Collectors.toList());

        if (matchingJobs.isEmpty()) {
            log.debug("No active jobs found for rule: {}", ruleName);
            return null;
        }

        log.info("Found {} active job(s) for rule: {}", matchingJobs.size(), ruleName);
        String lastSavepoint = null;

        for (JobStatusMessage job : matchingJobs) {
            JobID jobId = job.getJobId();
            JobStatus jobState = job.getJobState();

            log.info("Cancelling job: {}, state: {}", jobId, jobState);

            if (JobStatus.RUNNING.equals(jobState)) {
                lastSavepoint = flinkSubmitter.cancelJobWithSavepoint(job.getJobName(), jobId);
            } else {
                flinkSubmitter.cancelJob(jobId);
            }
        }

        return lastSavepoint;
    }
}
