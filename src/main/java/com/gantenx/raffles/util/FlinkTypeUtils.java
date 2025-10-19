package com.gantenx.raffles.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.types.Row;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FlinkTypeUtils {
    public static RowTypeInfo rowTypeInfoFromClazz(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<String> names = new ArrayList<>();
        List<TypeInformation<?>> typeInformations = new ArrayList<>();
        for (Field field : fields) {
            names.add(field.getName());
            TypeInformation<?> typeInfo = TypeInformation.of(field.getType());
            typeInformations.add(typeInfo);
        }

        String[] nameArray = Iterables.toArray(names, String.class);
        TypeInformation<?>[] typeInfoArray = Iterables.toArray(typeInformations, TypeInformation.class);

        return new RowTypeInfo(typeInfoArray, nameArray);
    }

    public static Map<String, Object> buildRowValueMap(Row row) {
        Map<String, Object> rowMap = Maps.newHashMap();
        Set<String> fieldNames = row.getFieldNames(true);
        if (fieldNames == null) {
            return rowMap;
        }
        for (String fieldName : fieldNames) {
            Object value = row.getField(fieldName);
            if (value instanceof Timestamp) {
                rowMap.put(fieldName, ((Timestamp) value).getTime());
            } else if (value instanceof LocalDateTime) {
                rowMap.put(fieldName, Timestamp.valueOf(((LocalDateTime) value)).getTime());
            } else if (value instanceof LocalDate) {
                rowMap.put(fieldName, Timestamp.valueOf(((LocalDate) value).atTime(0, 0, 0)).getTime());
            } else {
                rowMap.put(fieldName, value);
            }
        }

        return rowMap;
    }
}
