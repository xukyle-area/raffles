package com.gantenx.raffles.source;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KafkaTableSource {
    private String table;
    private Class<?> clazz;
    private String topic;
    private String startupMode;
    private String servers;
    private Integer maxDelay;
    private String timeField;
}
