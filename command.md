```bash
# 启动 zk
/usr/local/kafka/bin/zookeeper-server-start.sh -daemon /usr/local/etc/kafka/zookeeper.properties

# 启动 kafka
/usr/local/kafka/bin/kafka-server-start.sh -daemon /usr/local/etc/kafka/server.properties

# 查看状态
ps aux | grep -E 'kafka|zookeeper' | grep -v grep; lsof -i :2181; lsof -i :9092

# 创建 topic
/usr/local/kafka/bin/kafka-topics.sh --create --topic test --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1

# 查看 topic 列表
/usr/local/kafka/bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# 查看 topic 详情
/usr/local/kafka/bin/kafka-topics.sh --describe --topic test --bootstrap-server localhost:9092

# 控制台生产者
/usr/local/kafka/bin/kafka-console-producer.sh --topic test --bootstrap-server localhost:9092

# 控制台消费者
/usr/local/kafka/bin/kafka-console-consumer.sh --topic test --from-beginning --bootstrap-server localhost:9092

# 停止 Kafka
pkill -f kafka.Kafka

# 停止 Zookeeper
pkill -f QuorumPeerMain

# 查看 Kafka 日志
tail -f /usr/local/kafka/logs/server.log

# 查看 Zookeeper 日志
tail -f /usr/local/kafka/logs/zookeeper.out
```