-- Adiciona os campos de email e telefone celular prestador
ALTER TABLE prestador ADD COLUMN email VARCHAR(120);
ALTER TABLE prestador ADD COLUMN ddd_telefone VARCHAR(5);
ALTER TABLE prestador ADD COLUMN telefone VARCHAR(10);
ALTER TABLE prestador ADD COLUMN ddd_celular VARCHAR(5);
ALTER TABLE prestador ADD COLUMN celular VARCHAR(10);
-- Adiciona os campos de telefone celular documento usuario
ALTER TABLE usuario ADD COLUMN documento VARCHAR(14);
ALTER TABLE usuario ADD COLUMN tipo_pessoa VARCHAR(2) not null default 'PF';
ALTER TABLE usuario ADD COLUMN ddd_telefone VARCHAR(5);
ALTER TABLE usuario ADD COLUMN telefone VARCHAR(10);
ALTER TABLE usuario ADD COLUMN ddd_celular VARCHAR(5);
ALTER TABLE usuario ADD COLUMN celular VARCHAR(10);
ALTER TABLE usuario ADD COLUMN imagem TEXT;
