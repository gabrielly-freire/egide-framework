CREATE TABLE manifestations (
    id          BIGSERIAL PRIMARY KEY,
    protocol_number VARCHAR(255) UNIQUE NOT NULL,
    title       VARCHAR(500) NOT NULL,
    description TEXT NOT NULL,
    type        VARCHAR(100) NOT NULL,
    status      VARCHAR(50)  NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL
);

CREATE TABLE service_evaluations (
    id                BIGSERIAL PRIMARY KEY,
    manifestation_id  BIGINT UNIQUE NOT NULL,
    rating            INT    NOT NULL,
    comment           TEXT,
    active            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);

CREATE TABLE responsible_assignments (
    id                BIGSERIAL PRIMARY KEY,
    manifestation_id  BIGINT NOT NULL,
    responsible_id    BIGINT NOT NULL,
    assigned_by_id    BIGINT,
    active            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);

CREATE TABLE decision_records (
    id                BIGSERIAL PRIMARY KEY,
    manifestation_id  BIGINT NOT NULL,
    author_id         BIGINT NOT NULL,
    type              VARCHAR(50) NOT NULL,
    content           TEXT NOT NULL,
    active            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);

CREATE TABLE audit_entries (
    id                BIGSERIAL PRIMARY KEY,
    manifestation_id  BIGINT NOT NULL,
    actor_id          BIGINT NOT NULL,
    action            VARCHAR(255) NOT NULL,
    description       TEXT,
    occurred_at       TIMESTAMP NOT NULL
);
