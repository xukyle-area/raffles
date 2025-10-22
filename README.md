# 🚀 Raffles

Raffles is a Java-based application built with Spring Boot, integrating MyBatis for database operations, Flink for data processing, and other utilities for file handling and type conversions. It appears to be designed for managing rules, categories, and SQL templates, possibly for data validation or processing workflows.

## Features

- **Database Integration**: Uses MyBatis for ORM, with generated entities and mappers for tables like `category`, `rule`, and `sql_template`.
- **Flink Support**: Includes utilities for Flink type information and row processing.
- **File Listing**: Provides utilities to list JAR files from classpath or filesystem.
- **Docker Support**: Includes Docker configuration for containerization.
- **Kubernetes Deployment**: Scripts for restarting in Kubernetes environment.

## Technology Stack

- **Java**: Core language.
- **Spring Boot**: Framework for building the application.
- **MyBatis**: For database mapping and code generation.
- **Flink**: For stream processing utilities.
- **Maven**: Build tool.
- **Docker**: Containerization.
- **MySQL**: Database (configured in MyBatis generator).

## Prerequisites

- Java 8 or higher
- Maven 3.x
- MySQL 8.x (for code generation)
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
- **Flink Jars**: The `FileListing.getFlinkJars()` method retrieves JAR paths for Flink submissions.
- **Categories**: Defined in `Category` enum.

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
