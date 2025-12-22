CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    roles TEXT[] DEFAULT ARRAY['USER'],
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

CREATE TABLE IF NOT EXISTS cache (
    cache_key VARCHAR(255) PRIMARY KEY,
    user_id UUID,
    expression TEXT NOT NULL,
    points JSONB NOT NULL,
    calculation_time_ms INTEGER NOT NULL,
    access_count INTEGER DEFAULT 0,
    last_accessed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_cache_user_id ON cache(user_id);
CREATE INDEX IF NOT EXISTS idx_cache_last_accessed ON cache(last_accessed);

INSERT INTO users (id, username, email, password_hash, roles) VALUES
(
    '11111111-1111-1111-1111-111111111111',
    'admin',
    'admin@example.com',
    'sT3eXwqLKfi0vN6QY6J8Bz4Q8LhZLb6UjLhKf4N8D3M=',
    ARRAY['ADMIN', 'USER']
) ON CONFLICT (username) DO NOTHING;

INSERT INTO users (id, username, email, password_hash, roles) VALUES
(
    '22222222-2222-2222-2222-222222222222',
    'user',
    'user@example.com',
    'sT3eXwqLKfi0vN6QY6J8Bz4Q8LhZLb6UjLhKf4N8D3M=',
    ARRAY['USER']
) ON CONFLICT (username) DO NOTHING;

INSERT INTO users (id, username, email, password_hash, roles) VALUES
(
    '33333333-3333-3333-3333-333333333333',
    'apiuser',
    'api@example.com',
    'sT3eXwqLKfi0vN6QY6J8Bz4Q8LhZLb6UjLhKf4N8D3M=',
    ARRAY['API_USER']
) ON CONFLICT (username) DO NOTHING;