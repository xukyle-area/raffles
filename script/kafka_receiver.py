import json
from kafka import  KafkaConsumer

class CalculateOutput:
    def __init__(self, multiplyResult, addResult):
        self.multiplyResult = multiplyResult
        self.addResult = addResult

    def to_dict(self):
        return {
            "multiplyResult": self.multiplyResult,
            "addResult": self.addResult
        }

# 接收消息
def consume_calculate_input(topic, bootstrap_servers):
    consumer = KafkaConsumer(
        topic,
        bootstrap_servers=bootstrap_servers,
        value_deserializer=lambda m: json.loads(m.decode('utf-8')),
        auto_offset_reset='earliest',
        group_id='test-group'
    )
    for message in consumer:
        print(f"Received: {message.value}")
        # 假设收到CalculateInput，计算输出
        ci = message.value
        output = CalculateOutput(
            multiplyResult=ci['numberFirst'] * ci['numberSecond'],
            addResult=ci['numberFirst'] + ci['numberSecond']
        )
        print(f"Output: {output.to_dict()}")
        break  # 只处理一条

if __name__ == "__main__":
    topic = "calculate-output"
    bootstrap_servers = ["localhost:9092"]

    # 接收并处理
    consume_calculate_input(topic, bootstrap_servers)