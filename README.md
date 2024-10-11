# Iti0302 2024 Project (Working on title)

## How to Run the Application

You can run the application in multiple ways: directly via Gradle, using Docker, or through an IDE like IntelliJ IDEA.
For this application you need PostgreSQL database up and running, for this in the project directory located docker compose file.
Use

  ```bash
  docker compose up -d
  ```

### Using Gradle

1. Clone the Repository:

  ```bash
  git clone https://github.com/datjul/iti0302-2024-backend.git
  cd iti0302-2024-backend
  ```

2. Build the Project:

  ```bash
  ./gradlew clean build
  ```

3. Run the Application:

  ```bash
  ./gradlew bootRun
  ```

Alternatively, you can run the generated JAR file:

  ```bash
  java -jar build/libs/iti0302-2024-backend-0.0.1-SNAPSHOT.jar
  ```

### Using IntelliJ IDEA

Open the Project:

Launch IntelliJ IDEA.
Click on Open and select the project directory.
Run the Application:

Navigate to the Application class.
Click the Run button or press Shift + F10 (^ + R on mac).

### Using Docker

To build docker image and run container you need app to be builded in jar (see how to build with gradle above).

Build the Docker Image using the Dockerfile from the directory:

1. Navigate to the project root directory (where the Dockerfile is located) and run:

  ```bash
  docker build -t crossover3:latest .
  ```

Run the Application Container:

2. Change docker-compose.yml as follows:

```bash
version: "3.7"

services:
  postgres:
    image: postgres:14.1
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=docker
    ports:
      - '5432:5432'
    volumes:
      - ./postgres-data:/var/lib/postgresql/data

  web-project:
    image: crossover3:latest
    container_name: web-project
    restart: always
    depends_on:
      - postgres
    ports:
      - '8080:8080'
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/postgres
    volumes:
      - /path/to/application.properties:/app/application.properties
      
```

And then execute the following command to run database and application in a container:

```bash
docker compose up -d
```

The application will be accessible at http://localhost:8080 (assuming default ports).