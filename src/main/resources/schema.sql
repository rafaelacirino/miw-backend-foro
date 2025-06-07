CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT PRIMARY KEY,
                                     first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL CHECK (role IN ('ADMIN', 'MEMBER', 'UNKNOWN')),
    registered_date TIMESTAMP
    );