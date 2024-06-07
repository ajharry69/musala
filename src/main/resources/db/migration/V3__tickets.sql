CREATE TABLE IF NOT EXISTS tickets
(
    id                  BIGSERIAL PRIMARY KEY,
    attendees_count     INTEGER NOT NULL,
    event_id            BIGINT  NOT NULL,
    created_by_id       UUID DEFAULT null,
    last_modified_by_id UUID DEFAULT null,
    date_created        TIMESTAMP WITH TIME ZONE,
    date_last_modified  TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE,
    FOREIGN KEY (created_by_id) REFERENCES users (id) ON DELETE SET NULL,
    FOREIGN KEY (last_modified_by_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_tickets_date_created ON tickets (date_created);
CREATE INDEX IF NOT EXISTS idx_tickets_date_last_modified ON tickets (date_last_modified);
