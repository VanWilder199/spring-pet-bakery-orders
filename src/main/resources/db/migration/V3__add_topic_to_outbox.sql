ALTER TABLE outbox_event
    ADD COLUMN topic VARCHAR(255) NOT NULL DEFAULT 'order-notification-topic';