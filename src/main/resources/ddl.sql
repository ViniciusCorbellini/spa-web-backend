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