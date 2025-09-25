-- ======================
-- Tabela: Usuario
-- ======================
CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    foto_perfil VARCHAR(255),
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ======================
-- Tabela: Post
-- ======================
CREATE TABLE IF NOT EXISTS post (
    id SERIAL PRIMARY KEY,
    usuario_id INT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    texto VARCHAR(280) NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_post_usuario ON post(usuario_id);

-- ======================
-- Tabela: Seguidor
-- ======================
CREATE TABLE IF NOT EXISTS seguidor (
    id SERIAL PRIMARY KEY,
    seguidor_id INT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    seguido_id INT NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT uq_seguidor UNIQUE(seguidor_id, seguido_id),
    CONSTRAINT chk_self_follow CHECK (seguidor_id <> seguido_id)
);

CREATE INDEX IF NOT EXISTS idx_seguidor_id ON seguidor(seguidor_id);
CREATE INDEX IF NOT EXISTS idx_seguido_id ON seguidor(seguido_id);

-- ======================
-- Tabela: FraseAnonima
-- ======================
CREATE TABLE IF NOT EXISTS frase_anonima (
    id SERIAL PRIMARY KEY,
    texto VARCHAR(280) NOT NULL,
    data_criacao TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    data_expiracao TIMESTAMP NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_frase_expiracao ON frase_anonima(data_expiracao);

-- OBS: substituido pela classe functionInitializer no diretorio configs
-- CREATE OR REPLACE FUNCTION remover_frases_expiradas()
-- RETURNS void AS $$
-- BEGIN
--    DELETE FROM frase_anonima
--    WHERE data_expiracao < NOW();
-- END;
-- $$ LANGUAGE plpgsql;

-- primeiro usuario
INSERT INTO public.usuario(
	nome, email, senha_hash, foto_perfil, data_criacao)
	VALUES ('Mano corbas', 'vini@exemplo.com', '$2a$10$...', 'vini.png', '2025-09-09 21:12');

-- segundo usuario
INSERT INTO public.usuario(
	nome, email, senha_hash, foto_perfil, data_criacao)
	VALUES ('Mano corbas2', 'vini2@exemplo.com', '$2a$10$...2', 'vini2.png', '2025-09-09 21:22');

-- Usuario 2 agora segue o usuario 1
INSERT INTO public.seguidor(
	seguidor_id, seguido_id)
	VALUES (2, 1);

-- primeiro post
INSERT INTO public.post(
	usuario_id, texto, data_criacao)
	VALUES (2, 'PBZCHGNQBERF FNB YRTNVF', NOW());

-- frase anonima
INSERT INTO public.frase_anonima(
	texto, data_criacao, data_expiracao)
	VALUES ('So sei que nada sei', NOW(), '2026-01-01');