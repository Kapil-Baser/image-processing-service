# Image Processing Service

This is a solution to the Image Processing Service on roadmap.sh: [Image Processing Service](https://roadmap.sh/projects/image-processing-service)

## Overview

This project involves creating a backend system for an image processing service similar to Cloudinary. The service will allow users to upload images, perform various transformations, and retrieve images in different formats. The system will feature user authentication, image upload, transformation operations, and efficient retrieval mechanisms.

## Technologies Used

- Java 25
- Spring Boot
- Spring Security for authentication & authorization
- Spring Security OAuth2 Server for JWT
- Hibernate/JPA for database interaction
- Flyway for database migration versioning
- Supabase for postgresql database and S3 bucket
- Caffiene cache to optimize database read performance and redude API latency.

## Features

- ### User Authentication

    **Sign-Up**: Users can create an account.

    **Log-In**: Users can use JWT to log into their account.

    **JWT Authentication**: Secured endpoints using JWTs for authenticated access.

- ### Image Management

    **Upload Image**: Users can upload images.

    **Transform Image**: Users can perform various transformations (resize, crop, rotate, filters etc.).

    **Retrieve Image**: Users to retrieve a saved image in different formats.

    **List Images**: List all uploaded images by the user with metadata.

- ### Image Transformation

  - Resize
  - Crop
  - Rotate
  - Flip
  - Mirror
  - Change format (JPEG, PNG, WEBP)
  - Apply filters (grayscale, brightness)

## Getting Started

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) 20.10+
- A free [Supabase](https://supabase.com) account and project
- Git


### 1. Set up Supabase

This app uses Supabase for two things:

| Purpose | Supabase feature |
|---|---|
| Relational data | PostgreSQL database |
| Image uploads | Storage bucket |

**Database**

1. Create a new project at [supabase.com/dashboard](https://supabase.com/dashboard) and save the database password.
2. Go to **Project Settings → Database → Connection string** and copy the **JDBC** connection details.
3. Use the **Session pooler** connection string. **Type:** JDBC

**Storage**

1. Go to **Storage → New bucket** and create a bucket named `<your-bucket-name>`.
2. Choose **Public** for images to be viewable by URL
3. Go to **Project Settings → API Keys** and copy the **Secret Key**.

### 2. Configure environment variables

Copy the example file and fill in your values:

```bash
cp .env.example .env
```

### 3. Build and run with Docker

```bash
# Build the image
docker build -t <app-name> .

# Run the container
docker run -p 8080:8080 --env-file .env <app-name>
```

The API is now available at `http://localhost:8080`.

**Or with Docker Compose**:

```bash
docker compose up --build -d
```

### Stopping

```bash
docker compose down        # if using Compose
# or press Ctrl+C / docker stop <container> if using docker run
```