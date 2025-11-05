# ShelveIt Docker Setup

This project supports running in two different modes using Docker containers:

## Web Mode (Default)
Runs the Spring Boot web application with REST API and web interface.

### To run web mode:
```bash
docker-compose up web
```

This will:
- Start PostgreSQL database (port 5432)
- Start the backend API (port 8080)
- Start the web application (port 8081)
- The CLI service will NOT start (only web interface available)

## CLI Mode
Runs the command-line interface for interactive shell usage.

### To run CLI mode:
```bash
docker-compose --profile cli up cli
```

This will:
- Start PostgreSQL database (port 5432)
- Start the backend API (port 8080)
- Start the CLI application (interactive terminal)
- The web server will NOT start (only CLI interface available)

## Running Both Modes Simultaneously
You can run both web and CLI modes at the same time:

```bash
docker-compose --profile cli up
```

This will start:
- PostgreSQL database (port 5432)
- Backend API (port 8080)
- Web application (port 8081)
- CLI application (interactive terminal)

## Interacting with CLI from Another Terminal

When the CLI service is running in Docker, you can interact with it from a separate terminal window. If the CLI container is already running (started with `docker-compose --profile cli up`), open a new terminal and run:

```bash
docker exec -it shelveit_cli sh -c "java -jar /app/app.jar"
```

This will start an interactive CLI session in the new terminal window.

## Important Notes

1. **Service Profiles**: The `cli` service uses Docker Compose profiles - it only starts when `--profile cli` is specified
2. **Service Dependencies**:
   - `web` depends on `backend`
   - `cli` depends on `backend`
   - `backend` depends on `postgres`
3. **Port Mapping**:
   - Backend API: `http://localhost:8080/api`
   - Web UI: `http://localhost:8081`
   - PostgreSQL: `localhost:5432`
4. **CLI Container Networking**: The CLI container connects to the backend using Docker's internal DNS resolution (`http://backend:8080/api`), not `localhost`
