package com.gantenx.raffles.biz.ongoingcdd;

import java.io.Serializable;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import com.gantenx.raffles.model.RuleFlinkSql;
import com.gantenx.raffles.sink.SinkBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OngoingCddSinkBuilder implements SinkBuilder, Serializable {

    @Override
    public Object buildSinkObject(Map<String, Object> item, RuleFlinkSql rule) {
        OngoingCddOutput ongoingCddOutput = new OngoingCddOutput();
        String needCdd = MapUtils.getString(item, "need_cdd");
        String customerId = MapUtils.getString(item, "customer_id");
        String userId = MapUtils.getString(item, "user_id");
        String gapDay = MapUtils.getString(item, "gap_day");
        String daysThreshold = MapUtils.getString(item, "days_threshold");
        ongoingCddOutput.setCustomerId(customerId);
        ongoingCddOutput.setUserId(userId);
        ongoingCddOutput.setGapDay(gapDay);
        ongoingCddOutput.setDaysThreshold(daysThreshold);
        ongoingCddOutput.setNeedCdd(needCdd);
        return ongoingCddOutput;
    }
}
