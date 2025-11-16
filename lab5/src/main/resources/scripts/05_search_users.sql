-- 1. Найти пользователя по ID
SELECT * FROM Users WHERE id = $1;

-- 2. Найти пользователя по имени пользователя
SELECT * FROM Users WHERE username = $1;

-- 3. Найти пользователя по email
SELECT * FROM Users WHERE email = $1;

-- 4. Найти всех пользователей
SELECT * FROM Users ORDER BY created_at DESC LIMIT $1 OFFSET $2;

-- 5. Найти пользователей по части имени
SELECT * FROM Users WHERE username ILIKE $1 ORDER BY username;