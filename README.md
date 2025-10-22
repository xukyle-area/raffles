# 🚀 Raffles

Raffles is a high-scalable streaming processing platform based on Flink and Spring Boot, responsible for the architecture design and implementation of Flink tasks and rule engine systems. It supports dynamic task generation and configurable rule management.

Keywords: Flink, Spring Boot, Configurable Tasks, Rule Engine

## Technical Implementation and Achievements

- **Multi-source Data Ingestion Architecture**: Unified collection and efficient processing of streaming and batch tasks, significantly improving data throughput performance.
- **Full-link Configurable Management**: Configurable management of task input, logic, and output, supporting rapid task deployment and flexible expansion for business teams.
- **Unified REST API Interface System**: Supports task triggering, monitoring, and automated operations, enhancing system operation efficiency and reliability.
- **Dynamic Rule Management**: Supports dynamic rule submission, online editing, and hot-loading, meeting the needs of high-frequency changes and complex real-time decision scenarios. Rule loading time reduced by 60%, task deployment cycle from 1 day to 1 hour.

## Technology Stack

- **Flink**: Streaming processing framework.
- **Spring Boot**: Application framework.
- **Java**: Core language.
- **Maven**: Build tool.
- **MyBatis**: Database operations.
- **Docker**: Containerization.
- **Kubernetes**: Deployment.
- **MySQL**: Database.

## Prerequisites

- Java 8 or higher
- Maven 3.x
- MySQL 8.x
- Docker (optional, for containerized deployment)

## Getting Started

### Clone the Repository

```bash
git clone <repository-url>
cd raffles
```

### Build the Project

Use Maven to build the project:

```bash
mvn clean install
```

This will compile the code, run tests, and package the application into a JAR file located in `target/`.

### Generate MyBatis Code

The project uses MyBatis Generator to create entities, mappers, and XML files from database tables.

1. Ensure your MySQL database is running and configured.
2. Update `src/main/resources/generatorConfig.xml` with your database connection details.
3. Run the generation script:

```bash
./generate-mybatis.sh
```

Or manually with Maven:

```bash
mvn mybatis-generator:generate
```

Generated files will be placed in:
- Entities: `src/main/java/com/gantenx/raffles/model/entity/`
- Mappers: `src/main/java/com/gantenx/raffles/model/mapper/`
- XML Mappings: `src/main/resources/mapper/`

### Run the Application

After building, run the Spring Boot application:

```bash
java -jar target/raffles-1.0-SNAPSHOT.jar
```

Or using Maven:

```bash
mvn spring-boot:run
```

The application will start on the default port (usually 8080). Check `src/main/resources/application.yaml` for configuration.

### Docker Build

To build and run with Docker:

```bash
./build-docker.sh
```

This uses the `dockerfile` in the root directory.

### Kubernetes Deployment

Use the provided script to restart in Kubernetes:

```bash
./restart-in-kubernetes.sh
```

## Configuration

- **Database**: Configure in `src/main/resources/generatorConfig.xml` and `application.yaml`.
- **Flink Tasks**: Configurable task management.
- **Rules**: Dynamic rule engine.

## Project Structure

```
.
├── src/
│   ├── main/
│   │   ├── java/com/gantenx/raffles/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── model/           # Generated entities and mappers
│   │   │   └── utils/           # Utility classes like FileListing and FlinkTypeUtils
│   │   └── resources/
│   │       ├── generatorConfig.xml  # MyBatis generator config
│   │       └── mapper/          # Generated XML mappings
│   └── test/                    # Test sources
├── target/                      # Build output
├── pom.xml                      # Maven configuration
├── dockerfile                   # Docker build file
├── generate-mybatis.sh          # Script for MyBatis code generation
└── README.md                    # This file
```

## Contributing

1. Fork the repository.
2. Create a feature branch.
3. Make your changes.
4. Run tests and build.
5. Submit a pull request.

## License

[Specify your license here, e.g., MIT]

## Additional Resources

- [MyBatis Generator Documentation](https://mybatis.org/generator/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Apache Flink Documentation](https://flink.apache.org/)
- See `MYBATIS_GENERATOR_README.md` for detailed MyBatis setup.
