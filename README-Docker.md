# Docker Setup

## Quick Start

```bash
# Start everything
docker-compose up --build

# Access application
# Web: http://localhost:8080
# Database: localhost:5432

# Stop
docker-compose down
```

## Commands

```bash
# Web interface only (recommended)
docker-compose up --build

# Interactive CLI (requires terminal)
docker-compose up -d postgres
docker run -it --rm --network shelveit_default \
  -e SPRING_PROFILES_ACTIVE=dev \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/shelveit_dev \
  -e SPRING_DATASOURCE_USERNAME=dev_user \
  -e SPRING_DATASOURCE_PASSWORD=dev_password \
  shelveit-app

```

## Environment Variables

```bash
# Database
POSTGRES_DB=shelveit_dev
POSTGRES_USER=dev_user
POSTGRES_PASSWORD=dev_password

# Application
SPRING_PROFILES_ACTIVE=dev
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/shelveit_dev
SPRING_DATASOURCE_USERNAME=dev_user
SPRING_DATASOURCE_PASSWORD=dev_password
```
