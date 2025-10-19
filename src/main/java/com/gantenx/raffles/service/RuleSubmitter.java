package com.gantenx.raffles.service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.JobStatus;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.streaming.api.environment.RemoteStreamEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.util.function.TriConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.Category;
import com.gantenx.raffles.config.CategoryConfig;
import com.gantenx.raffles.config.CategoryConfig.DataTypeConfig;
import com.gantenx.raffles.config.ConfigManager;
import com.gantenx.raffles.config.consists.DataType;
import com.gantenx.raffles.config.consists.Direction;
import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.sink.SinkService;
import com.gantenx.raffles.source.SourceService;
import com.gantenx.raffles.utils.GsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RuleSubmitter {
    @Resource
    private RuleStatusService ruleStatusService;
    @Resource
    private FlinkSubmitter flinkSubmitter;
    @Resource
    private RuleService ruleService;

    private final Map<Category, TriConsumer<RemoteStreamEnvironment, StreamTableEnvironment, RuleFlinkSql>> sourceMap =
            new HashMap<>();
    private final Map<Category, TriConsumer<StreamTableEnvironment, Table, RuleFlinkSql>> sinkMap = new HashMap<>();

    @Autowired
    public void sourceAndSinkServices(Set<SourceService> sourcesSet, Set<SinkService> sinksSet) {
        Map<DataType, SourceService> sources = this.buildServiceMap(sourcesSet, SourceService::getDataType);
        Map<DataType, SinkService> sinks = this.buildServiceMap(sinksSet, SinkService::getDataType);

        for (Category category : Category.values()) {
            CategoryConfig categoryConfig = ConfigManager.getCategoryConfig(category);
            DataTypeConfig sourceConfig = categoryConfig.getSourceConfig();
            DataTypeConfig sinkConfig = categoryConfig.getSinkConfig();

            Map<Direction, DataType> map = new HashMap<>();
            map.put(Direction.SOURCE, sourceConfig.getDataType());
            map.put(Direction.SINK, sinkConfig.getDataType());

            Optional.ofNullable(sources.get(map.get(Direction.SOURCE)))
                    .ifPresent(o -> this.sourceMap.put(category, o::source));
            Optional.ofNullable(sinks.get(map.get(Direction.SINK))).ifPresent(o -> this.sinkMap.put(category, o::sink));
        }
    }

    private <T, K> Map<K, T> buildServiceMap(Set<T> services, Function<T, K> keyExtractor) {
        return services.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
    }


    /**
     * 检查flink任务是否存在，不存在则取消
     */
    public void checkAndCancelJobs() {
        List<RuleFlinkSql> allRules = ruleService.getRules();
        Set<String> flinkCode = allRules.stream().map(RuleFlinkSql::getName).collect(Collectors.toSet());
        List<JobStatusMessage> activeJobs = flinkSubmitter.getActiveJobs();
        for (JobStatusMessage activeJob : activeJobs) {
            String jobName = activeJob.getJobName();
            JobID jobId = activeJob.getJobId();
            if (!flinkCode.contains(jobName)) {
                log.info("cancel job, job name:{}, job id:{}", jobName, jobId);
                flinkSubmitter.cancelJob(jobId);
            }
        }
    }

    /**
     * 按照业务提交规则
     *
     * @param bizType 业务类型的枚举
     */
    public void submitRules(Category category) {
        CategoryConfig categoryConfig = ConfigManager.getCategoryConfig(category);;


        boolean batch = categoryConfig.isBatch();
        log.info("submitRules, bizType:{}, category:{}", category.getName(), category);

        Set<String> activeNames = flinkSubmitter.getActiveJobNames();

        List<RuleFlinkSql> rules = ruleService.getRules();

        rules.stream().filter(rule -> category.equals(rule.getCategory()))
                .filter(rule -> batch || !activeNames.contains(rule.getName()) || !ruleService.isDuplicateRule(rule))
                .peek(rule -> log.info("after filter, final submit rule:{}", GsonUtils.toJson(rule)))
                .forEach(this::submitSingleRule);
    }

    /**
     * 提交单个任务
     * 1. 取消正在执行的任务
     * 2. 提交任务
     * 3. 更新缓存
     */
    public void submitSingleRule(RuleFlinkSql rule) {
        log.info("submitSingleRule, rule:{}", rule);
        String ruleCode = rule.getName();
        String savepoint = this.cancelJobs(ruleCode);
        Object category = rule.getCategory();
        TriConsumer<StreamTableEnvironment, Table, RuleFlinkSql> sink = sinkMap.get(category);
        TriConsumer<RemoteStreamEnvironment, StreamTableEnvironment, RuleFlinkSql> sources = sourceMap.get(category);

        boolean success = flinkSubmitter.submitJob(rule, savepoint, sink, sources);
        if (success) {
            log.info("rule submit success, ruleCode:{}", ruleCode);
            this.updateRuleStatus(rule);
        }
    }

    /**
     * 按照 code 取消任务，并返回 savepoint
     * 1. 状态正常的任务：取消并返回这个任务的 savepoint
     * 2. 状态异常的任务：直接取消
     */
    private String cancelJobs(String code) {
        // 筛选出所有与当前 code 匹配的任务
        List<JobStatusMessage> jobs = flinkSubmitter.getActiveJobs().stream()
                .filter(job -> job.getJobName().equals(code)).collect(Collectors.toList());

        String savepoint = null;
        for (JobStatusMessage job : jobs) {
            log.info("cancel job, job name:{}, jobId: {}, jobState: {}", job.getJobName(), job.getJobId(),
                    job.getJobState());
            JobID jobId = job.getJobId();
            if (JobStatus.RUNNING.equals(job.getJobState())) {
                savepoint = flinkSubmitter.cancelJobWithSavepoint(job.getJobName(), jobId);
            } else {
                flinkSubmitter.cancelJob(jobId);
            }
            log.info("cancel job success, jobId: {}", jobId);
        }
        // 返回任务的 savepoint
        return savepoint;
    }

    /**
     * 在任务提交完成后，缓存这些任务的：
     * 1. version
     * 2. expression，也就是可执行的 sql 语句
     * 上面字段变动之后，任务会被取消，再重新提交
     */
    private void updateRuleStatus(RuleFlinkSql rule) {
        String key = rule.getName();
        ruleStatusService.setLatestExpression(key, rule.getExecutableSql() + rule.getParams());
        ruleStatusService.setLatestVersion(key, rule.getVersion());
    }
}
