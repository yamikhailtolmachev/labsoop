-- 1. Удалить запись кэша по ключу
DELETE FROM ComputationCache WHERE cache_key = $1 RETURNING cache_key;

-- 2. Удалить кэш пользователя
DELETE FROM ComputationCache WHERE user_id = $1;

-- 3. Удалить старые записи кэша (старше 30 дней)
DELETE FROM ComputationCache WHERE computed_at < CURRENT_TIMESTAMP - INTERVAL '30 days';