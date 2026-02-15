create table usuario
(
    oid            varchar(36) primary key,
    criado_em      timestamptz  not null,
    atualizado_em  timestamptz  null,

    oid_prestador  varchar(36)  not null,
    criado_por     varchar(36)  not null,
    atualizado_por varchar(36)  null,

    email          varchar(100) not null,
    nome           varchar(120) not null,
    senha          text         null,
    status         varchar(20)  not null,
    admin          boolean      null,

    constraint fk_usuario_prestador
        foreign key (oid_prestador) references prestador (oid),

    constraint uk_usuario_prestador_email
        unique (oid_prestador, email)
);

create index idx_usuario_oid_prestador on usuario (oid_prestador);
create index idx_usuario_email on usuario (email);
create index idx_usuario_status on usuario (status);
