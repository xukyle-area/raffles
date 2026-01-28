import json
from kafka import KafkaProducer

# Model结构
class CalculateInput:
    def __init__(self, numberFirst, numberSecond):
        self.numberFirst = numberFirst
        self.numberSecond = numberSecond

    def to_dict(self):
        return {
            "numberFirst": self.numberFirst,
            "numberSecond": self.numberSecond
        }

# 发送消息
def send_calculate_input(topic, bootstrap_servers):
    producer = KafkaProducer(
        bootstrap_servers=bootstrap_servers,
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )
    data = CalculateInput(9, 6)
    producer.send(topic, data.to_dict())
    producer.flush()
    print(f"Sent: {data.to_dict()}")

if __name__ == "__main__":
    topic = "calculate-input"
    bootstrap_servers = ["localhost:9092"]

    # 发送
    send_calculate_input(topic, bootstrap_servers)