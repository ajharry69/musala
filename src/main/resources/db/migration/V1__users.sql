CREATE TABLE IF NOT EXISTS users
(
    id                 UUID PRIMARY KEY,
    name               VARCHAR(100) NULL,
    email              VARCHAR(250) NULL,
    password           VARCHAR(500) NULL,
    date_created       TIMESTAMP WITH TIME ZONE,
    date_last_modified TIMESTAMP WITH TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_users_name ON users (name);
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);
CREATE INDEX IF NOT EXISTS idx_users_password ON users (password);
CREATE INDEX IF NOT EXISTS idx_users_date_created ON users (date_created);
CREATE INDEX IF NOT EXISTS idx_users_date_last_modified ON users (date_last_modified);
