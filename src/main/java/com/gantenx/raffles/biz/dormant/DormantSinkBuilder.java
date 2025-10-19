package com.gantenx.raffles.biz.dormant;

import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.sink.SinkBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.io.Serializable;
import java.util.Map;

@Slf4j
public class DormantSinkBuilder implements SinkBuilder, Serializable {

    @Override
    public Object buildSinkObject(Map<String, Object> item, RuleFlinkSql rule) {
        DormantOutput dormantOutput = new DormantOutput();
        String event = MapUtils.getString(item, "event");
        String account = MapUtils.getString(item, "account");
        String eventTime = MapUtils.getString(item, "event_time");
        dormantOutput.setAccount(account);
        dormantOutput.setEvent(event);
        dormantOutput.setEventTime(eventTime);
        return dormantOutput;
    }
}
