CREATE TABLE analysts (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255) NOT NULL,
    specialty  VARCHAR(100) NOT NULL,
    region     VARCHAR(100) NOT NULL,
    active     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE legal_impediments (
    id                BIGSERIAL PRIMARY KEY,
    manifestation_id  BIGINT NOT NULL,
    analyst_id        BIGINT NOT NULL REFERENCES analysts(id),
    reason            VARCHAR(50) NOT NULL,
    active            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL,
    updated_at        TIMESTAMP NOT NULL
);
