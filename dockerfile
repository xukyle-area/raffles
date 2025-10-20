FROM openjdk:8-jre-slim

WORKDIR /app

RUN mkdir -p /app/flink-libs

# 复制应用文件
COPY target/raffles-1.0-SNAPSHOT.jar /app/
COPY target/raffles-1.0-SNAPSHOT.jar.original /app/flink-libs/raffles.jar
COPY target/classes/lib/*.jar /app/flink-libs/

ENV FLINK_LIBS_DIR=/app/flink-libs

EXPOSE 18080

CMD ["java", "-jar", "/app/raffles-1.0-SNAPSHOT.jar"]