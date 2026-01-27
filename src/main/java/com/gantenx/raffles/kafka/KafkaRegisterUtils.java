package com.gantenx.raffles.kafka;

import static org.apache.flink.table.api.Expressions.$;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Duration;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.formats.json.JsonRowDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.ApiExpression;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import com.gantenx.raffles.utils.FlinkTypeUtils;


@SuppressWarnings("deprecation")
public class KafkaRegisterUtils implements Serializable {

    private static final long serialVersionUID = 3600595263156225931L;

    public static void registerKafkaTable(StreamExecutionEnvironment env, StreamTableEnvironment tableEnv,
            KafkaTableSource schema, String groupId) {
        RowTypeInfo rowTypeInfo = FlinkTypeUtils.rowTypeInfoFromClazz(schema.getClazz());
        JsonRowDeserializationSchema jsonRowDeserializationSchema =
                (new JsonRowDeserializationSchema.Builder(rowTypeInfo)).ignoreParseErrors().build();
        KafkaSource<Row> kafkaSource = KafkaSource.<Row>builder().setBootstrapServers(schema.getServers())
                .setTopics(schema.getTopic()).setGroupId(groupId)
                .setStartingOffsets(OffsetsInitializer.committedOffsets(OffsetResetStrategy.LATEST))
                .setDeserializer(KafkaRecordDeserializationSchema.valueOnly(jsonRowDeserializationSchema)).build();
        String timeAttributeField = schema.getTimeField();
        WatermarkStrategy<Row> watermarkStrategy = WatermarkStrategy
                .forBoundedOutOfOrderness(Duration.ofDays(ObjectUtils.defaultIfNull(schema.getMaxDelay(), 0)));
        watermarkStrategy = watermarkStrategy.withIdleness(Duration.ofMinutes(1));
        if (StringUtils.isNotBlank(timeAttributeField)) {
            watermarkStrategy = watermarkStrategy.withTimestampAssigner(
                    // 集群上lambda表达式会报错
                    new SerializableTimestampAssigner<Row>() {
                        @Override
                        public long extractTimestamp(Row element, long recordTimestamp) {
                            Object object = element.getField(rowTypeInfo.getFieldIndex(timeAttributeField));
                            // 时间属性不能为空，默认值设置为1970-01-01 00:00:00
                            Long mills = 0L;
                            if (object instanceof Timestamp) {
                                mills = ((Timestamp) object).getTime();
                            } else if (object instanceof Long) {
                                mills = (Long) object;
                            }
                            // flink stream转table时RowTime会强制转到UTC时区，手动恢复毫秒数
                            return mills;
                        }
                    });
        }

        DataStream<Row> sourceStream = env.fromSource(kafkaSource, watermarkStrategy, schema.getTable());
        ApiExpression[] apiExpressions = new ApiExpression[rowTypeInfo.getFieldNames().length];
        for (int i = 0; i < rowTypeInfo.getFieldNames().length; i++) {
            ApiExpression expression = $(rowTypeInfo.getFieldNames()[i]);
            if (StringUtils.equals(rowTypeInfo.getFieldNames()[i], timeAttributeField)) {
                expression = expression.rowtime();
            }
            apiExpressions[i] = expression;
        }
        tableEnv.createTemporaryView(schema.getTable(), sourceStream, apiExpressions);
    }

}
