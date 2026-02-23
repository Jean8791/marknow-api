create table convite
(
    oid                varchar(36) primary key,
    criado_em          timestamptz not null,
    atualizado_em      timestamptz null,
    codigo             varchar(20) not null unique,
    descricao          varchar(255),
    email_destinatario varchar(120),
    expira_em          timestamptz not null,
    utilizado_em       timestamptz,
    oid_usuario_criacao varchar(36),
    max_usos           integer     not null default 1,
    qtd_usado          integer     not null default 0
);

create index idx_convite_codigo on convite (codigo);