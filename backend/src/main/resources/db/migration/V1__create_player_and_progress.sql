CREATE TABLE players (
    id UUID PRIMARY KEY,
    name VARCHAR(40) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE station_progress (
    id UUID PRIMARY KEY,
    player_id UUID NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    station_id VARCHAR(40) NOT NULL,
    current_step INTEGER NOT NULL DEFAULT 0,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_player_station UNIQUE(player_id, station_id)
);

CREATE TABLE command_attempts (
    id UUID PRIMARY KEY,
    player_id UUID NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    station_id VARCHAR(40) NOT NULL,
    command TEXT NOT NULL,
    accepted BOOLEAN NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_station_progress_player ON station_progress(player_id);
CREATE INDEX idx_command_attempts_player_created ON command_attempts(player_id, created_at);
