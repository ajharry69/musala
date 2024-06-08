CREATE TABLE IF NOT EXISTS tickets
(
    id                  BIGSERIAL PRIMARY KEY,
    attendees_count     INTEGER NOT NULL,
    event_id            BIGINT  NOT NULL,
    reserved_by_id UUID DEFAULT null,
    date_reserved  TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (reserved_by_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_tickets_date_reserved ON tickets (date_reserved);
