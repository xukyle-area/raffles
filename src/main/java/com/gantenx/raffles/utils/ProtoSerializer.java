package com.gantenx.raffles.utils;

import org.apache.kafka.common.serialization.Serializer;
import com.google.protobuf.GeneratedMessageV3;

public class ProtoSerializer<T extends GeneratedMessageV3> implements Serializer<T> {
    @Override
    public byte[] serialize(String topic, T data) {
        return data.toByteArray();
    }
}
