CREATE EXTENSION IF NOT EXISTS unaccent;

INSERT INTO usuarios (nome, email, login, senha, tipo, data_criacao, data_ultima_alteracao)
VALUES (
    'Administrador',
    'admin@gastrohub.com',
    'admin',
    '$argon2id$v=19$m=65536,t=3,p=1$Z2FzdHJvaHVic2FsdDE2Yg$q6v+dTe1os0BL+6L2m/Fsv+rEuqDXpAfAE5iPxFSG5o',
    'ADMIN',
    NOW(),
    NOW()
) ON CONFLICT (login) DO NOTHING;
