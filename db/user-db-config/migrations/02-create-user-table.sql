CREATE TABLE IF NOT EXISTS users (
    id                SERIAL          PRIMARY KEY,
    login             VARCHAR(255)    NOT NULL    UNIQUE      CHECK ("login" ~ '^[a-zA-Z0-9.]{3,20}$'),
    email             VARCHAR(255)    NOT NULL    UNIQUE      CHECK ("email" ~ '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    password_hash     TEXT            NOT NULL,
    role              user_role       NOT NULL    DEFAULT 'user',
    created_at        TIMESTAMP       NOT NULL    DEFAULT NOW(),
    updated_at        TIMESTAMP,
    status            user_status     NOT NULL    DEFAULT 'active'
);
