package com.gantenx.raffles.sink;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import com.gantenx.raffles.utils.GsonUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Slf4j
public class KafkaSender implements Serializable {
    private static final long serialVersionUID = 1097336535112059778L;
    protected transient volatile KafkaProducer<String, String> producer;
    private final String topic;
    private final String servers;

    public KafkaSender(String servers, String topic) {
        this.servers = servers;
        this.topic = topic;
    }

    public void send(Object record) {
        try {
            String msg = GsonUtils.toJson(record);
            log.info("send record: {}", record.toString());
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, msg);
            Callback callback = (metadata, e) -> log.info("send record exception: {}", Objects.isNull(e) ? "null" : e.getMessage());
            KafkaProducer<String, String> producer = this.getKafkaProducer();
            producer.send(producerRecord, callback);
        } catch (Exception e) {
            log.error("send record fail: {}", record.toString(), e);
        }
    }

    protected KafkaProducer<String, String> getKafkaProducer() {
        if (null == producer) {
            synchronized (this) {
                if (null == producer) {
                    producer = buildProducer(servers);
                }
            }
        }
        return producer;
    }

    private KafkaProducer<String, String> buildProducer(String servers) {
        Map<String, Object> producerConfig = new HashMap<>();
        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerConfig.put(ProducerConfig.ACKS_CONFIG, "1");
        String clientId = "producer-sink" + new Random().nextInt(100);
        producerConfig.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        return new KafkaProducer<>(producerConfig);
    }
}

