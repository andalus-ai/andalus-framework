ALTER TABLE andalus_execution_task
    ADD COLUMN IF NOT EXISTS failure_count INTEGER NOT NULL DEFAULT 0;
ALTER TABLE andalus_execution_task
    ADD COLUMN IF NOT EXISTS failure_category VARCHAR(128);
ALTER TABLE andalus_execution_task
    ADD COLUMN IF NOT EXISTS failure_message TEXT;
ALTER TABLE andalus_execution_task
    ADD COLUMN IF NOT EXISTS dead_lettered_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS idx_andalus_execution_task_dead_letter
    ON andalus_execution_task (dead_lettered_at, assignment_id)
    WHERE dead_lettered_at IS NOT NULL;
