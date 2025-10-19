# Deployment Guides

This directory contains documentation for deploying the Alexia application.

## Platform Choice: Render

**Render is the recommended platform** for this project due to its excellent developer experience, integrated services, and generous free tier.

- **All-in-One Free Tier**: Render provides a free web service and a free PostgreSQL database, which simplifies setup.
- **Docker Support**: It has first-class support for Docker, allowing for consistent and reliable deployments.
- **Automation**: `render.yaml` allows for fully automated deployments (Infrastructure as Code).

## Critical Deployment Issues

Deploying a Vaadin Spring Boot application can be tricky. Here are the most common issues encountered and their solutions:

### 1. Vaadin Development Mode in Production ❌

- **Problem**: Application crashes on startup, complaining that it's not a Maven/Gradle project.
- **Root Cause**: Vaadin tries to run in development mode inside the production container, which lacks the necessary project files.
- **Solution**: You **must** enable Vaadin's production mode. This is done by adding the `vaadin-maven-plugin` to your `pom.xml` to build the frontend assets during the compile phase.

### 2. Port Configuration Mismatch ❌

- **Problem**: Health checks fail because the application is listening on the wrong port.
- **Root Cause**: Different platforms expect different ports (e.g., 8080, 8000, 10000).
- **Solution**: The application should dynamically bind to the port specified by the `PORT` environment variable. This project is configured to do so with `server.port=${PORT:8080}`.

### 3. Database Connection on Free Tiers ❌

- **Problem**: Application cannot connect to an external database like Supabase.
- **Root Cause**: Many free tiers (including Render's) have network restrictions that block outbound connections to external databases.
- **Solution**: Use the platform's integrated database service (e.g., Render PostgreSQL). It's on the internal network and works seamlessly.

## Universal Deployment Checklist

- [ ] `vaadin.productionMode=true` is enabled (via `pom.xml` plugin).
- [ ] Port is dynamically configured using `server.port=${PORT:8080}`.
- [ ] A multi-stage `Dockerfile` is used to create a small, secure final image.
- [ ] For free tiers, use the platform's internal database.
- [ ] Test the production Docker image locally before deploying.
