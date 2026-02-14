-- V2__super_admin.sql

alter table usuario
    add column admin boolean not null default false;

-- garante que só exista UM super admin
create unique index uk_admin
    on usuario ((1))
    where admin = true;

-- seed do admin global (OID fixo)
insert into usuario (oid, email, nome, senha, status, admin, criado_por, criado_em, atualizado_em)
values ('00000000-0000-0000-0000-000000000001',
        'admin@marknow.local',
        'Administrador Global',
        null,
        'ATIVO',
        true,
        null,
        now(),
        now())
on conflict (lower(email)) do nothing;
