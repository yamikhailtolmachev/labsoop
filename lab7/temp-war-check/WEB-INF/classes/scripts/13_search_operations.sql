-- 1. Найти операцию по ID
SELECT * FROM Operations WHERE id = $1;

-- 2. Найти операции по ID пользователя
SELECT * FROM Operations WHERE user_id = $1 ORDER BY computed_at DESC;

-- 3. Найти операции по типу
SELECT * FROM Operations WHERE operation_type = $1;

-- 4. Найти операции, в которых участвовала указанная функция (в качестве 1 или 2)
SELECT * FROM Operations WHERE function1_id = $1 OR function2_id = $1 ORDER BY computed_at DESC;

-- 5. Найти операции по ID результирующей функции
SELECT * FROM Operations WHERE result_function_id = $1;

-- 6. Найти операции с определёнными параметрами
SELECT * FROM Operations WHERE parameters @> $1::jsonb;