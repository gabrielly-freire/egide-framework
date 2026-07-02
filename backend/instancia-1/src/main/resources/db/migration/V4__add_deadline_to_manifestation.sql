-- Prazo (SLA) da fase atual do workflow, adicionado ao ponto fixo do Core (Manifestation).
-- Nullable: preenchido pelo WorkflowTemplate da instância; fases sem prazo ficam null.
ALTER TABLE manifestations ADD COLUMN deadline_at TIMESTAMP;
