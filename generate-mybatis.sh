#!/bin/bash

# MyBatis Generator 代码生成脚本
# 描述：根据数据库表结构生成实体类、Mapper接口和XML映射文件
# 作者：Raffles Team
# 日期：$(date)

echo "=========================================="
echo "MyBatis Generator 代码生成工具"
echo "=========================================="

# 检查Maven是否安装
if ! command -v mvn &> /dev/null; then
    echo "错误：Maven 未安装或不在 PATH 中"
    echo "请先安装 Maven 并配置环境变量"
    exit 1
fi

# 显示将要生成的表
echo "将为以下表生成代码："
echo "- category (类目表)"
echo "- rule (规则表)"
echo "- sql_template (SQL模板表)"
echo "- compliance_flink_table_ddl (Flink表DDL表)"
echo ""

# 询问用户是否确认
read -p "是否继续生成代码？(y/N): " -r
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "已取消代码生成"
    exit 0
fi

echo ""
echo "开始生成代码..."

# 运行 MyBatis Generator
mvn mybatis-generator:generate

# 检查命令执行结果
if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "代码生成完成！"
    echo "=========================================="
    echo ""
    echo "生成的文件位置："
    echo "✓ 实体类: src/main/java/com/gantenx/raffles/model/entity/"
    echo "✓ Mapper接口: src/main/java/com/gantenx/raffles/model/mapper/"  
    echo "✓ XML映射文件: src/main/resources/mapper/"
    echo ""
    echo "后续步骤："
    echo "1. 检查生成的代码是否符合预期"
    echo "2. 在主应用类添加 @MapperScan 注解"
    echo "3. 根据需要自定义 Mapper 方法"
    echo "4. 提交代码到版本控制系统"
    echo ""
    echo "详细使用说明请参考: MYBATIS_GENERATOR_README.md"
else
    echo ""
    echo "=========================================="
    echo "代码生成失败！"
    echo "=========================================="
    echo ""
    echo "可能的原因："
    echo "- 数据库连接失败"
    echo "- 表不存在"
    echo "- 配置文件错误"
    echo ""
    echo "请检查："
    echo "1. 数据库是否启动"
    echo "2. generatorConfig.xml 中的连接配置"
    echo "3. 数据库表是否已创建"
    echo ""
    echo "详细错误信息请查看上方输出"
    exit 1
fi