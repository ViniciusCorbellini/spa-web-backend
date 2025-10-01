-- Índices para Post
CREATE INDEX IF NOT EXISTS idx_post_usuario ON post(usuario_id);

-- Índices para Seguidor
CREATE INDEX IF NOT EXISTS idx_seguidor_id ON seguidor(seguidor_id);
CREATE INDEX IF NOT EXISTS idx_seguido_id ON seguidor(seguido_id);

-- Índice para FraseAnonima
CREATE INDEX IF NOT EXISTS idx_frase_expiracao ON frase_anonima(data_expiracao);