-- ======================
-- Usuários iniciais
-- ======================
INSERT INTO usuario (nome, email, senha_hash, foto_perfil, data_criacao)
VALUES
  ('Mano corbas', 'vini@exemplo.com', '$2a$10$...', 'vini.png', '2025-09-09 21:12'),
  ('Mano corbas2', 'vini2@exemplo.com', '$2a$10$...2', 'vini2.png', '2025-09-09 21:22');

-- ======================
-- Relação de seguidores
-- ======================
INSERT INTO seguidor (seguidor_id, seguido_id)
VALUES (2, 1);

-- ======================
-- Posts iniciais
-- ======================
INSERT INTO post (usuario_id, texto, data_criacao)
VALUES (2, 'PBZCHGNQBERF FNB YRTNVF', NOW());

-- ======================
-- Frases anônimas
-- ======================
INSERT INTO frase_anonima (texto, data_criacao, data_expiracao, usuario_id)
VALUES ('So sei que nada sei', NOW(), '2026-01-01', 1);


-- obs: tive que resetar o banco no render. Portanto vou simplesmente mudar as 
-- migrações. Agora o bd já cria a tabela frase_anonima corretamente
-- e insere os dados certinho.
-- essa é a melhor solução? talvez não, mas é a mais conveniente