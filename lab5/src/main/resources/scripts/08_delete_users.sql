-- 1. Удалить пользователя по ID
DELETE FROM Users WHERE id = $1 RETURNING id;