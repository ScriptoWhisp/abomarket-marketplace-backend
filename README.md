# Abomarket (online marketplace iti0302)

## Project Idea

An online marketplace web application where users can buy and sell products through fixed-price listings. Sellers can register to create product listings with descriptions, images, prices, and categories. Buyers can browse these listings, add items to a shopping cart, and make immediate purchases. The platform will support user authentication, product management and order processing.

This is the backend part of the project. To start the whole application first read instructions, setup and start the [frontend part](https://github.com/ScriptoWhisp/abomarket-marketplace-frontend)

#### Key Features

- **User Registration & Authentication:**
  - Sellers and buyers can create accounts.
  - Secure login and authentication mechanisms.
- **Product Listings:**
  - Sellers can create, edit, and delete product listings.
  - Each listing includes descriptions, images, prices, and categories.
- **Browsing & Searching:**
  - Buyers can browse products by categories.
  - Advanced search functionality to find specific items.
- **Shopping Cart:**
  - Buyers can add products to a shopping cart.
  - Review and modify cart contents before purchase.
- **Purchasing:**
  - Immediate purchase options with secure payment processing.
  - Order tracking and status updates.

---

## Technologies Used

### Backend

- **Java 21**
- **Gradle**
- **Spring Boot**
#### Libraries
- **Liquibase**
- **MapStruct**
- **Spring Security**
- **JSON Web Tokens (JJWT)**
- **Swagger-ui (OpenAPI)**
- **Validation**
- **Lombok**

### Frontend

- **Node.js**
- **Vue.js**
- **Tailwind CSS**

### Database

- **PostgreSQL**

## Requirements (For backend)

- **Java 21**
- **Docker**
- **Docker Compose**
- **PostgreSQL**
- **Gradle** (Used to install dependencies and build the project)

## CI/CD Setup

### Overview

The project utilizes **GitLab CI/CD** to automate the processes of building, testing, and deploying the application. The pipeline is defined in the `.gitlab-ci.yml` file and consists of three primary stages: **Build**, **Dockerize**, and **Deploy**.

### Pipeline Stages

1. **Build:**
   - Uses `gradle:jdk21` to clean and build the project, generating a `.jar` file stored in `build/libs`.
2. **Dockerize:**
   - Uses `docker:dind` to build a Docker image, tags it with the commit SHA and `latest`, and pushes it to Docker Hub.
3. **Deploy:**
   - Connects to the remote server via SSH and deploys the application using `docker-compose` to start `docker-compose.yml`, which is already on the server. `docker-compose` pulls the latest Docker image from Docker Hub, runs the database, and then launches the application in containers.

## Database Diagram

https://dbdiagram.io/d/Crossover-WebProject-database-design-v1-66ffd865fb079c7ebd591f90




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

2. Change docker-compose.yml as follows

> NOTE: change /path/to/application.properties to the path of your application.properties file:

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
