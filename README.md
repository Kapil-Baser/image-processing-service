# Image Processing Service

This is a solution to the Image Processing Service on roadmap.sh: [Image Processing Service](https://roadmap.sh/projects/image-processing-service)

## Overview

This project involves creating a backend system for an image processing service similar to Cloudinary. The service will allow users to upload images, perform various transformations, and retrieve images in different formats. The system will feature user authentication, image upload, transformation operations, and efficient retrieval mechanisms.

## Technologies Used

- Java 25
- Spring Boot
- Spring Security
- Spring Security OAuth2 Server for JWT
- Hibernate/JPA for database interaction
- Flyway for database migration versioning
- Supabase for postgresql database and S3 bucket

## Features

- Upload an Image to remove storage
- Various transformations can be applied on an image
- A User can list all the images they have stored
- A user can delete an image