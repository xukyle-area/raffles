package com.gantenx.raffles.utils;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.StringDeserializer;
import com.gantenx.raffles.constants.Constant;
import com.gantenx.raffles.constants.enums.Environment;
import lombok.extern.slf4j.Slf4j;

/**
 * Kafka消息查询工具
 * <p>
 * 依赖:
 * - org.apache.kafka:kafka-clients:3.4.0
 * - org.slf4j:slf4j-api:1.7.36
 * - org.slf4j:slf4j-simple:1.7.36
 */
@Slf4j
public class KafkaMessageViewer {

    private final static boolean FROM_BEGINNING = true;

    public static void main(String[] args) {
        PropertyUtils.initLogLevel();

        KafkaMessageViewer.viewKafkaMessages(Environment.AWS1.getKafkaBootstrapServers(), Constant.ONGOING_KAFKA_TOPIC);
    }

    public static void createTopicIfNotExists(String bootstrapServers, String topic, int numPartitions,
            short replicationFactor) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(props)) {
            NewTopic newTopic = new NewTopic(topic, numPartitions, replicationFactor);
            adminClient.createTopics(Collections.singletonList(newTopic)).all().get();
            log.info("成功创建 Kafka Topic: " + topic);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                log.info("Topic 已存在: " + topic);
            } else {
                throw new RuntimeException("创建 Topic 失败: " + topic, e);
            }
        } catch (Exception e) {
            throw new RuntimeException("创建 Topic 失败: " + topic, e);
        }
    }

    /**
     * 查询并显示Kafka消息
     */
    public static void viewKafkaMessages(String bootstrapServers, String topic) {
        String consumerGroup = "kafka-viewer-" + UUID.randomUUID();

        Properties props = buildProperties(bootstrapServers, consumerGroup);

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props)) {
            consumer.subscribe(Collections.singletonList(topic));
            AtomicInteger messageCount = new AtomicInteger(0);
            log.info("开始查询Kafka主题: " + topic);
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                if (records.isEmpty()) {
                    // 继续轮询新消息
                    continue;
                }

                for (ConsumerRecord<String, String> record : records) {
                    printRecord(record);
                    messageCount.incrementAndGet();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("查询Kafka消息失败", e);
        }
    }

    private static Properties buildProperties(String bootstrapServers, String consumerGroup) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, FROM_BEGINNING ? "earliest" : "latest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return props;
    }

    /**
     * 打印单条消息记录
     */
    private static void printRecord(ConsumerRecord<String, String> record) {
        log.info("----------------------------------------------");
        log.info("主题: {}", record.topic());
        log.info("分区: {}", record.partition());
        log.info("偏移量: {}", record.offset());
        log.info("时间戳: {} ({})", new Date(record.timestamp()), record.timestamp());
        if (record.key() != null) {
            log.info("键: {}", record.key());
        }
        log.info("值: {}", record.value());
    }
}
