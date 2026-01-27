package com.gantenx.raffles.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.Category;
import lombok.extern.slf4j.Slf4j;

/**
 * 定时提交 Flink 任务
 */
@Slf4j
@Service
public class FlinkScheduler {

    @Autowired
    private RuleSubmitter ruleSubmitter;

    @Scheduled(cron = "0 0/3 * * * ?")
    public void submitCalculate() {
        log.info("Starting scheduled Flink job submission...");
        ruleSubmitter.submit(Category.CALCULATE);
    }
}
