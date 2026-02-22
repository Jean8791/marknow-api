create table usuario
(
    oid           varchar(36) primary key,
    criado_em     timestamptz  not null,
    atualizado_em timestamptz  null,

    email         varchar(100) not null,
    nome          varchar(120) not null,
    senha         text         null,
    status        varchar(20)  not null,
    admin         boolean      null
);

create index idx_usuario_email on usuario (email);
create index idx_usuario_status on usuario (status);
