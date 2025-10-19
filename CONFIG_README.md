# Raffles 配置说明

## 配置文件结构

本项目的配置主要分为以下几个部分：

### 1. 数据库配置
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/raffles?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: password
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

### 2. MyBatis 配置
```yaml
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.gantenx.raffles.model.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    lazy-loading-enabled: false
    aggressive-lazy-loading: false
```

### 3. Flink 配置

已添加完整的 Flink 配置支持，包括：

- **基础连接配置**：host、port
- **Savepoint 配置**：savepoint-path
- **性能配置**：checkpoint-interval、parallelism
- **重启策略**：max-failures、failure-rate-interval-minutes、delay-minutes
- **内存配置**：jobmanager、taskmanager

```yaml
flink:
  host: localhost
  port: 8081
  savepoint-path: /tmp/flink-savepoints
  checkpoint-interval: 600000
  parallelism: 1
  restart-strategy:
    max-failures: 5
    failure-rate-interval-minutes: 15
    delay-minutes: 2
  memory:
    jobmanager: 1g
    taskmanager: 2g
```

### 4. 业务配置（可选）

在注释中提供了业务配置的示例，可以根据需要启用：

```yaml
raffles:
  biz:
    configs:
      openaccount:
        enable: true
        batch: false
        category: openaccount
        source-config:
          type: kafka
          kafka:
            servers: localhost:9092
            topic: open-account-input
        sink-config:
          type: kafka
          kafka:
            servers: localhost:9092
            topic: open-account-output
```

## 配置类说明

### FlinkConfig
- 位置：`com.gantenx.raffles.config.FlinkConfig`
- 注解：`@ConfigurationProperties(prefix = "flink")`
- 功能：自动绑定 application.yaml 中的 flink 配置

### BizConfigManager
- 位置：`com.gantenx.raffles.biz.BizConfigManager`
- 功能：管理业务配置，提供静态方法访问各种业务配置

## 使用方式

### 1. 注入 FlinkConfig
```java
@Autowired
private FlinkConfig flinkConfig;

// 使用配置
String host = flinkConfig.getHost();
int port = flinkConfig.getPort();
```

### 2. 使用 BizConfigManager
```java
// 获取业务配置
BizConfig.SourceConfig sourceConfig = BizConfigManager.getSourceConfig(BizType.OPEN_ACCOUNT);
boolean isBatch = BizConfigManager.isBatch(BizType.DORMANT);
```

## 环境变量支持

可以通过环境变量覆盖配置：

```bash
export FLINK_HOST=production-flink-host
export FLINK_PORT=8081
export SPRING_DATASOURCE_URL=jdbc:mysql://prod-db:3306/raffles
```

## 配置验证

项目启动时会自动验证配置的正确性，如果配置有误会在日志中显示详细的错误信息。