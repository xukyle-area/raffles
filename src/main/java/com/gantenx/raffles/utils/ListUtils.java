package com.gantenx.raffles.utils;

import java.util.List;

public class ListUtils {
    public static <T> T firstOrNull(List<T> list) {
        return list.size() > 0 ? list.get(0) : null;
    }
}
