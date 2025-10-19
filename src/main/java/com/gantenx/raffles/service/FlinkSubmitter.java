package com.gantenx.raffles.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.JobID;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.client.program.rest.RestClusterClient;
import org.apache.flink.configuration.*;
import org.apache.flink.core.execution.SavepointFormatType;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.client.JobStatusMessage;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointConfigOptions;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.messages.Acknowledge;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.environment.RemoteStreamEnvironment;
import org.apache.flink.streaming.api.graph.StreamGraph;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.util.function.TriConsumer;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.biz.FlinkConfig;
import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.util.FileListing;
import com.gantenx.raffles.util.ScheduledThreadPool;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class FlinkSubmitter {
    // 所需要的 jars 文件, 通过 dist.sh 将 resources/lib 下的文件打包到镜像中
    private final static List<String> JAR_FILES = FileListing.getPaths("/app/flink-lib");

    private final FlinkConfig flinkConfig = new FlinkConfig();

    private RestClusterClient<UUID> commonClusterClient;

    private List<JobStatusMessage> activeJobs;

    @PostConstruct
    public void init() throws Exception {
        this.initClient();
        ScheduledThreadPool.scheduleWithFixedDelay(this::keepAlive, 10, "flink-keep-alive");
    }

    /**
     * 定时任务方法，每10秒钟更新一次 activeJobs 字段
     * 同时检测 commonClusterClient 是否正常
     * 如果 commonClusterClient 异常，重新初始化
     */
    public void keepAlive() {
        try {
            activeJobs = commonClusterClient.listJobs().get(3, TimeUnit.SECONDS).stream()
                    .filter(job -> !job.getJobState().isGloballyTerminalState()).distinct()
                    .collect(Collectors.toList());
            activeJobs.stream().map(JobStatusMessage::getJobName).reduce((a, b) -> a + ", " + b)
                    .ifPresent(o -> log.info("active jobs: {}", o));
        } catch (Exception e) {
            log.error("Failed to list jobs: {}", e.getMessage());
            this.handleClusterClientError(e);
        }
    }

    public void initClient() throws Exception {
        RemoteStreamEnvironment remoteStreamEnvironment = this.buildRemoteStreamEnvironment(new Configuration());
        Configuration configuration = remoteStreamEnvironment.getClientConfiguration();
        commonClusterClient = new RestClusterClient<>(configuration, UUID.randomUUID());
    }

    /**
     * 获取当前正在运行的任务
     *
     * @return 当前正在运行的任务列表
     */
    public List<JobStatusMessage> getActiveJobs() {
        return this.activeJobs;
    }

    /**
     * 获取当前正在运行的任务的名称
     *
     * @return 当前正在运行的任务的名称
     */
    public Set<String> getActiveJobNames() {
        return this.getActiveJobs().stream().map(JobStatusMessage::getJobName).collect(Collectors.toSet());
    }

    /**
     * @param sql           运行在 flink 上的 sql
     * @param savepointPath savepoint 地址，可选
     * @param sink          自定义注册sink
     * @param sources       自定义注册数据源
     */
    public boolean submitJob(RuleFlinkSql sql, @Nullable String savepointPath,
            TriConsumer<StreamTableEnvironment, Table, RuleFlinkSql> sink,
            TriConsumer<RemoteStreamEnvironment, StreamTableEnvironment, RuleFlinkSql> sources) {
        Configuration config = this.buildConfiguration(sql.getName(), savepointPath);
        RemoteStreamEnvironment rse = this.buildRemoteStreamEnvironment(config);
        StreamTableEnvironment ste = StreamTableEnvironment.create(rse, EnvironmentSettings.newInstance().build());

        sources.accept(rse, ste, sql);
        Table table = ste.sqlQuery(sql.getExecutableSql());
        sink.accept(ste, table, sql);

        try (RestClusterClient<UUID> client = new RestClusterClient<>(config, UUID.randomUUID())) {
            CompletableFuture<JobID> completableFuture = client.submitJob(this.buildJobGraph(rse, config));
            JobID jobID;
            try {
                jobID = completableFuture.get(9, TimeUnit.MINUTES);
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                log.error("Failed to submit job: sql id: {}, sql code: {}", sql.getId(), sql.getName(), e);
                return false;
            }
            log.info("sql id: {}, sql code: {} submit success, jobID: {}", sql.getId(), sql.getName(), jobID);
            return true;
        } catch (Exception e) {
            log.error("Error while creating RestClusterClient: {}", e.getMessage(), e);
        }
        return false;
    }

    /**
     * 通过 jobId 强制停止某个任务
     */
    public String cancelJobWithSavepoint(String code, JobID jobId) {
        try {
            String savepointDirectory = flinkConfig.getSavepointPath() + "/" + code;
            CompletableFuture<String> completableFuture =
                    commonClusterClient.cancelWithSavepoint(jobId, savepointDirectory, SavepointFormatType.CANONICAL);
            String savepointPath = completableFuture.get(9, TimeUnit.MINUTES);
            log.info("cancel job with savepoint, jobId: {} success, savepointPath: {}", jobId, savepointPath);
            return savepointPath;
        } catch (Exception e) {
            log.error("cancel job with savepoint, jobId: {} error, message:{}", jobId, e.getMessage());
            this.cancelJob(jobId);
        }
        return StringUtils.EMPTY;
    }

    /**
     * 通过 jobId 强制停止某个任务
     */
    public void cancelJob(JobID jobId) {
        try {
            CompletableFuture<Acknowledge> cancel = commonClusterClient.cancel(jobId);
            cancel.get(9, TimeUnit.MINUTES);
            log.info("cancel job without savepoint, jobId: {} success", jobId);
        } catch (Exception e) {
            log.error("cancel job without savepoint, jobId: {} error, message:{}", jobId, e.getMessage());
        }
    }

    /**
     * 构建 flink 的配置
     *
     * @param ruleName      规则名称
     * @param savepointPath savepoint 的地址, s3 路径
     * @return 配置项
     */
    private Configuration buildConfiguration(String ruleName, @Nullable String savepointPath) {
        Configuration configuration = new Configuration();
        if (StringUtils.isNotBlank(savepointPath)) {
            configuration.set(SavepointConfigOptions.SAVEPOINT_PATH, savepointPath);
        }
        configuration.setString(JobManagerOptions.ADDRESS, flinkConfig.getHost());
        configuration.setInteger(JobManagerOptions.PORT, flinkConfig.getPort());
        configuration.setString(RestOptions.ADDRESS, flinkConfig.getHost());
        configuration.setInteger(RestOptions.PORT, flinkConfig.getPort());

        String jobId = JobID.generate().toString();
        configuration.setString(PipelineOptionsInternal.PIPELINE_FIXED_JOB_ID, jobId);
        configuration.setString(PipelineOptions.NAME, ruleName);
        configuration.set(PipelineOptions.JARS, JAR_FILES);
        return configuration;
    }

    /**
     * 通过配置项, 构建 flink 环境
     *
     * @param configuration flink 配置
     */
    private RemoteStreamEnvironment buildRemoteStreamEnvironment(Configuration configuration) {
        List<String> jars = configuration.getOptional(PipelineOptions.JARS).orElse(new ArrayList<>());
        String savepointPath =
                configuration.getOptional(SavepointConfigOptions.SAVEPOINT_PATH).orElse(StringUtils.EMPTY);

        SavepointRestoreSettings settings =
                StringUtils.isNotBlank(savepointPath) ? SavepointRestoreSettings.forPath(savepointPath, Boolean.FALSE)
                        : null;
        RemoteStreamEnvironment remoteStreamEnvironment = new RemoteStreamEnvironment(flinkConfig.getHost(),
                flinkConfig.getPort(), configuration, jars.toArray(new String[0]), new URL[] {}, settings);
        remoteStreamEnvironment.enableCheckpointing(1000L * 60L * 10L, CheckpointingMode.EXACTLY_ONCE);
        remoteStreamEnvironment
                .setRestartStrategy(RestartStrategies.failureRateRestart(5, Time.minutes(15), Time.minutes(2)));
        remoteStreamEnvironment.setParallelism(1);
        return remoteStreamEnvironment;
    }

    /**
     * 构建提交的 job
     *
     * @param remoteStreamEnvironment flink 环境
     * @param configuration           配置
     * @return 用于提交的 job graph
     */
    private JobGraph buildJobGraph(RemoteStreamEnvironment remoteStreamEnvironment, Configuration configuration) {
        String fixedJobID = configuration.getString(PipelineOptionsInternal.PIPELINE_FIXED_JOB_ID);
        StreamGraph streamGraph = remoteStreamEnvironment.getStreamGraph();
        JobGraph jobGraph = streamGraph.getJobGraph();
        jobGraph.setJobID(JobID.fromHexString(fixedJobID));
        List<String> jars = configuration.get(PipelineOptions.JARS);
        for (String jar : jars) {
            Path jarPath = new Path("file://" + jar);
            jobGraph.addJar(jarPath);
        }
        return jobGraph;
    }

    /**
     * 处理 flink 集群连接异常
     *
     * @param e 异常
     */
    private void handleClusterClientError(Exception e) {
        Throwable cause = e.getCause() != null ? e.getCause() : e;
        if (this.isRecoverableError(cause)) {
            log.info("recoverable connection error. try to reconnect to Flink cluster...");
            try {
                commonClusterClient.close();
                this.initClient();
                log.info("reconnection to Flink cluster successfully.");
            } catch (Exception reconnectException) {
                log.error("failed to reconnect to Flink cluster: {}", reconnectException.getMessage());
            }
        } else {
            // TODO 非可恢复的异常，需要告警
            log.error("Encountered non-recoverable error: {}", cause.getMessage());
        }
    }

    /**
     * 检查异常是否为可恢复的异常
     *
     * @param e 异常
     * @return 是否为可恢复的异常
     */
    private boolean isRecoverableError(Throwable e) {
        // 检查是否为连接关闭相关的异常，可以尝试恢复连接
        if (e instanceof java.net.SocketException || e instanceof java.net.SocketTimeoutException
                || e instanceof java.io.EOFException
                || (e instanceof org.apache.flink.runtime.rest.util.RestClientException
                        && e.getCause() instanceof java.net.SocketException)) {
            log.error("Detected connection issue: {}", e.getMessage());
            return true;
        }
        // 其他类型的异常不认为是可恢复的
        log.error("Detected non-recoverable error: {}", e.getMessage());
        return false;
    }
}
