package com.gantenx.raffles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.gantenx.raffles"})
public class RafflesApplication {
    public static void main(String[] args) {
        SpringApplication.run(RafflesApplication.class, args);
    }
}
