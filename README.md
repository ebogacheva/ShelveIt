# ShelveIt

Personal inventory management application with CLI and web interfaces. Helps you keep track of where you store your items using a hierarchical storage system.

## Features

- **Storage Management**: Create hierarchy (RESIDENCE → ROOM → FURNITURE → UNIT)
- **Item Management**: Add items with keywords for easy searching
- **Search**: Find items by name, keywords, or location
- **CLI Interface**: Interactive command-line interface with enhanced help system
- **Web Interface**: REST API and web UI
- **Docker Support**: Easy containerized deployment

## Quick Start

### Docker (Recommended)

The application runs in two modes: **web** and **CLI**.

#### Web Mode (Default)
Runs the Spring Boot web application with REST API and web interface.

```bash
# Start web mode (includes backend and database)
docker-compose up web

# Access application
# Web UI: http://localhost:8081
# Backend API: http://localhost:8080/api
# Database: localhost:5432
```

#### CLI Mode Only
Runs the command-line interface for interactive shell usage.

```bash
# Start CLI mode (includes backend and database, but NOT web)
docker-compose --profile cli up cli
```

**Note:** To interact with the CLI from another terminal window, see the [Docker Setup Guide](README-Docker.md#interacting-with-cli-from-another-terminal) for detailed instructions.

#### Both Modes
Run both web and CLI modes simultaneously:

```bash
# Start all services (postgres, backend, web, cli)
docker-compose --profile cli up
```

For detailed Docker setup instructions, see [Docker Setup Guide](README-Docker.md).

## Technologies

- **Backend**: Java 21, Spring Boot, PostgreSQL
- **Build**: Maven
- **Containerization**: Docker, Docker Compose
- **Testing**: JUnit 5, Mockito

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.