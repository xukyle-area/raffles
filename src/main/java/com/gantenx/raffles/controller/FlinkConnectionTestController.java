package com.gantenx.raffles.controller;

import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.gantenx.raffles.config.FlinkConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class FlinkConnectionTestController {

    @Autowired
    private FlinkConfig flinkConfig;

    @GetMapping("/flink/test-connection")
    public ResponseEntity<Map<String, Object>> testFlinkConnection() {
        Map<String, Object> result = new HashMap<>();

        // 检查配置
        result.put("flinkHost", flinkConfig.getHost());
        result.put("flinkRestPort", flinkConfig.getRestPort());
        result.put("flinkRpcPort", flinkConfig.getRpcPort());

        // 检查TCP连接 - JobManager RPC端口 (6123)
        boolean rpcConnectable = testTcpConnection(flinkConfig.getHost(), flinkConfig.getRpcPort());
        result.put("rpcPortAccessible", rpcConnectable);

        // 检查TCP连接 - REST端口
        boolean restConnectable = testTcpConnection(flinkConfig.getHost(), flinkConfig.getRestPort());
        result.put("restPortAccessible", restConnectable);

        // 检查Flink REST API
        String[] endpoints = {"/overview", "/v1/overview", "/config", "/v1/config"};
        Map<String, String> apiResults = new HashMap<>();

        for (String endpoint : endpoints) {
            String apiResponse = testRestEndpoint(flinkConfig.getHost(), flinkConfig.getRestPort(), endpoint);
            apiResults.put(endpoint, apiResponse);
        }
        result.put("restApiTests", apiResults);

        // 总体状态
        boolean overallHealthy = rpcConnectable && restConnectable;
        result.put("overallHealthy", overallHealthy);
        result.put("recommendation", getRecommendation(rpcConnectable, restConnectable));

        return ResponseEntity.ok(result);
    }

    private boolean testTcpConnection(String host, int port) {
        try (Socket socket = new Socket(host, port)) {
            return true;
        } catch (Exception e) {
            log.debug("TCP connection failed to {}:{} - {}", host, port, e.getMessage());
            return false;
        }
    }

    private String testRestEndpoint(String host, int port, String endpoint) {
        try {
            URL url = new URL("http://" + host + ":" + port + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(3000);

            int responseCode = conn.getResponseCode();
            return "HTTP " + responseCode;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String getRecommendation(boolean rpcOk, boolean restOk) {
        if (!rpcOk && !restOk) {
            return "Flink集群完全不可访问。请检查：1) Flink是否运行 2) 端口转发是否正确设置 3) 防火墙设置";
        } else if (!rpcOk) {
            return "RPC端口(6123)不可访问。检查JobManager是否运行或端口转发设置";
        } else if (!restOk) {
            return "REST端口不可访问。检查Flink WebUI端口转发设置";
        } else {
            return "连接正常。如果仍有问题，可能是Flink集群内部配置问题";
        }
    }
}
