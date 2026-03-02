-- 1. Remove a restrição antiga (o nome ck_assinatura_status foi identificado no seu erro)
ALTER TABLE assinatura
    DROP CONSTRAINT IF EXISTS ck_assinatura_status;

-- 2. Cria a nova restrição incluindo o status 'ATRASADO'
ALTER TABLE assinatura
    ADD CONSTRAINT ck_assinatura_status
        CHECK (status IN ('TRIAL', 'ATIVA', 'ATRASADO', 'INADIMPLENTE', 'SUSPENSA', 'CANCELADA'));
