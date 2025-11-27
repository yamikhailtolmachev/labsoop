-- 1. Удалить операцию по ID
DELETE FROM Operations WHERE id = $1 RETURNING id;

-- 2. Удалить все операции пользователя
DELETE FROM Operations WHERE user_id = $1;