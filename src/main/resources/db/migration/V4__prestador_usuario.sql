create table prestador_usuario
(
    oid           varchar(36) primary key,
    criado_em     timestamptz not null,
    atualizado_em timestamptz null,

    oid_prestador varchar(36) not null,
    oid_usuario   varchar(36) not null,

    role          varchar(20) not null,
    status        varchar(20) not null,

    constraint fk_prestador_usuario_prestador
        foreign key (oid_prestador) references prestador (oid),

    constraint fk_prestador_usuario_usuario
        foreign key (oid_usuario) references usuario (oid),

    constraint uk_prestador_usuario
        unique (oid_prestador, oid_usuario)
);

create index idx_pu_oid_prestador on prestador_usuario (oid_prestador);
create index idx_pu_oid_usuario on prestador_usuario (oid_usuario);
create index idx_pu_status on prestador_usuario (status);
