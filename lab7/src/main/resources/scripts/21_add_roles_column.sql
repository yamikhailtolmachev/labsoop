ALTER TABLE Users ADD COLUMN roles TEXT[] DEFAULT ARRAY['USER']::TEXT[];

CREATE INDEX idx_users_roles ON Users USING GIN(roles);

UPDATE Users SET roles = ARRAY['USER']::TEXT[] WHERE roles IS NULL;

COMMENT ON COLUMN Users.roles IS 'Роли пользователя (массив текста)';