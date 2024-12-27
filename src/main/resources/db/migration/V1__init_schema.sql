CREATE TABLE IF NOT EXISTS user(
    user_id VARCHAR(36) PRIMARY KEY NOT NULL,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at DATETIME,
    updated_at DATETIME
)