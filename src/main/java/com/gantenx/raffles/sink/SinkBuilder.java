package com.gantenx.raffles.sink;

import java.util.Map;
import com.gantenx.raffles.model.FlinkRule;

public interface SinkBuilder {
    Object buildSinkObject(Map<String, Object> item, FlinkRule rule);
}
