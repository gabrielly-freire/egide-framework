-- Campos adicionados ao ponto fixo do Core (Manifestation) pela instância 3 (Atendimento Público):
-- affectedRegion (designação por região) e appealCount (contagem de recursos, LAI).
-- Nullable/default seguros para não quebrar dados existentes de outras instâncias.
ALTER TABLE manifestations ADD COLUMN affected_region VARCHAR(100);
ALTER TABLE manifestations ADD COLUMN appeal_count INTEGER NOT NULL DEFAULT 0;
