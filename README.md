# Backend Web Engineering Project

## Description

This is a Spring Boot project that uses MariaDB as the database and MinIO as the storage solution. The project provides a REST API to manage users and their files, serving as the backend for a social media platform.

## 🗃️ Requirements

Ensure the following tools are installed:

- **Docker**: To run the application containers
  - [Get Docker](https://www.docker.com/)

## 📦 Containers

The project consists of the following containers:

1. **Spring Boot Backend**
   - Port: `8080`

2. **MariaDB**
   - Port: `3306`

3. **MinIO**
   - API Port: `9000`
   - Dashboard Port: `9001`

## ⚙️ Already Installed Dependencies

- **SpringDoc OpenAPI**:
  - API JSON: `/api`
  - Swagger UI: `/swagger.html`

## 🚀 Setup

### Build Docker Containers

Build the application containers:

```bash
docker compose build
```

### Start Docker Containers

Start the containers (with build):

```bash
docker compose up
```

Alternatively, start the containers and rebuild them:

```bash
docker compose up --build
```

### Stop Docker Containers

Stop the running containers:

```bash
docker compose stop
```

### Remove Docker Containers

Completely remove the containers:

```bash
docker compose down
```

## 📋 Component Diagram

![Component Diagram](App_Component_Diagram.png)

### 💡 Additional Notes

This project uses:
- **MariaDB** for relational database management.
- **MinIO** for object storage.
- **SpringDoc OpenAPI** for interactive API documentation, accessible at `/swagger.html`.
