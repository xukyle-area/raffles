#!/bin/bash
# build-docker.sh

set -e

echo "🚀 Building Raffles Docker image..."

# 确保项目已构建
echo "📦 Building Maven project..."
mvn clean package -DskipTests

# 检查必要文件是否存在
if [ ! -f "target/raffles-1.0-SNAPSHOT.jar" ]; then
    echo "❌ Main JAR file not found!"
    exit 1
fi

if [ ! -f "target/raffles-1.0-SNAPSHOT.jar.original" ]; then
    echo "❌ Original JAR file not found!"
    exit 1
fi

if [ ! -d "target/classes/lib" ]; then
    echo "❌ Lib directory not found!"
    exit 1
fi

echo "✅ All required files found"

# 构建Docker镜像
echo "🐳 Building Docker image..."
docker build -t raffles:latest .

echo "📋 Image built successfully!"
echo "📦 Image details:"
docker images | grep raffles

echo "🎉 Build completed!"