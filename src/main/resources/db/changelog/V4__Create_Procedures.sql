-- Procedimento para remover frases expiradas
CREATE OR REPLACE PROCEDURE remover_frases_expiradas()
LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM frase_anonima
    WHERE data_expiracao < NOW();
END;
$$;
