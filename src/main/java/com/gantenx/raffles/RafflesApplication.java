package com.gantenx.raffles;

import org.mybatis.spring.annotation.MapperScan;
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
@MapperScan("com.gantenx.raffles.model.mapper")
public class RafflesApplication {
    public static void main(String[] args) {
        SpringApplication.run(RafflesApplication.class, args);
    }
}
