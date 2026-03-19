CREATE TABLE task_events_outbox
(
    id          UUID PRIMARY KEY,
    event_type  VARCHAR(255) NOT NULL,
    payload     JSONB        NOT NULL,
    status      VARCHAR(100) NOT NULL,
    retry_count INT DEFAULT 0,
    created_at  TIMESTAMP
);

CREATE INDEX idx_outbox_status_retry_created
    ON task_events_outbox (status, retry_count, created_at);