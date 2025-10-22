# raffles

## 服务重启流程

1. **修改代码**：根据需求更新相关代码文件。

2. **重新构建项目**：
   ```shell
   mvn clean package -DskipTests
   ```

3. **重启服务**：删除当前运行的Pod，触发自动重启：
   ```shell
   kubectl delete pod -l app=raffles -n app
   ```

4. **查看日志**：监控重启后的服务日志：
   ```shell
   kubectl logs -f deployment/raffles -n app
   ```

5. xx

```
kubectl get pod -n app
kubectl get pod raffles-5cd4b78d55-bt9w5 -n app -o yaml
```