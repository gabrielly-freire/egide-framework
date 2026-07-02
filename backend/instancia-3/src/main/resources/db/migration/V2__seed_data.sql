-- Senha padrão de todos os seeds abaixo: "password" (BCrypt strength 10)
INSERT INTO analysts (name, email, password_hash, role, specialty, region, active, created_at, updated_at) VALUES
    ('Ana Paula Oliveira',    'ana.oliveira@sus.gov.br',  '$2a$10$IHJbA0ZKwdcZazqMHc6dReJtOiZtR.0C5TNX.KlhnjfG/kMqVOB/O', 'ANALYST', 'SAUDE',              'Norte',        TRUE, NOW(), NOW()),
    ('Carlos Eduardo Santos', 'carlos.santos@sus.gov.br', '$2a$10$IHJbA0ZKwdcZazqMHc6dReJtOiZtR.0C5TNX.KlhnjfG/kMqVOB/O', 'ANALYST', 'EDUCACAO',           'Nordeste',     TRUE, NOW(), NOW()),
    ('Fernanda Lima',         'fernanda.lima@sus.gov.br', '$2a$10$IHJbA0ZKwdcZazqMHc6dReJtOiZtR.0C5TNX.KlhnjfG/kMqVOB/O', 'ANALYST', 'INFRAESTRUTURA',     'Centro-Oeste', TRUE, NOW(), NOW()),
    ('Marcos Vieira',         'marcos.vieira@sus.gov.br', '$2a$10$IHJbA0ZKwdcZazqMHc6dReJtOiZtR.0C5TNX.KlhnjfG/kMqVOB/O', 'ANALYST', 'ASSISTENCIA_SOCIAL', 'Sudeste',      TRUE, NOW(), NOW()),
    ('Juliana Costa',         'juliana.costa@sus.gov.br', '$2a$10$IHJbA0ZKwdcZazqMHc6dReJtOiZtR.0C5TNX.KlhnjfG/kMqVOB/O', 'ANALYST', 'MEIO_AMBIENTE',      'Sul',          TRUE, NOW(), NOW()),
    ('Admin Sistema',         'admin@sus.gov.br',         '$2a$10$IHJbA0ZKwdcZazqMHc6dReJtOiZtR.0C5TNX.KlhnjfG/kMqVOB/O', 'ADMIN',   'GESTAO',             'Nacional',     TRUE, NOW(), NOW());
