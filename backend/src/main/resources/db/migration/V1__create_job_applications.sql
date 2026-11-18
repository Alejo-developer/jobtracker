CREATE TABLE job_applications (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    company     VARCHAR(120)  NOT NULL,
    position    VARCHAR(120)  NOT NULL,
    job_url     VARCHAR(500),
    location    VARCHAR(120),
    source      VARCHAR(60),
    status      VARCHAR(20)   NOT NULL,
    applied_on  DATE,
    notes       VARCHAR(2000),
    created_at  TIMESTAMPTZ   NOT NULL,
    updated_at  TIMESTAMPTZ   NOT NULL,
    CONSTRAINT chk_job_applications_status
        CHECK (status IN ('SAVED', 'APPLIED', 'INTERVIEW', 'OFFER', 'REJECTED'))
);

CREATE INDEX idx_job_applications_status ON job_applications (status);
