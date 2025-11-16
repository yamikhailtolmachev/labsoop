-- 1. Найти функцию по ID
SELECT * FROM Functions WHERE id = $1;

-- 2. Найти функции по ID пользователя
SELECT * FROM Functions WHERE user_id = $1 ORDER BY created_at DESC;

-- 3. Найти функции по типу
SELECT * FROM Functions WHERE type = $1;

-- 4. Найти функции по типу и пользователю
SELECT * FROM Functions WHERE user_id = $1 AND type = $2 ORDER BY created_at DESC;

-- 5. Найти функцию по имени и пользователю
SELECT * FROM Functions WHERE user_id = $1 AND name = $2;

-- 6. Найти функции с выражением
SELECT * FROM Functions WHERE expression ILIKE $1;