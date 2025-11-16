-- 1. Удалить функцию по ID
DELETE FROM Functions WHERE id = $1 RETURNING id;