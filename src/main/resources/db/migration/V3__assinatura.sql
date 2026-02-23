create table plano
(
    oid                  varchar(36) primary key,
    criado_em            timestamptz  not null,
    atualizado_em        timestamptz  null,
    nome                 varchar(120) not null,
    limite_agendamentos  integer      not null,
    valor                numeric(15,2) not null,
    status               varchar(20)  not null,
    possui_trial         boolean      default false,
    dias_trial           integer      default 0
);

create table assinatura
(
    oid                 varchar(36) primary key,
    criado_em           timestamptz not null,
    atualizado_em       timestamptz null,

    oid_prestador       varchar(36) not null,
    criado_por          varchar(36) not null,
    atualizado_por      varchar(36) null,

    oid_plano           varchar(36) null,
    status              varchar(20) not null,
    fim_periodo_atual   timestamptz not null,
    tolerancia_ate      timestamptz null,
    provedor            varchar(20) null,
    referencia_provedor varchar(80) null,

    constraint fk_assinatura_prestador
        foreign key (oid_prestador) references prestador (oid),
    constraint fk_assinatura_plano
        foreign key (oid_plano) references plano (oid)
);

create index idx_assinatura_oid_prestador on assinatura (oid_prestador);
create index idx_assinatura_status on assinatura (status);
create index idx_assinatura_fim_periodo on assinatura (fim_periodo_atual);
