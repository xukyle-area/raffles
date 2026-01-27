package com.gantenx.raffles.sink.function;

import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.types.Row;
import org.apache.flink.types.RowKind;
import com.gantenx.raffles.kafka.KafkaSender;
import com.gantenx.raffles.model.FlinkRule;
import com.gantenx.raffles.sink.mapper.RowToObjectMapper;
import com.gantenx.raffles.utils.FlinkTypeUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka Sink Function实现
 * 将Flink流数据写入Kafka
 */
@Slf4j
public class KafkaSinkFunction implements SinkFunction<Tuple2<Boolean, Row>> {
    private static final long serialVersionUID = 1243590888337448708L;
    private static final String TRANSACTION_SINK_ROW_KEY = "retractKey";
    private static final String IS_TO_UPDATE = "isToUpdate";

    private final KafkaSender kafkaSender;
    private final FlinkRule ruleFlinkSql;
    private final RowToObjectMapper rowMapper;

    public KafkaSinkFunction(FlinkRule rule, RowToObjectMapper rowMapper, String servers, String topic) {
        this.ruleFlinkSql = rule;
        this.rowMapper = rowMapper;
        this.kafkaSender = new KafkaSender(servers, topic);
    }

    @Override
    public void invoke(Tuple2<Boolean, Row> row, Context context) {
        try {
            Map<String, Object> rowMap = FlinkTypeUtils.buildRowValueMap(row.f1);
            if (MapUtils.isEmpty(rowMap)) {
                log.info("receive empty row");
                return;
            }
            if (rowMap.containsKey(TRANSACTION_SINK_ROW_KEY)) {
                rowMap.put(TRANSACTION_SINK_ROW_KEY, concatTransactionKey(rowMap));
            }
            if (row.f1.getKind().equals(RowKind.INSERT)) {
                rowMap.put(IS_TO_UPDATE, false);
            } else if (row.f1.getKind().equals(RowKind.UPDATE_AFTER)) {
                rowMap.put(IS_TO_UPDATE, true);
            } else {
                return;
            }
            Object record = rowMapper.buildObject(rowMap, ruleFlinkSql);
            kafkaSender.send(record);
        } catch (Exception e) {
            log.error("invoke {}", e.getMessage(), e);
        }
    }

    private String concatTransactionKey(Map<String, Object> rowMap) {
        return String.join("_", ruleFlinkSql.getName(),
                rowMap.getOrDefault(TRANSACTION_SINK_ROW_KEY, StringUtils.EMPTY).toString());
    }
}
