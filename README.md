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
```bash
# Start everything
docker-compose up --build

# Access application
# Web: http://localhost:8080
# Database: localhost:5432
```

## Technologies

- **Backend**: Java 21, Spring Boot, PostgreSQL
- **Build**: Maven
- **Containerization**: Docker, Docker Compose
- **Testing**: JUnit 5, Mockito

## License

MIT License - see [LICENSE](LICENSE) file for details.