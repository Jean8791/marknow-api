-- V2__create_prestador_usuario.sql
-- PostgreSQL

-- =========================
-- PRESTADOR
-- =========================
create table prestador
(
    oid         varchar(36) primary key,
    nome        varchar(120) not null,
    documento   varchar(14),
    tipo_pessoa varchar(2)   not null,
    status      varchar(20)  not null default 'ATIVO',
    created_at  timestamptz  not null default now(),
    updated_at  timestamptz  not null default now()
);

-- Se você tiver enum TipoPessoa com valores tipo "PF" e "PJ"
alter table prestador
    add constraint chk_prestador_tipo_pessoa
        check (tipo_pessoa in ('PF', 'PJ'));

-- Ajuste os valores conforme seu enum PrestadorStatus
alter table prestador
    add constraint chk_prestador_status
        check (status in ('ATIVO', 'INATIVO', 'BLOQUEADO'));

-- Index para busca por documento (não força unicidade por padrão)
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
    created_at timestamptz  not null default now(),
    updated_at timestamptz  not null default now()
);

-- Unique case-insensitive para email
create unique index uk_usuario_email_ci on usuario (lower(email));
create index idx_usuario_status on usuario (status);

-- Ajuste os valores conforme seu enum UsuarioStatus
alter table usuario
    add constraint chk_usuario_status
        check (status in ('ATIVO', 'INATIVO', 'BLOQUEADO'));


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
    created_at    timestamptz not null default now(),
    updated_at    timestamptz not null default now(),

    constraint fk_prestador_usuario_prestador
        foreign key (oid_prestador) references prestador (oid) on delete cascade,

    constraint fk_prestador_usuario_usuario
        foreign key (oid_usuario) references usuario (oid) on delete cascade
);

-- Impede duplicar o mesmo usuário no mesmo prestador
create unique index uk_prestador_usuario_prestador_usuario
    on prestador_usuario (oid_prestador, oid_usuario);

create index idx_prestador_usuario_prestador on prestador_usuario (oid_prestador);
create index idx_prestador_usuario_usuario on prestador_usuario (oid_usuario);
create index idx_prestador_usuario_role on prestador_usuario (role);
create index idx_prestador_usuario_status on prestador_usuario (status);

-- Ajuste conforme seus enums:
alter table prestador_usuario
    add constraint chk_prestador_usuario_role
        check (role in ('ADMIN', 'OPERACIONAL'));

alter table prestador_usuario
    add constraint chk_prestador_usuario_status
        check (status in ('ATIVO', 'INATIVO', 'BLOQUEADO'));
