CREATE TABLE ComputationCache (
    cache_key VARCHAR(512) PRIMARY KEY,
    user_id UUID NOT NULL,
    function_expression TEXT NOT NULL,
    left_bound DOUBLE PRECISION NOT NULL,
    right_bound DOUBLE PRECISION NOT NULL,
    points_count INTEGER NOT NULL,
    result_function_id UUID NOT NULL,
    computed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    access_count INTEGER DEFAULT 1,

    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (result_function_id) REFERENCES Functions(id) ON DELETE CASCADE,

    CONSTRAINT valid_cache_bounds CHECK (left_bound < right_bound),
    CONSTRAINT valid_cache_points CHECK (points_count > 0),
    CONSTRAINT positive_access_count CHECK (access_count >= 1)
);

CREATE INDEX idx_cache_user_id ON ComputationCache(user_id);
CREATE INDEX idx_cache_result_id ON ComputationCache(result_function_id);
CREATE INDEX idx_cache_expression ON ComputationCache(function_expression);
CREATE INDEX idx_cache_computed_at ON ComputationCache(computed_at);
CREATE INDEX idx_cache_access_count ON ComputationCache(access_count);

CREATE INDEX idx_cache_lookup ON ComputationCache(
    user_id,
    function_expression,
    left_bound,
    right_bound,
    points_count
);

COMMENT ON TABLE ComputationCache IS 'Таблица кэширования вычислений для избежания повторных расчетов';
COMMENT ON COLUMN ComputationCache.cache_key IS 'Уникальный ключ кэша (хэш параметров)';
COMMENT ON COLUMN ComputationCache.function_expression IS 'Математическое выражение функции';
COMMENT ON COLUMN ComputationCache.result_function_id IS 'Ссылка на готовую функцию в таблице Functions';
COMMENT ON COLUMN ComputationCache.access_count IS 'Количество обращений к этому кэшу';