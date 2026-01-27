package com.gantenx.raffles.sink.builder;

import java.util.Map;
import com.gantenx.raffles.model.FlinkRule;

public abstract class AbstractSinkBuilder {
    public abstract Object buildSinkObject(Map<String, Object> item, FlinkRule rule);
}
