-- 1. Создать нового пользователя
INSERT INTO Users (username, email, password_hash)
VALUES ($1, $2, $3)
RETURNING id, username, email, created_at;