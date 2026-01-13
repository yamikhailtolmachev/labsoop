-- 1. Найти запись кэша по ключу
SELECT * FROM ComputationCache WHERE cache_key = $1;

-- 2. Найти кэш по пользователю
SELECT * FROM ComputationCache WHERE user_id = $1 ORDER BY computed_at DESC;

-- 3. Найти кэш по выражению
SELECT * FROM ComputationCache WHERE function_expression ILIKE $1;

-- 4. Найти кэш по параметрам
SELECT * FROM ComputationCache
WHERE user_id = $1
  AND function_expression = $2
  AND left_bound = $3
  AND right_bound = $4
  AND points_count = $5;

-- 5. Найти самые часто используемые кэши
SELECT * FROM ComputationCache ORDER BY access_count DESC LIMIT $1;