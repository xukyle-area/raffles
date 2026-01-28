#!/bin/bash

set -e

JAR_NAME="raffles-1.0-SNAPSHOT.jar"
JAR_PATH="target/${JAR_NAME}"
LIB_DIR="target/classes/lib"
LIB_TARGET_DIR="target/lib"


# 0. 解析参数
COMMIT_MSG="Auto commit before restarting raffles"
while getopts "m:" opt; do
    case $opt in
        m)
            COMMIT_MSG="$OPTARG"
            ;;
        *)
            echo "用法: $0 [-m commit_message]"
            exit 1
            ;;
    esac
done
shift $((OPTIND-1))

# 1. 检查代码变动并构建
if git diff --quiet && git diff --staged --quiet; then
        echo "代码没有变动，跳过构建步骤。"
else
        echo "检测到代码变动，开始构建..."
        git add .
        git commit -m "$COMMIT_MSG"
        mvn clean package -DskipTests
fi

# 2. 确保主程序jar和依赖jar复制到 target/lib
mkdir -p "$LIB_TARGET_DIR"
if [ -f "$JAR_PATH" ]; then
    cp "$JAR_PATH" "$LIB_TARGET_DIR/"
    echo "已复制 $JAR_PATH 到 $LIB_TARGET_DIR/"
else
    echo "未找到 $JAR_PATH，构建失败或路径错误。"
    exit 1
fi
# 2.1 复制所有依赖jar到 target/lib
if [ -d "$LIB_DIR" ]; then
    cp "$LIB_DIR"/*.jar "$LIB_TARGET_DIR/"
    echo "已复制依赖jar到 $LIB_TARGET_DIR/"
else
    echo "未找到依赖目录 $LIB_DIR，跳过依赖jar复制。"
fi

# 3. 启动服务
java -jar "target/$JAR_NAME" "$@"
