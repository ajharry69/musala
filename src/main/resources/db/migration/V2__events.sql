CREATE TABLE IF NOT EXISTS events
(
    id                        BIGSERIAL PRIMARY KEY,
    name                      VARCHAR(100) NOT NULL,
    date                      DATE         NOT NULL,
    available_attendees_count INTEGER      NOT NULL,
    category                  VARCHAR(20)  NOT NULL,
    description               VARCHAR(500) NOT NULL,
    created_by_id             UUID DEFAULT null,
    last_modified_by_id       UUID DEFAULT null,
    date_created              TIMESTAMP WITH TIME ZONE,
    date_last_modified        TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (created_by_id) REFERENCES users (id) ON DELETE SET NULL,
    FOREIGN KEY (last_modified_by_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE INDEX IF NOT EXISTS idx_events_name ON events (name);
CREATE INDEX IF NOT EXISTS idx_events_date ON events (date);
CREATE INDEX IF NOT EXISTS idx_events_category ON events (category);
CREATE INDEX IF NOT EXISTS idx_events_description ON events (description);
CREATE INDEX IF NOT EXISTS idx_events_date_created ON events (date_created);
CREATE INDEX IF NOT EXISTS idx_events_date_last_modified ON events (date_last_modified);
