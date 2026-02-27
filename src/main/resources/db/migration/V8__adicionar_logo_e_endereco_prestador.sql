-- Adiciona os campos de endereço
ALTER TABLE prestador
    ADD COLUMN cep VARCHAR(9);
ALTER TABLE prestador
    ADD COLUMN logradouro VARCHAR(150);
ALTER TABLE prestador
    ADD COLUMN numero VARCHAR(20);
ALTER TABLE prestador
    ADD COLUMN bairro VARCHAR(100);
ALTER TABLE prestador
    ADD COLUMN cidade VARCHAR(100);
ALTER TABLE prestador
    ADD COLUMN uf VARCHAR(2);
ALTER TABLE prestador
    ADD COLUMN complemento VARCHAR(100);

-- Adiciona a coluna da logo como TEXT para suportar o Base64
ALTER TABLE prestador
    ADD COLUMN logo_url TEXT;

CREATE INDEX idx_prestador_cidade ON prestador (cidade);
CREATE INDEX idx_prestador_cep ON prestador (cep);