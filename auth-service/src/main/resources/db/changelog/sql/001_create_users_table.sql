CREATE TABLE users
(
    id          UUID PRIMARY KEY,
    keycloak_id VARCHAR NOT NULL UNIQUE,
    username    VARCHAR NOT NULL UNIQUE,
    email       VARCHAR NOT NULL UNIQUE,
    created_at  TIMESTAMP
)