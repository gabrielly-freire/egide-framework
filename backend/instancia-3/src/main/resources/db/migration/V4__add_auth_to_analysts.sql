-- Adiciona campos de autenticação aos analistas.
-- Senha padrão dos seeds: "password" (BCrypt strength 10)
ALTER TABLE analysts
    ADD COLUMN email         VARCHAR(255),
    ADD COLUMN password_hash VARCHAR(255),
    ADD COLUMN role          VARCHAR(50) NOT NULL DEFAULT 'ANALYST';

UPDATE analysts SET email = 'ana.oliveira@sus.gov.br',    password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' WHERE name = 'Ana Paula Oliveira';
UPDATE analysts SET email = 'carlos.santos@sus.gov.br',   password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' WHERE name = 'Carlos Eduardo Santos';
UPDATE analysts SET email = 'fernanda.lima@sus.gov.br',   password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' WHERE name = 'Fernanda Lima';
UPDATE analysts SET email = 'marcos.vieira@sus.gov.br',   password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' WHERE name = 'Marcos Vieira';
UPDATE analysts SET email = 'juliana.costa@sus.gov.br',   password_hash = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.' WHERE name = 'Juliana Costa';

INSERT INTO analysts (name, email, password_hash, role, specialty, region, active, created_at, updated_at)
VALUES ('Admin Sistema', 'admin@sus.gov.br', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'ADMIN', 'GESTAO', 'Nacional', TRUE, NOW(), NOW());

ALTER TABLE analysts
    ALTER COLUMN email         SET NOT NULL,
    ALTER COLUMN password_hash SET NOT NULL,
    ADD CONSTRAINT analysts_email_unique UNIQUE (email);
