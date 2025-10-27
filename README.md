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
# Start web mode
docker-compose up web

# Access application
# Web: http://localhost:8080
# Database: localhost:5432
```

#### CLI Mode only
Runs the command-line interface for interactive shell usage.

```bash
# Start CLI mode
docker-compose --profile cli up cli
```

#### Both Modes
Run both web and CLI modes simultaneously:

```bash
docker-compose --profile cli up
```

For detailed Docker setup instructions, see [Docker Setup Guide](README-Docker.md).

## Technologies

- **Backend**: Java 21, Spring Boot, PostgreSQL
- **Build**: Maven
- **Containerization**: Docker, Docker Compose
- **Testing**: JUnit 5, Mockito

## License

MIT License - see [LICENSE](LICENSE) file for details.