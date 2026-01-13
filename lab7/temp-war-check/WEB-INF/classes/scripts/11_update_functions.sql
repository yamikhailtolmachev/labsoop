-- 1. Обновить выражение функции по ID
UPDATE Functions
SET expression = $2, updated_at = CURRENT_TIMESTAMP
WHERE id = $1;

-- 2. Обновить данные точек функции по ID
UPDATE Functions
SET points_data = $2::jsonb, updated_at = CURRENT_TIMESTAMP
WHERE id = $1;

-- 3. Обновить имя функции по ID
UPDATE Functions
SET name = $2, updated_at = CURRENT_TIMESTAMP
WHERE id = $1;

-- 4. Обновить границы и количество точек функции по ID
UPDATE Functions
SET left_bound = $2, right_bound = $3, points_count = $4, updated_at = CURRENT_TIMESTAMP
WHERE id = $1;