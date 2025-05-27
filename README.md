# Payments Service

Service for creating and retrieving payment records in a MySQL-backed system.

---

## Features

- Create payments with validation
- Retrieve a single payment by `external_id`
- Retrieve all payments associated with an `email`
- MySQL persistence with `external_id` uniqueness enforcement

---

## Requirements

- Java 21
- JDK 21
- Apache Maven 3.x >=


## API Endpoints

### Create a Payment

Create a new payment entry.

**Request**

```bash
curl --location 'http://localhost:8080/payments/create' \
--header 'Content-Type: application/json' \
--data-raw '{
    "external_id": "1234567890",
    "currency": "PEN",
    "amount": 20.0,
    "email": "test@gmail.com"
}'
```

**Success Response** (`201 Created`)

```json
{
    "external_id": "1234567890",
    "currency": "PEN",
    "amount": 20.0,
    "email": "test@gmail.com",
    "created_at": "2024-10-01T12:34:56",
    "updated_at": "2024-10-01T12:34:56"
}
```

**Duplicate Error Response** (`409 Conflict`)

```json
{
    "error_message": "Payment with external_id already exists: 1234567890",
    "error_code": "PAYMENT_DUPLICATE_EXTERNAL_ID"
}
```

---

### Get Payment by `external_id`

Fetch a payment using its unique `external_id`.

**Request**

```bash
curl --location 'http://localhost:8080/payments/{external_id}' \
--header 'Content-Type: application/json'
```

**Response** (`200 OK`)

```json
{
    "external_id": "1234567890",
    "currency": "PEN",
    "amount": 20.0,
    "email": "test@gmail.com",
    "created_at": "2024-10-01T12:34:56",
    "updated_at": "2024-10-01T12:34:56"
}
```

**Not Found Response** (`404 Not Found`)

```json
{
    "error_message": "Payment with external_id 1234567890 not found",
    "error_code": "PAYMENT_NOT_FOUND"
}
```

---

### Get Payments by `email`

Fetch all payments linked to a given email address.

**Request**

```bash
curl --location 'http://localhost:8080/payments/email/{email}'
```

**Response** (`200 OK`)

```json
[
    {
        "external_id": "1234567890",
        "currency": "PEN",
        "amount": 20.0,
        "email": "test@gmail.com",
        "created_at": "2024-10-01T12:34:56",
        "updated_at": "2024-10-01T12:34:56"
    }
]
```

---

## Database Setup

### MySQL via Docker Compose

To run the MySQL service locally for this project:

```yaml
# db_transactions.yml
version: '3.3'

services:
  db:
    image: mysql:latest
    restart: always
    environment:
      MYSQL_DATABASE: 'transactions'
      MYSQL_USER: 'admin'
      MYSQL_PASSWORD: 'admin1234'
      MYSQL_ROOT_PASSWORD: 'admin1234'
    ports:
      - '3308:3306'
    expose:
      - '3308'
```

Start the container with:

```bash
docker compose -f db_transactions.yml up
```

---

## Table Structure: `payments`

```sql
CREATE TABLE `payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `external_id` varchar(100) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `currency` varchar(3) NOT NULL,
  `email` varchar(100) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `external_id_UNIQUE` (`external_id`),
  KEY `idx_payments_external_id` (`external_id`),
  KEY `idx_payments_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
```

---
