CREATE TABLE notifications
(
    id         UUID PRIMARY KEY,
    user_id    VARCHAR NOT NULL,
    type       VARCHAR(50) NOT NULL,
    status     VARCHAR(50) NOT NULL,
    payload    JSONB   NOT NULL,
    created_at TIMESTAMP,
    read_at    TIMESTAMP
)