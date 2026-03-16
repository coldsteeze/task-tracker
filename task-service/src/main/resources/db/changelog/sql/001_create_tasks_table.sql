CREATE TABLE tasks
(
    id          UUID PRIMARY KEY,
    user_id     VARCHAR     NOT NULL,
    title       VARCHAR     NOT NULL,
    description VARCHAR,
    status      VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
)