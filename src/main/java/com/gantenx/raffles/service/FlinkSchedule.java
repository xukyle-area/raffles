package com.gantenx.raffles.service;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.gantenx.raffles.config.Category;
import com.gantenx.raffles.utils.ScheduledThreadPool;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FlinkSchedule {

    @Autowired
    private RuleSubmitter ruleSubmitter;

    @PostConstruct
    public void init() {
        ScheduledThreadPool.scheduleWithFixedDelay(() -> {
            try {
                ruleSubmitter.submit(Category.CALCULATE);
            } catch (Exception e) {
                log.error("Error submitting Flink jobs: ", e);
            }
        }, 120, 0, "FlinkSchedule");
    }
}
