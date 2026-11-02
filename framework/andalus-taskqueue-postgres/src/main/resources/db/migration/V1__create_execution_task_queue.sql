CREATE TABLE IF NOT EXISTS andalus_execution_task (
    assignment_id VARCHAR(255) PRIMARY KEY,
    idempotency_key VARCHAR(512) NOT NULL UNIQUE,
    execution_id VARCHAR(255) NOT NULL,
    node_id VARCHAR(255) NOT NULL,
    attempt_number INTEGER NOT NULL CHECK (attempt_number > 0),
    assigned_worker_id VARCHAR(255) NOT NULL,
    assignment_fence_generation BIGINT NOT NULL,
    assignment_status VARCHAR(32) NOT NULL,
    timeout_millis BIGINT NOT NULL,
    max_retries INTEGER NOT NULL,
    available_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    claim_owner VARCHAR(255),
    claim_until TIMESTAMPTZ,
    claim_generation BIGINT NOT NULL DEFAULT 0 CHECK (claim_generation >= 0),
    acknowledged BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK ((claim_owner IS NULL) = (claim_until IS NULL))
);

CREATE INDEX IF NOT EXISTS idx_andalus_execution_task_ready
    ON andalus_execution_task (available_at, assignment_id)
    WHERE acknowledged = FALSE;
