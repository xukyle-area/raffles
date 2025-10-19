package com.gantenx.raffles.biz;

import lombok.Data;

@Data
public class FlinkConfig {
    private String savepointPath;
    private String host;
    private int port;
}
