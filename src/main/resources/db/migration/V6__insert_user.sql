-- 1. Criar o Utilizador Super Admin (Sem vínculo direto a prestador na tabela usuario)
INSERT INTO usuario (oid,
                     criado_em,
                     email,
                     nome,
                     senha,
                     status,
                     admin)
VALUES ('00000000-0000-0000-0000-000000000000',
        NOW(),
        'admin@marknow.com.br',
        'Super Administrador MarkNow',
        '$2a$10$587vl1BUe/QgE2TnEDMMJuJ/.CZTYfxs0/8tRDtj4AxV.9IkzqSmG', -- admin123
        'ATIVO',
        true);
