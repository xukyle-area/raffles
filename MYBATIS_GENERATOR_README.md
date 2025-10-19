# MyBatis Generator 使用说明

## 概述
本项目已配置了 MyBatis Generator，用于根据数据库表结构自动生成实体类、Mapper 接口和 XML 映射文件。

## 配置的表
根据您提供的数据库结构，已配置以下表：

1. **category** - 类目表（规则分类管理）
2. **rule** - 规则表（存储各种检查规则）
3. **sql_template** - SQL 模板表（存储 SQL 检查模板）
4. **compliance_flink_table_ddl** - Flink 表 DDL 表（存储 Flink 数据表定义）

## 使用步骤

### 1. 准备数据库
确保 MySQL 数据库已启动，并执行您提供的建表脚本创建相应的表结构。

### 2. 检查配置
检查以下配置文件是否正确：

- `src/main/resources/generatorConfig.xml` - MyBatis Generator 配置
- `src/main/resources/application.yaml` - 数据库连接配置

### 3. 运行代码生成
使用以下命令之一运行 MyBatis Generator：

#### 方式一：使用 Maven 命令
```bash
mvn mybatis-generator:generate
```

#### 方式二：使用生成脚本
```bash
./generate-mybatis.sh
```

### 4. 生成的文件
代码生成后，将在以下位置创建文件：

```
src/main/java/com/gantenx/raffles/model/
├── entity/                    # 实体类
│   ├── Category.java
│   ├── Rule.java
│   ├── SqlTemplate.java
│   └── ComplianceFlinkTableDDL.java
└── mapper/                    # Mapper 接口
    ├── CategoryMapper.java
    ├── RuleMapper.java
    ├── SqlTemplateMapper.java
    └── ComplianceFlinkTableDDLMapper.java

src/main/resources/mapper/     # XML 映射文件
├── CategoryMapper.xml
├── RuleMapper.xml
├── SqlTemplateMapper.xml
└── ComplianceFlinkTableDDLMapper.xml
```

### 5. 整合到项目中
生成代码后，您需要：

1. 在主应用类上添加 `@MapperScan` 注解：
```java
@SpringBootApplication
@MapperScan("com.gantenx.raffles.model.mapper")
public class RafflesApplication {
    // ...
}
```

2. 如需自定义查询，可以在 Mapper 接口中添加方法，并在对应的 XML 文件中实现。

## 数据库连接配置说明

当前配置的数据库连接信息：
- 数据库：`raffles`
- 主机：`localhost:3306`
- 用户名：`root`
- 密码：`rootpassword`

如需修改，请同时更新：
- `generatorConfig.xml` 中的 `<jdbcConnection>` 部分
- `application.yaml` 中的 `spring.datasource` 部分

## 常见问题

### 1. 数据库连接失败
- 检查数据库是否启动
- 验证连接信息是否正确
- 确认数据库用户权限

### 2. 表不存在
- 确认已执行建表脚本
- 检查表名是否正确
- 验证数据库名称

### 3. 生成失败
- 查看控制台错误信息
- 检查 MySQL 驱动版本兼容性
- 确认目标目录存在且有写权限

## 注意事项

1. **重复生成**：Generator 配置了 `overwrite=true`，会覆盖已存在的文件
2. **自定义代码**：如果需要在生成的类中添加自定义代码，建议继承生成的基类
3. **版本控制**：建议将生成的代码提交到版本控制系统
4. **备份**：在重新生成前，建议备份已修改的文件

## 扩展配置

如需添加更多表，在 `generatorConfig.xml` 中添加相应的 `<table>` 配置：

```xml
<table tableName="your_table_name" domainObjectName="YourClassName">
    <generatedKey column="id" sqlStatement="JDBC" />
</table>
```