#!/bin/bash
set -e

# 配置参数
JAR_NAME="raffles-1.0-SNAPSHOT.jar"
THIN_JAR_NAME="raffles-1.0-SNAPSHOT.jar.original"
THIN_JAR_TARGET="raffles-1.0-SNAPSHOT-thin.jar"
JAR_PATH="target/${JAR_NAME}"
THIN_JAR_PATH="target/${THIN_JAR_NAME}"
LIB_DIR="target/classes/lib"
LIB_TARGET_DIR="target/lib"

# 解析 commit message 参数
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

# 检查代码变动并构建
if git diff --quiet && git diff --staged --quiet; then
  echo "代码没有变动，跳过构建步骤。"
else
  echo "检测到代码变动，开始构建..."
  git add .
  git commit -m "$COMMIT_MSG"
  mvn clean package -DskipTests
fi

# 准备 lib 目录
mkdir -p "$LIB_TARGET_DIR"

# 检查主程序 jar 是否存在
if [ ! -f "$JAR_PATH" ]; then
  echo "未找到 $JAR_PATH，构建失败或路径错误。"
  exit 1
fi

# 复制 thin jar 并重命名
if [ -f "$THIN_JAR_PATH" ]; then
  cp "$THIN_JAR_PATH" "$LIB_TARGET_DIR/$THIN_JAR_TARGET"
  echo "已复制 thin jar $THIN_JAR_NAME 到 $LIB_TARGET_DIR/$THIN_JAR_TARGET"
else
  echo "未找到 thin jar $THIN_JAR_NAME，跳过。"
fi

# 复制所有依赖 jar 到 lib 目录
if [ -d "$LIB_DIR" ]; then
  cp "$LIB_DIR"/*.jar "$LIB_TARGET_DIR/"
  echo "已复制依赖 jar 到 $LIB_TARGET_DIR/"
else
  echo "未找到依赖目录 $LIB_DIR，跳过依赖 jar 复制。"
fi

# 启动服务（主程序 jar 只在 target 下）
echo "启动 raffles 服务..."
java -jar "$JAR_PATH" "$@"