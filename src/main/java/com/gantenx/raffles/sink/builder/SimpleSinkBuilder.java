package com.gantenx.raffles.sink.builder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Supplier;
import com.gantenx.raffles.model.FlinkRule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleSinkBuilder<T> extends AbstractSinkBuilder implements Serializable {

    private final Supplier<T> instanceSupplier;

    public SimpleSinkBuilder(Supplier<T> instanceSupplier) {
        this.instanceSupplier = instanceSupplier;
    }

    @Override
    public Object buildSinkObject(Map<String, Object> item, FlinkRule rule) {
        T output = instanceSupplier.get();
        // Dynamically set fields from map to object using reflection
        for (Map.Entry<String, Object> entry : item.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            try {
                Field field = output.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(output, value);
            } catch (NoSuchFieldException e) {
                log.warn("Field '{}' not found in class {}", fieldName, output.getClass().getSimpleName());
            } catch (IllegalAccessException e) {
                log.error("Cannot access field '{}' in class {}", fieldName, output.getClass().getSimpleName(), e);
            }
        }
        return output;
    }
}
