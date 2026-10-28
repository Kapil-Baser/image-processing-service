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
  -  Crop
  -  Rotate
  -  Flip

    Mirror

    Compress

    Change format (JPEG, PNG, etc.)

    Apply filters (grayscale, brightness, etc.)
