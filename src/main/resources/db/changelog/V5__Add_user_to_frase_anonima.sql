ALTER TABLE frase_anonima
ADD COLUMN usuario_id BIGINT NOT NULL;

ALTER TABLE frase_anonima
ADD CONSTRAINT fk_frase_anonima_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuario(id);
