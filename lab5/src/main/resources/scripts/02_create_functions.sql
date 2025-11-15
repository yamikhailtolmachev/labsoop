CREATE TABLE Functions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'BASIC',
    expression TEXT,
    left_bound DOUBLE PRECISION,
    right_bound DOUBLE PRECISION,
    points_count INTEGER,
    points_data JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,

    CONSTRAINT valid_bounds CHECK (left_bound < right_bound),
    CONSTRAINT valid_points_count CHECK (points_count > 0),
    CONSTRAINT valid_type CHECK (type IN ('BASIC', 'COMPOSITE', 'OPERATION_RESULT'))
);

CREATE INDEX idx_functions_user_id ON Functions(user_id);
CREATE INDEX idx_functions_type ON Functions(type);
CREATE INDEX idx_functions_created_at ON Functions(created_at);
CREATE INDEX idx_functions_expression ON Functions(expression);
CREATE INDEX idx_functions_points_data ON Functions USING GIN (points_data);

COMMENT ON TABLE Functions IS 'Таблица для хранения математических функций';
COMMENT ON COLUMN Functions.id IS 'Уникальный идентификатор функции';
COMMENT ON COLUMN Functions.user_id IS 'Владелец функции (ссылка на Users)';
COMMENT ON COLUMN Functions.name IS 'Название функции';
COMMENT ON COLUMN Functions.type IS 'Тип функции: BASIC, COMPOSITE, OPERATION_RESULT';
COMMENT ON COLUMN Functions.expression IS 'Математическое выражение функции';
COMMENT ON COLUMN Functions.points_data IS 'Данные точек в JSON формате (НЕ поточечно)';