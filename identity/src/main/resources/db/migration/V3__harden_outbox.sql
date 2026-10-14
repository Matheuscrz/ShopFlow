ALTER TABLE outbox_events
    ADD COLUMN attempts INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN last_error TEXT;

CREATE INDEX idx_outbox_pending_created
    ON outbox_events (created_at)
    WHERE status = 'PENDING';