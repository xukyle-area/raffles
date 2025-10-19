package com.gantenx.raffles.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Map;

public class GsonUtils {

    private static final Gson GSON = new Gson();

    public static String toJson(Object src) {
        return GSON.toJson(src);
    }

    public static <T> T fromJson(String json, Type typeOfT) throws JsonSyntaxException {
        return GSON.fromJson(json, typeOfT);
    }

    public static Map<String, Object> toMap(String json) {
        return GsonUtils.fromJson(json, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}