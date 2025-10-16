package com.gantenx.phthonus.utils;

import java.util.concurrent.TimeUnit;
import lombok.Getter;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

@Getter
public final class HttpFactory {

    private static volatile HttpFactory instance;

    private final OkHttpClient sharedClient;

    private HttpFactory() {
        final Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequestsPerHost(500);
        dispatcher.setMaxRequests(500);
        sharedClient = new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .pingInterval(20, TimeUnit.SECONDS)
                .build();
    }

    public static HttpFactory getInstance() {
        if (instance == null) {
            synchronized (HttpFactory.class) {
                if (instance == null) {
                    instance = new HttpFactory();
                }
            }
        }
        return instance;
    }
}