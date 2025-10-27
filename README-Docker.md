# ShelveIt Docker Setup

This project supports running in two different modes using Docker containers:

## Web Mode (Default)
Runs the Spring Boot web application with REST API and web interface.

### To run web mode:
```bash
docker-compose up web
```

This will:
- Start PostgreSQL database
- Start the web application on port 8080
- The CLI command runner will NOT execute (only web interface available)

## CLI Mode
Runs the command-line interface for interactive shell usage.

### To run CLI mode:
```bash
docker-compose --profile cli up cli
```

This will:
- Start PostgreSQL database
- Start the CLI application
- The web server will NOT start (only CLI interface available)

## Running Both Modes Simultaneously
You can run both web and CLI modes at the same time:

```bash
docker-compose --profile cli up
```

This will start:
- PostgreSQL database
- Web application (port 8080)
- CLI application (interactive terminal)

## Important

1. **Profile-based CommandLineRunner**: The `ShelveItCommandLineRunner` now only runs when the `cli` profile is active
2. **Separate Application Properties**: 
   - `application-dev.properties` - for web mode
   - `application-cli.properties` - for CLI mode (disables web server)
3. **Docker Configuration**: Updated docker-compose.yml to properly configure different profiles for web and CLI containers
