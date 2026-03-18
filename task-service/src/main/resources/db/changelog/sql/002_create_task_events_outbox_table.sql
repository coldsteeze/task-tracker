CREATE TABLE task_events_outbox
(
    id         UUID PRIMARY KEY,
    event_type VARCHAR(255) NOT NULL,
    payload    TEXT         NOT NULL,
    created_at TIMESTAMP
)