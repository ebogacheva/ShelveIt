# ShelveIt App

- ShelveIt is a personal inventory management application that helps you keep track of where you store your items. 
- It allows you to create a hierarchy of storages (e.g., residence, room, furniture, unit) and associate items with specific storages. 
- The app provides features such as searching for items by name, keywords, or storage hierarchy, and retrieving items near a specific item.

---

## Features

- Create and manage storages (four types supported - RESIDENCE, ROOM, FURNITURE, UNIT).
- Add items to storages with optional keywords for easier searching.
- Search for items by name, keywords.
- Retrieve items by id, near a specific item, or in a specific storage.
- Track the hierarchy of storages for a specific item.
- Console-based interface for easy interaction.
- REST API for integration with other systems.

---

## Technologies Used

- **Java**: Version 21
- **Spring Boot**: Framework for building the application
- **PostgreSQL**: Database for storing items and storages
- **Docker**: For running the database
- **Maven**: Build tool for managing dependencies
- **JUnit 5**: For testing
- **Mockito**: For mocking in tests

---

## Getting Started

Follow these steps to set up and run the ShelveIt app locally.

---

### Prerequisites

1. **Java**: Ensure Java 21 is installed on your system.
2. **Maven**: Ensure Maven is installed.
3. **Docker**: Ensure Docker is installed for running the PostgreSQL database.

---

### Setting Up the Database

1. Navigate to the root folder of the project (where the `docker-compose.yml` file is located).

2. Start the database using Docker:
   ```bash
   docker-compose up -d

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.