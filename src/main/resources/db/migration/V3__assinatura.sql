create table assinatura
(
    oid                 varchar(36) primary key,
    criado_em           timestamptz not null,
    atualizado_em       timestamptz null,

    oid_prestador       varchar(36) not null,
    criado_por          varchar(36) not null,
    atualizado_por      varchar(36) null,

    plano               varchar(30) not null,
    status              varchar(20) not null,
    fim_periodo_atual   timestamptz not null,
    tolerancia_ate      timestamptz null,
    provedor            varchar(20) null,
    referencia_provedor varchar(80) null,

    constraint fk_assinatura_prestador
        foreign key (oid_prestador) references prestador (oid)
);

create index idx_assinatura_oid_prestador on assinatura (oid_prestador);
create index idx_assinatura_status on assinatura (status);
create index idx_assinatura_fim_periodo on assinatura (fim_periodo_atual);
