


# LCBAPI

**Lecturer Consultation Booking API**

LCBAPI is a backend REST API for managing lecturer consultation appointments in a university environment.

Students can view lecturer availability, book consultation slots, cancel bookings, and receive consultation-related notifications. Lecturers can create availability windows, manage consultation slots, and record consultation outcomes. Administrators can manage users and review reported consultation issues.

## Features

* JWT-based authentication
* Role-based access control
* Student, Lecturer, and Admin roles
* Lecturer availability windows
* Automatic consultation slot generation
* Consultation booking and cancellation
* Consultation outcome and attendance tracking
* Reporting of missed consultations
* In-app real-time notifications using WebSocket/STOMP
* Scheduled consultation reminders
* Soft deletion
* API documentation with Redoc

## Tech Stack

* **Java 21**
* **Spring Boot**
* **Spring Security**
* **Spring Data JPA / Hibernate**
* **PostgreSQL**
* **JWT**
* **WebSocket / STOMP**
* **Maven**
* **Docker**


## Main Roles

### Student

* View available consultation windows
* Book consultation slots
* Cancel bookings
* View consultation information
* Receive notifications
* Report missed consultations

### Lecturer

* Create and manage availability windows
* Manage consultation slots
* View bookings
* Record consultation outcomes
* Receive booking and cancellation notifications

### Admin

* Manage users
* Manage roles and permissions
* Review reported consultations
* Resolve reported issues

## Authentication

LCBAPI uses JWT authentication.

The access token is used to authenticate REST API requests, while a refresh token is used to obtain new access tokens.

WebSocket connections also use the authenticated user's identity to deliver personalized notifications.

## Notifications

LCBAPI supports real-time in-app notifications using **WebSocket with STOMP**.

Personalized notifications are delivered through user-specific destinations.

Examples include:

* New consultation booking
* Booking cancellation
* Consultation reminders
* Attendance status updates
* Admin responses to reported consultations

## Scheduled Tasks

The API uses scheduled jobs to process time-based operations such as consultation reminders.

For example, students can receive a reminder shortly before a scheduled consultation.

## API Documentation

The API documentation is available through **Redoc**.
A link to the api documentation:
#### https://nanor-bright-suka.github.io/Lecturer-Consultation-Booking-API/

## Running Remote With Server Url below
#### https://lecturer-consultation-booking-api.onrender.com

### Requirements

* Java 21
* Maven
* PostgreSQL
* Docker



## Project Structure

The project follows a layered Spring Boot architecture:

```text
controller
service
repository
entity
dto
mapper
security
config
exception
```

## Database

PostgreSQL is used as the primary database.

Database migrations are managed using **Flyway**.

## Purpose

LCBAPI was developed as a university final-year project to provide a structured system for managing lecturer-student consultation appointments and reducing the difficulties involved in coordinating consultation times.

