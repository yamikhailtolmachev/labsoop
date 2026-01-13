-- 1. Увеличить счётчик обращений к кэшу
UPDATE ComputationCache
SET access_count = access_count + 1, computed_at = CURRENT_TIMESTAMP
WHERE cache_key = $1;

-- 2. Обновить ID результирующей функции в кэше
UPDATE ComputationCache
SET result_function_id = $2, computed_at = CURRENT_TIMESTAMP
WHERE cache_key = $1;