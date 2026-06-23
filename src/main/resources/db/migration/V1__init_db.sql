CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE urls(
    id BIGSERIAL PRIMARY KEY,
    url TEXT not null,
    code VARCHAR(10) not null unique,
    created_at TIMESTAMP,
    expires_at TIMESTAMP,
    click_count INTEGER DEFAULT 0,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE 
)