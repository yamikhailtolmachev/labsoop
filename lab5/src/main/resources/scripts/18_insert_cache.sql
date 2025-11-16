-- 1. Создать новую запись кэша
INSERT INTO ComputationCache (cache_key, user_id, function_expression, left_bound, right_bound, points_count, result_function_id)
VALUES ($1, $2, $3, $4, $5, $6, $7)
ON CONFLICT (cache_key) DO UPDATE
SET access_count = ComputationCache.access_count + 1,
    computed_at = CURRENT_TIMESTAMP
RETURNING cache_key, result_function_id, access_count;