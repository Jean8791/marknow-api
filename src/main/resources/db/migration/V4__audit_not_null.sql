-- V4__audit_not_null.sql

-- usuário ainda pode ter created_by null (bootstrap / import), se você quiser:
-- alter table usuario alter column created_by set not null;  -- NÃO recomendo agora

alter table prestador
    alter column criado_por set not null;
alter table prestador_usuario
    alter column criado_por set not null;
alter table assinatura
    alter column criado_por set not null;
