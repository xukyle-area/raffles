package com.gantenx.raffles.util;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SQLTableExtractor {

    private static final Pattern WITH_PATTERN =
            Pattern.compile("WITH\\s+([a-zA-Z0-9_]+)\\s+AS|\\),\\s*([a-zA-Z0-9_]+)\\s+AS");
    private static final Pattern TABLE_PATTERN = Pattern.compile("(FROM|JOIN)\\s+([a-zA-Z0-9_]+)");

    public static Set<String> extractExternalTables(String sqlQuery) {
        Set<String> allTables = new HashSet<>(16);
        Set<String> withTables = new HashSet<>(16);

        Matcher withMatcher = WITH_PATTERN.matcher(sqlQuery);
        Matcher tableMatcher = TABLE_PATTERN.matcher(sqlQuery);

        while (withMatcher.find()) {
            if (withMatcher.group(1) != null) {
                withTables.add(withMatcher.group(1));
            } else if (withMatcher.group(2) != null) {
                withTables.add(withMatcher.group(2));
            }
        }

        while (tableMatcher.find()) {
            String tableName = tableMatcher.group(2);
            if (!withTables.contains(tableName)) {
                allTables.add(tableName);
            }
        }

        return allTables;
    }
}
