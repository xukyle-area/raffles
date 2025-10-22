# 🚀 Raffles

Raffles 是一个基于 Flink 和 Spring Boot 的高可扩展流式处理平台，负责 Flink 任务与规则引擎系统的架构设计与实现，支持动态任务生成与规则配置化管理。

关键词: Flink、Spring Boot、配置化任务、规则引擎

## 技术实现与成果

- **多源数据接入架构**：实现流式与批量任务统一采集与高效处理，显著提升数据吞吐性能。
- **全链路配置化管理**：实现任务输入、逻辑与输出全链路配置化管理，支持业务方快速上线新任务与灵活扩展。
- **统一 REST API 接口体系**：支持任务触发、监控与自动化运维，提升系统运维效率与可靠性。
- **动态规则管理**：支持规则动态提交、在线编辑与热加载应用，满足高频变更与复杂实时决策场景需求。规则加载时间缩短 60%，任务上线周期由 1 天降至 1 小时。

## 技术栈

- **Flink**：流式处理框架。
- **Spring Boot**：应用程序框架。
- **Java**：核心语言。
- **Maven**：构建工具。
- **MyBatis**：数据库操作。
- **Docker**：容器化。
- **Kubernetes**：部署。
- **MySQL**：数据库。

## 先决条件

- Java 8 或更高版本
- Maven 3.x
- MySQL 8.x
- Docker（可选，用于容器化部署）

## 入门指南

### 克隆仓库

```bash
git clone <repository-url>
cd raffles
```

### 构建项目

使用 Maven 构建项目：

```bash
mvn clean install
```

这将编译代码、运行测试，并将应用程序打包成位于 `target/` 的 JAR 文件。

### 生成 MyBatis 代码

项目使用 MyBatis Generator 从数据库表创建实体、映射器和 XML 文件。

1. 确保您的 MySQL 数据库正在运行并已配置。
2. 使用您的数据库连接详细信息更新 `src/main/resources/generatorConfig.xml`。
3. 运行生成脚本：

```bash
./generate-mybatis.sh
```

或手动使用 Maven：

```bash
mvn mybatis-generator:generate
```

生成的文件将放置在：
- 实体：`src/main/java/com/gantenx/raffles/model/entity/`
- 映射器：`src/main/java/com/gantenx/raffles/model/mapper/`
- XML 映射：`src/main/resources/mapper/`

### 运行应用程序

构建后，运行 Spring Boot 应用程序：

```bash
java -jar target/raffles-1.0-SNAPSHOT.jar
```

或使用 Maven：

```bash
mvn spring-boot:run
```

应用程序将在默认端口（通常为 8080）上启动。检查 `src/main/resources/application.yaml` 以获取配置。

### Docker 构建

使用 Docker 构建和运行：

```bash
./build-docker.sh
```

这使用根目录中的 `dockerfile`。

### Kubernetes 部署

使用提供的脚本在 Kubernetes 中重启：

```bash
./restart-in-kubernetes.sh
```

## 配置

- **数据库**：在 `src/main/resources/generatorConfig.xml` 和 `application.yaml` 中配置。
- **Flink 任务**：配置化任务管理。
- **规则**：动态规则引擎。

## 项目结构

```
.
├── src/
│   ├── main/
│   │   ├── java/com/gantenx/raffles/
│   │   │   ├── config/          # 配置类
│   │   │   ├── model/           # 生成的实体和映射器
│   │   │   └── utils/           # 工具类，如 FileListing 和 FlinkTypeUtils
│   │   └── resources/
│   │       ├── generatorConfig.xml  # MyBatis 生成器配置
│   │       └── mapper/          # 生成的 XML 映射
│   └── test/                    # 测试源代码
├── target/                      # 构建输出
├── pom.xml                      # Maven 配置
├── dockerfile                   # Docker 构建文件
├── generate-mybatis.sh          # MyBatis 代码生成脚本
└── README.md                    # 此文件
```

## 贡献

1. Fork 仓库。
2. 创建功能分支。
3. 进行更改。
4. 运行测试并构建。
5. 提交拉取请求。

## 许可证

[在此指定您的许可证，例如 MIT]

## 附加资源

- [MyBatis Generator 文档](https://mybatis.org/generator/)
- [Spring Boot 文档](https://spring.io/projects/spring-boot)
- [Apache Flink 文档](https://flink.apache.org/)
- 有关详细的 MyBatis 设置，请参见 `MYBATIS_GENERATOR_README.md`。