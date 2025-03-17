# Trailmenu

This is the Central Backend Service for the Trailmenu Outdoor Recipe Planner

## Table of Contents

- [Local Setup](#local-setup)
    - [Java Version](#java-version)
    - [Database](#database)
    - [OpenAPI](#openapi)
    - [Testing](#testing)
- [Project Management](#project-management)

## Local Setup

### Java Version

Install Java version 21 Temurin using sdkman on your machine.

### Database

To start up the local database docker needs to be installed. Start up database container using `docker compose up -d`

### OpenAPI

Endpoints and Authentication are documented using OpenAPI and the SwaggerUI.

- [SwaggerUI](http://127.0.0.1:8080/swagger-ui/index.html#/)
- [ApiDocs](http://127.0.0.1:8080/v3/api-docs)

### Testing

Tests can be executed using `./gradlew test`. A test coverage limit of 80% is automatically enforced. You can view
the current coverage report [here](http://localhost:63342/core/build/reports/jacoco/test/html/index.html?).

## Project Management

Storys and Tasks for this Project are managed in a related GitHub Project
see [here](https://github.com/users/paulbrenker/projects/5/views/1).