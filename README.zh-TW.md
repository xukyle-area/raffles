# 🚀 Raffles

Raffles 是一个基于 Java 的应用程序，使用 Spring Boot 构建，集成了 MyBatis 用于数据库操作，Flink 用于数据处理，以及其他用于文件处理和类型转换的工具。它似乎设计用于管理规则、类别和 SQL 模板，可能用于数据验证或处理工作流。

## 功能特性

- **数据库集成**：使用 MyBatis 进行 ORM，为 `category`、`rule` 和 `sql_template` 等表生成实体和映射器。
- **Flink 支持**：包括用于 Flink 类型信息和行处理的工具。
- **文件列表**：提供从类路径或文件系统列出 JAR 文件的工具。
- **Docker 支持**：包括用于容器化的 Docker 配置。
- **Kubernetes 部署**：用于在 Kubernetes 环境中重启的脚本。

## 技术栈

- **Java**：核心语言。
- **Spring Boot**：用于构建应用程序的框架。
- **MyBatis**：用于数据库映射和代码生成。
- **Flink**：用于流处理工具。
- **Maven**：构建工具。
- **Docker**：容器化。
- **MySQL**：数据库（在 MyBatis 生成器中配置）。

## 先决条件

- Java 8 或更高版本
- Maven 3.x
- MySQL 8.x（用于代码生成）
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
- **Flink Jars**：`FileListing.getFlinkJars()` 方法检索用于 Flink 提交的 JAR 路径。
- **类别**：在 `Category` 枚举中定义。

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