CREATE TABLE Operations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    function1_id UUID NOT NULL,
    function2_id UUID,
    result_function_id UUID NOT NULL,
    operation_type VARCHAR(20) NOT NULL,
    parameters JSONB,
    computed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (function1_id) REFERENCES Functions(id) ON DELETE RESTRICT,
    FOREIGN KEY (function2_id) REFERENCES Functions(id) ON DELETE RESTRICT,
    FOREIGN KEY (result_function_id) REFERENCES Functions(id) ON DELETE RESTRICT,

    CONSTRAINT valid_operation_type CHECK (
        operation_type IN ('ADD', 'SUBTRACT', 'MULTIPLY', 'DIVIDE', 'DIFFERENTIATE', 'CREATE')
    ),
    CONSTRAINT different_functions CHECK (function1_id != function2_id OR function2_id IS NULL)
);

CREATE INDEX idx_operations_user_id ON Operations(user_id);
CREATE INDEX idx_operations_function1_id ON Operations(function1_id);
CREATE INDEX idx_operations_function2_id ON Operations(function2_id);
CREATE INDEX idx_operations_result_id ON Operations(result_function_id);
CREATE INDEX idx_operations_type ON Operations(operation_type);
CREATE INDEX idx_operations_computed_at ON Operations(computed_at);
CREATE INDEX idx_operations_parameters ON Operations USING GIN (parameters);
CREATE INDEX idx_operations_updated_at ON Operations(updated_at);

COMMENT ON TABLE Operations IS 'Хранит историю операций над функциями';
COMMENT ON COLUMN Operations.id IS 'Уникальный идентификатор записи операции';
COMMENT ON COLUMN Operations.user_id IS 'Идентификатор пользователя, инициировавшего операцию (ссылка на Users.id)';
COMMENT ON COLUMN Operations.function1_id IS 'Идентификатор первой функции, участвующей в операции (операнд)';
COMMENT ON COLUMN Operations.function2_id IS 'Идентификатор второй функции, участвующей в операции (операнд), может быть NULL для унарных операций';
COMMENT ON COLUMN Operations.result_function_id IS 'Идентификатор функции, являющейся результатом выполнения операции (ссылка на Functions.id)';
COMMENT ON COLUMN Operations.operation_type IS 'Тип выполненной операции: ADD, SUBTRACT, MULTIPLY, DIVIDE, DIFFERENTIATE, CREATE';
COMMENT ON COLUMN Operations.parameters IS 'Дополнительные параметры, используемые при выполнении операции, хранящиеся в формате JSONB';
COMMENT ON COLUMN Operations.computed_at IS 'Время создания записи об операции';
COMMENT ON COLUMN Operations.updated_at IS 'Время последнего обновления записи об операции';