-- Função para remover frases expiradas
CREATE OR REPLACE FUNCTION remover_frases_expiradas()
RETURNS void AS $$
BEGIN
    DELETE FROM frase_anonima
    WHERE data_expiracao < NOW();
END;
$$ LANGUAGE plpgsql;