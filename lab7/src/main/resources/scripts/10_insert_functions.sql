-- 1. Создать новую функцию
INSERT INTO Functions (user_id, name, type, expression, left_bound, right_bound, points_count, points_data)
VALUES ($1, $2, $3, $4, $5, $6, $7, $8::jsonb)
RETURNING id, user_id, name, type, created_at;