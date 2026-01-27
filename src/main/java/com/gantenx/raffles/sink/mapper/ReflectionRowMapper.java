package com.gantenx.raffles.sink.mapper;

import java.lang.reflect.Field;
import java.util.Map;
import com.gantenx.raffles.model.FlinkRule;
import lombok.extern.slf4j.Slf4j;

/**
 * 基于反射的通用行数据映射器
 * 通过反射动态设置对象字段值
 */
@Slf4j
public class ReflectionRowMapper<T> implements RowToObjectMapper {
    private static final long serialVersionUID = 1L;

    private final Class<T> outputClass;

    public ReflectionRowMapper(Class<T> outputClass) {
        this.outputClass = outputClass;
    }

    @Override
    public Object buildObject(Map<String, Object> item, FlinkRule rule) {
        try {
            T output = outputClass.getDeclaredConstructor().newInstance();
            // 动态设置字段值
            for (Map.Entry<String, Object> entry : item.entrySet()) {
                String fieldName = entry.getKey();
                Object value = entry.getValue();
                try {
                    Field field = outputClass.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(output, value);
                } catch (NoSuchFieldException e) {
                    log.warn("Field '{}' not found in class {}", fieldName, outputClass.getSimpleName());
                } catch (IllegalAccessException e) {
                    log.error("Cannot access field '{}' in class {}", fieldName, outputClass.getSimpleName(), e);
                }
            }
            return output;
        } catch (Exception e) {
            log.error("Failed to create instance of {}", outputClass.getSimpleName(), e);
            throw new RuntimeException("Failed to create output object", e);
        }
    }
}
