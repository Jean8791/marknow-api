-- V4__audit_not_null.sql

-- usuário ainda pode ter created_by null (bootstrap / import), se você quiser:
-- alter table usuario alter column created_by set not null;  -- NÃO recomendo agora

update prestador set criado_por = '00000000-0000-0000-0000-000000000001' where criado_por is null;
update prestador_usuario set criado_por = '00000000-0000-0000-0000-000000000001' where criado_por is null;
update assinatura set criado_por = '00000000-0000-0000-0000-000000000001' where criado_por is null;


alter table prestador
    alter column criado_por set not null;
alter table prestador_usuario
    alter column criado_por set not null;
alter table assinatura
    alter column criado_por set not null;
