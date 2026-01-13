-- 1. Обновить email пользователя по ID
UPDATE Users
SET email = $2, updated_at = CURRENT_TIMESTAMP
WHERE id = $1;

-- 2. Обновить username пользователя по ID
UPDATE Users
SET username = $2, updated_at = CURRENT_TIMESTAMP
WHERE id = $1;

-- 3. Обновить email и username пользователя по ID
UPDATE Users
SET email = $2, username = $3, updated_at = CURRENT_TIMESTAMP
WHERE id = $1;