CREATE TABLE Users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT valid_username CHECK (
        username IS NOT NULL AND
        char_length(TRIM(username)) >= 3 AND
        username = TRIM(username)
    ),
    CONSTRAINT valid_email CHECK (
        email IS NOT NULL AND
        email LIKE '%_@_%_.__%' AND
        char_length(email) BETWEEN 6 AND 100
    ),
    CONSTRAINT valid_password_hash CHECK (
        password_hash IS NOT NULL AND
        char_length(password_hash) >= 8
    )
);

CREATE INDEX idx_users_username ON Users(username);
CREATE INDEX idx_users_email ON Users(email);
CREATE INDEX idx_users_created_at ON Users(created_at);

COMMENT ON TABLE Users IS 'Таблица для хранения данных пользователей';
COMMENT ON COLUMN Users.id IS 'Уникальный идентификатор пользователя';
COMMENT ON COLUMN Users.username IS 'Логин пользователя (уникальный)';
COMMENT ON COLUMN Users.email IS 'Электронная почта (уникальная)';
COMMENT ON COLUMN Users.password_hash IS 'Хэш пароля пользователя';