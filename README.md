# raffles


```bash
docker run -d \
  -p 18081:18080 \
  --add-host host.docker.internal:host-gateway \
  -e MYSQL_HOST=host.docker.internal \
  -e FLINK_HOST=host.docker.internal \
  --name raffles \
  raffles:latest
```