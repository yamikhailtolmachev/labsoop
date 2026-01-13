-- 1. Обновить параметры операции по ID
UPDATE Operations
SET parameters = $2::jsonb, updated_at = CURRENT_TIMESTAMP
WHERE id = $1;

-- 2. Обновить тип операции по ID
UPDATE Operations
SET operation_type = $2, updated_at = CURRENT_TIMESTAMP
WHERE id = $1;