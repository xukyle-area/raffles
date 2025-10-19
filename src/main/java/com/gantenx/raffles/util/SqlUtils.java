package com.gantenx.raffles.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;


@Slf4j
public class SqlUtils {
    public static char SQL_PARAMETER_INDICATOR = ':';
    public static List<Character> SQL_DELIMITERS = Lists.newArrayList(' ', '\n', '\t', '\r', '\'', '"', ',', ')');

    public static String getExecutableSql(String expression, Map<String, Object> paramsMap) {
        if (StringUtils.isEmpty(expression) || !expression.contains(String.valueOf(SQL_PARAMETER_INDICATOR))) {
            return expression;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < expression.length(); ++i) {
            char c = expression.charAt(i);
            if (c != SQL_PARAMETER_INDICATOR) {
                sb.append(c);
                continue;
            }
            int j = i + 1;
            while (j < expression.length() && !SQL_DELIMITERS.contains(expression.charAt(j))) {
                j++;
            }

            String paramName = expression.substring(i + 1, j);
            Object value = MapUtils.getObject(paramsMap, paramName);
            if (value == null) {
                log.error("parameter {} doesn't exist", paramName);
                throw new RuntimeException(String.format("parameter %s doesn't exist", paramName));
            } else {
                sb.append(value);
            }
            i = j - 1;
        }

        return sb.toString();
    }
}
