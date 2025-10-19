package com.gantenx.raffles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.gantenx.raffles.config.CategoryConfigProperties;
import com.gantenx.raffles.config.FlinkConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.gantenx.raffles"})
@EnableConfigurationProperties({CategoryConfigProperties.class, FlinkConfig.class})
public class RafflesApplication {
    public static void main(String[] args) {
        SpringApplication.run(RafflesApplication.class, args);
    }
}
