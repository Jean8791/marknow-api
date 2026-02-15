create table prestador
(
    oid           varchar(36) primary key,
    criado_em     timestamptz  not null,
    atualizado_em timestamptz  null,

    nome          varchar(120) not null,
    documento     varchar(14)  null,
    tipo_pessoa   varchar(2)   not null,
    status        varchar(20)  not null
);

create index idx_prestador_documento on prestador (documento);
create index idx_prestador_status on prestador (status);
