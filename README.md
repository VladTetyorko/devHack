# DevHack Application

A Spring Boot application with PostgreSQL database.

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
openai.api.key=your-api-key
openai.model=gpt-3.5-turbo
openai.max-tokens=500
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
     gptj.api.key=your-key
     gptj.model=gpt-j-6b
     gptj.max-tokens=500
     gptj.api.url=http://gptj:8080/v1/chat/completions
     ```

3. **Switching Between AI Services**:
   - Set the `ai.service.provider` property to either `openai` or `gptj`:
     ```properties
     ai.service.provider=gptj
     ```
   - In Docker, this is set to `gptj` by default in `application-docker.properties`
   - For local development, it defaults to `openai`

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
