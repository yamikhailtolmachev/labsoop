-- 1. Создать новую операцию
INSERT INTO Operations (user_id, function1_id, function2_id, result_function_id, operation_type, parameters)
VALUES ($1, $2, $3, $4, $5, $6::jsonb)
RETURNING id, user_id, operation_type, computed_at;