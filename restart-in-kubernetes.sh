#!/bin/bash
# restart-in-kubernetes.sh
git add .
git commit -m "Auto commit before restarting Raffles in Kubernetes"
mvn clean package -DskipTests
./build-docker.sh
kubectl delete pod -l app=raffles -n app
echo "Restarted Raffles pods in Kubernetes."
echo "Use
    kubectl get pods -n app
to check pod status."
echo "Use '
    kubectl logs -f deployment/raffles -n app
' to view logs."