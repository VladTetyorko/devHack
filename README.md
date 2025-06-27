# DevHack Application

A Spring Boot application for interview preparation with PostgreSQL database. This application helps users prepare for technical interviews by providing a platform to manage, search, and generate interview questions using AI.

## Table of Contents

- [Prerequisites](#prerequisites)
- [Running with Docker Compose](#running-with-docker-compose)
- [Database Information](#database-information)
- [Development](#development)
  - [Setting Up Properties Files](#setting-up-properties-files)
  - [Using Environment Variables](#using-environment-variables)
  - [Running Locally](#running-locally)
- [Project Structure](#project-structure)
- [Database Migrations](#database-migrations)
- [Troubleshooting](#troubleshooting)
- [AI Services](#ai-services)
  - [OpenAI API](#openai-api)
  - [GPT-J via LocalAI](#gpt-j-via-localai)
- [Resetting Git History](#resetting-git-history)
- [Features](#features)
- [Architecture](#architecture)
- [Contributing](#contributing)

## Prerequisites

- Docker and Docker Compose installed
- Java 17 (for local development)
- Gradle (for local development)

## Running with Docker Compose

1. Build the application:
   ```bash
   ./gradlew build
   ```

2. Start the containers:
   ```bash
   docker-compose up -d
   ```

3. The application will be available at http://localhost:8081

4. To stop the containers:
   ```bash
   docker-compose down
   ```

5. To stop the containers and remove volumes:
   ```bash
   docker-compose down -v
   ```

## Database Information

- PostgreSQL database: `interview_prep`
- Username: `postgres`
- Password: `postgres`
- Port: `5432` (inside Docker), mapped to `5434` on the host

## Development

### Setting Up Properties Files

This project uses template property files to avoid committing sensitive information like API keys to git.
Before running the application, you need to create your own properties files:

1. Copy each template file to create the actual properties file:
   ```bash
   cp src/main/resources/application.properties.template src/main/resources/application.properties
   cp src/main/resources/application-local.properties.template src/main/resources/application-local.properties
   cp src/main/resources/application-docker.properties.template src/main/resources/application-docker.properties
   ```

2. Edit each properties file to add your actual API keys and other sensitive information.

Note: The actual properties files are excluded from git in .gitignore to prevent accidentally committing sensitive information.

### Using Environment Variables

Instead of hardcoding sensitive information in properties files, you can use environment variables. The application is configured to read values from environment variables with fallback to default values.

To set up the required environment variables, you can use the provided script:

1. Run the script with the appropriate environment:
   ```bash
   source set_env_vars.sh [environment]
   ```
   Where `[environment]` is one of: `default`, `local`, or `docker` (default is "default").

2. Update the sensitive values with your actual API keys:
   ```bash
   export OPENAI_API_KEY="your-actual-openai-api-key"
   export GPTJ_API_KEY="your-actual-gptj-api-key"
   ```

3. Run the application with the environment variables set:
   ```bash
   # For default environment
   ./gradlew bootRun

   # For local environment
   ./gradlew bootRun --args='--spring.profiles.active=local'

   # For docker environment
   docker-compose up -d
   ```

Note: Remember to run the script with `source` to export variables to your current shell.

### Running Locally

For local development without Docker:

1. Ensure PostgreSQL is running locally on port 5434 with the correct credentials
2. Run the application with the "local" profile:
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=local'
   ```

   Alternatively, you can set the environment variable:
   ```bash
   export SPRING_PROFILES_ACTIVE=local
   ./gradlew bootRun
   ```

## Project Structure

- Spring Boot 3.5.3
- Java 17
- PostgreSQL database
- JPA for database access
- Liquibase for database migrations
- OpenAI API integration
- GPT-J integration via LocalAI

## Database Migrations

This project uses Liquibase for database migrations. Changelog files are located in `src/main/resources/db/changelog` with the master changelog file at `db/changelog/db.changelog-master.yaml`.

- `01-create-schema.sql`: Creates the initial database schema
- `02-init-data.sql`: Inserts initial data into the database

When the application starts, Liquibase automatically applies any pending migrations to the database.

## Troubleshooting

### Database Connection Issues

If you encounter database connection issues, try the following:

1. **Docker Environment**:
   - Ensure Docker and Docker Compose are running
   - Check if the PostgreSQL container is running: `docker ps | grep postgres_interview`
   - Restart the containers: `docker-compose down && docker-compose up -d`

2. **Local Environment**:
   - Ensure PostgreSQL is running on port 5434
   - Verify the database credentials in `application-local.properties`
   - Make sure you're using the "local" profile: `--spring.profiles.active=local`

3. **Common Error Messages**:
   - "Connection refused": The PostgreSQL server is not running or not accessible
   - "Authentication failed": Incorrect username or password
   - "Database does not exist": The `interview_prep` database has not been created

## AI Services

This application integrates with two AI services:

### OpenAI API

The application uses the OpenAI API for generating text and interview questions. Configuration is in `application.properties`:

```properties
# Using environment variables with fallback to default values
openai.api.key=${OPENAI_API_KEY:your-openai-api-key}
openai.model=${OPENAI_MODEL:gpt-3.5-turbo}
openai.max-tokens=${OPENAI_MAX_TOKENS:500}
```

You can set these values using environment variables:
```bash
export OPENAI_API_KEY="your-actual-openai-api-key"
export OPENAI_MODEL="gpt-3.5-turbo"
export OPENAI_MAX_TOKENS="500"
```

### GPT-J via LocalAI

The application also integrates with GPT-J through LocalAI, which provides an OpenAI-compatible API for local models:

1. **Docker Setup**:
   - The GPT-J service is configured in `docker-compose.yml`
   - Models are stored in the `./models` directory
   - LocalAI automatically downloads the necessary models on startup

2. **Configuration**:
   - GPT-J configuration is in `application-docker.properties`:
     ```properties
     # Using environment variables with fallback to default values
     gptj.api.key=${GPTJ_API_KEY:your-key}
     gptj.model=${GPTJ_MODEL:gpt-j-6b}
     gptj.max-tokens=${GPTJ_MAX_TOKENS:500}
     gptj.api.url=${GPTJ_API_URL_DOCKER:http://gptj:8080/v1/chat/completions}
     ```

   - You can set these values using environment variables:
     ```bash
     export GPTJ_API_KEY="your-actual-gptj-api-key"
     export GPTJ_MODEL="gpt-j-6b"
     export GPTJ_MAX_TOKENS="500"
     export GPTJ_API_URL_DOCKER="http://gptj:8080/v1/chat/completions"
     ```

3. **Switching Between AI Services**:
   - Set the `ai.service.provider` property to either `openai` or `gptj`:
     ```properties
     # Using environment variable with fallback to default value
     ai.service.provider=${AI_SERVICE_PROVIDER:openai}
     ```
   - You can set this value using an environment variable:
     ```bash
     export AI_SERVICE_PROVIDER="gptj"
     ```
   - In Docker, this is set to `gptj` by default in `application-docker.properties`
   - For local development, it defaults to `openai`

   - The provided `set_env_vars.sh` script sets the appropriate values for each environment.

For more information about the GPT-J models, see the [models/README.md](models/README.md) file.

## Resetting Git History

If you need to reset the git history to remove sensitive information from previous commits:

1. Make sure you have a backup of your code (the script will create one automatically)
2. Run the reset script:
   ```bash
   ./reset_git.sh
   ```

This script will:
- Create a backup of your code
- Remove the .git directory
- Initialize a new git repository
- Add all files (except those in .gitignore, including the .properties files)
- Make an initial commit

After running this script, you'll have a clean git history with no previous commits, and the sensitive .properties files will be excluded from git tracking.

## Features

DevHack offers the following key features:

- **Interview Question Management**: Create, view, edit, and delete interview questions
- **Question Tagging**: Organize questions by tags (e.g., Java, Spring, Algorithms)
- **Difficulty Levels**: Filter questions by difficulty (Easy, Medium, Hard)
- **Search Functionality**: Search questions by keywords, tags, and difficulty
- **Dashboard**: View statistics and summaries of your question collection
- **AI-Powered Question Generation**: Generate new interview questions using AI
  - Single tag question generation
  - Multi-tag question generation
  - Customizable difficulty levels
- **User Management**: User registration, authentication, and profile management
- **Notes**: Add personal notes to questions for future reference
- **Answers**: Store and manage answers to interview questions

## Architecture

The application follows a standard Spring Boot architecture with the following components:

- **Controller Layer**: Handles HTTP requests and responses
  - REST controllers for API endpoints
  - MVC controllers for web views
- **Service Layer**: Contains business logic
  - Domain services for core business operations
  - View services for presentation logic
  - API services for external integrations (OpenAI, GPT-J)
- **Repository Layer**: Data access using Spring Data JPA
- **Model Layer**: Domain entities and data transfer objects
- **Configuration**: Application configuration and security settings

The application uses a PostgreSQL database for data persistence and integrates with AI services (OpenAI API and GPT-J via LocalAI) for question generation.

### Key Components:

- **Spring Boot**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database access
- **Thymeleaf**: Server-side templating
- **Liquibase**: Database migration
- **Docker**: Containerization
- **OpenAI API**: AI-powered question generation
- **LocalAI**: Local AI model integration

## Contributing

Contributions to DevHack are welcome! Here's how you can contribute:

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature-name`
3. Commit your changes: `git commit -m 'Add some feature'`
4. Push to the branch: `git push origin feature/your-feature-name`
5. Submit a pull request

Please make sure your code follows the project's coding standards and includes appropriate tests.
