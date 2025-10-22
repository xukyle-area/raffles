#!/bin/bash
# restart-in-kubernetes.sh

# 检查代码是否有变动
if git diff --quiet && git diff --staged --quiet; then
    echo "代码没有变动，跳过构建步骤。"
else
    echo "检测到代码变动，开始构建..."
    git add .
    git commit -m "Auto commit before restarting tethys in Kubernetes"

    mvn clean package -DskipTests
    ./build-docker.sh
fi
# 重启 Kubernetes 中的 Raffles pods
kubectl delete pod -l app=raffles -n app
echo "Restarted Raffles pods in Kubernetes."
echo "Use
kubectl get pods -n app
to check pod status."
echo "Use '
kubectl logs -f deployment/raffles -n app
' to view logs."