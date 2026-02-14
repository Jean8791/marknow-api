-- V1__schema.sql
create extension if not exists pgcrypto;

-- =========================
-- PRESTADOR (TENANT)
-- =========================
create table prestador
(
    oid         varchar(36) primary key,
    nome        varchar(120) not null,
    documento   varchar(14),
    tipo_pessoa varchar(2)   not null,
    status      varchar(20)  not null default 'ATIVO',
    criado_por  varchar(36)  null,
    atualizado_por  varchar(36)  null,
    criado_em  timestamptz  not null default now(),
    atualizado_em  timestamptz  not null default now()
);

alter table prestador
    add constraint chk_prestador_tipo_pessoa
        check (tipo_pessoa in ('PF', 'PJ'));

alter table prestador
    add constraint chk_prestador_status
        check (status in ('ATIVO', 'BLOQUEADO'));

create index idx_prestador_documento on prestador (documento);
create index idx_prestador_status on prestador (status);

-- =========================
-- USUARIO
-- =========================
create table usuario
(
    oid        varchar(36) primary key,
    email      varchar(100) not null,
    nome       varchar(120) not null,
    senha      varchar(255),
    status     varchar(20)  not null default 'ATIVO',
    criado_por varchar(36)  null,
    atualizado_por varchar(36)  null,
    criado_em timestamptz  not null default now(),
    atualizado_em timestamptz  not null default now()
);

create unique index uk_usuario_email_ci on usuario (lower(email));
create index idx_usuario_status on usuario (status);

alter table usuario
    add constraint chk_usuario_status
        check (status in ('ATIVO', 'BLOQUEADO'));

-- =========================
-- PRESTADOR_USUARIO (VÍNCULO)
-- =========================
create table prestador_usuario
(
    oid           varchar(36) primary key,
    oid_prestador varchar(36) not null,
    oid_usuario   varchar(36) not null,
    role          varchar(20) not null,
    status        varchar(20) not null default 'ATIVO',
    criado_por    varchar(36) null,
    atualizado_por    varchar(36) null,
    criado_em    timestamptz not null default now(),
    atualizado_em    timestamptz not null default now(),

    constraint fk_prestador_usuario_prestador
        foreign key (oid_prestador) references prestador (oid) on delete cascade,

    constraint fk_prestador_usuario_usuario
        foreign key (oid_usuario) references usuario (oid) on delete cascade
);

create unique index uk_prestador_usuario_prestador_usuario
    on prestador_usuario (oid_prestador, oid_usuario);

create index idx_prestador_usuario_prestador on prestador_usuario (oid_prestador);
create index idx_prestador_usuario_usuario on prestador_usuario (oid_usuario);

alter table prestador_usuario
    add constraint chk_prestador_usuario_role
        check (role in ('ADMIN', 'OPERACIONAL'));

alter table prestador_usuario
    add constraint chk_prestador_usuario_status
        check (status in ('ATIVO', 'BLOQUEADO'));
