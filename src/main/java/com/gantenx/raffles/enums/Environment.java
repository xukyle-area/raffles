package com.gantenx.raffles.enums;

import lombok.Getter;

@Getter
public enum Environment {
    AWS1("aws1", "", ""),
    AWS2("aws2", "", ""),
    SANDBOX("sandbox", "", "");

    private final String value;
    private final String kafkaBootstrapServers;
    private final String redisCluster;

    Environment(String value, String kafkaBootstrapServers, String redisCluster) {
        this.value = value;
        this.kafkaBootstrapServers = kafkaBootstrapServers;
        this.redisCluster = redisCluster;
    }

    public static Environment fromValue(String value) {
        for (Environment environment : Environment.values()) {
            if (environment.value.equalsIgnoreCase(value)) {
                return environment;
            }
        }
        throw new IllegalArgumentException("Unknown environment: " + value);
    }
}
