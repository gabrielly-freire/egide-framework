-- O hash original em V4 não correspondia à senha "password" (verificado com bcrypt).
-- Hash correto para a senha "password", gerado com BCrypt strength 10.
UPDATE analysts
SET password_hash = '$2a$10$IHJbA0ZKwdcZazqMHc6dReJtOiZtR.0C5TNX.KlhnjfG/kMqVOB/O'
WHERE email IN (
    'ana.oliveira@sus.gov.br',
    'carlos.santos@sus.gov.br',
    'fernanda.lima@sus.gov.br',
    'marcos.vieira@sus.gov.br',
    'juliana.costa@sus.gov.br',
    'admin@sus.gov.br'
);
