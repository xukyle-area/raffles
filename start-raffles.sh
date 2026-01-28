#!/bin/bash

set -e

JAR_NAME="raffles-1.0-SNAPSHOT.jar"
JAR_PATH="target/${JAR_NAME}"
LIB_DIR="target/classes/lib"
LIB_TARGET_DIR="target/lib"

# 1. 检查代码变动并构建
if git diff --quiet && git diff --staged --quiet; then
    echo "代码没有变动，跳过构建步骤。"
else
    echo "检测到代码变动，开始构建..."
    git add .
    git commit -m "Auto commit before restarting raffles"
    mvn clean package -DskipTests
fi

# 2. 确保主程序jar复制到 target/lib
mkdir -p "$LIB_TARGET_DIR"
if [ -f "$JAR_PATH" ]; then
    cp "$JAR_PATH" "$LIB_TARGET_DIR/"
    echo "已复制 $JAR_PATH 到 $LIB_TARGET_DIR/"
else
    echo "未找到 $JAR_PATH，构建失败或路径错误。"
    exit 1
fi

# 3. 启动服务
cd target
java -jar "lib/$JAR_NAME" "$@"
