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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

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
CREATE INDEX idx_cache_updated_at ON ComputationCache(updated_at);

CREATE INDEX idx_cache_lookup ON ComputationCache(
    user_id,
    function_expression,
    left_bound,
    right_bound,
    points_count
);

COMMENT ON TABLE ComputationCache IS 'Хранит результаты вычислений функций для предотвращения повторных дорогостоящих расчётов';
COMMENT ON COLUMN ComputationCache.id IS 'Уникальный идентификатор записи кэша';
COMMENT ON COLUMN ComputationCache.cache_key IS 'Уникальный ключ кэша, генерируемый на основе параметров вычисления';
COMMENT ON COLUMN ComputationCache.user_id IS 'Идентификатор пользователя, которому принадлежит кэшированный результат';
COMMENT ON COLUMN ComputationCache.function_expression IS 'Математическое выражение функции, для которой был вычислен результат';
COMMENT ON COLUMN ComputationCache.left_bound IS 'Левая граница интервала, для которого был вычислен результат';
COMMENT ON COLUMN ComputationCache.right_bound IS 'Правая граница интервала, для которого был вычислен результат';
COMMENT ON COLUMN ComputationCache.points_count IS 'Количество точек, использованных при вычислении результата';
COMMENT ON COLUMN ComputationCache.result_function_id IS 'Идентификатор функции (из таблицы Functions), содержащей вычисленные точки';
COMMENT ON COLUMN ComputationCache.computed_at IS 'Время создания записи кэша';
COMMENT ON COLUMN ComputationCache.access_count IS 'Счётчик обращений к этой записи кэша';
COMMENT ON COLUMN ComputationCache.updated_at IS 'Время последнего обновления записи кэша';