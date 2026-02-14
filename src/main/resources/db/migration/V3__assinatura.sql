-- V3__assinatura.sql

create table assinatura
(
    oid                varchar(36) primary key,
    oid_prestador      varchar(36) not null,
    plano              varchar(30) not null,
    status             varchar(20) not null,
    fim_periodo_atual timestamptz not null,
    tolerancia_ate        timestamptz null,
    provedor           varchar(20) null,
    referencia_provedor       varchar(80) null,
    criado_por         varchar(36) null,
    atualizado_por         varchar(36) null,
    criado_em         timestamptz not null default now(),
    atualizado_em         timestamptz not null default now(),

    constraint fk_assinatura_prestador
        foreign key (oid_prestador) references prestador (oid) on delete cascade
);

-- 1 assinatura por prestador
create unique index uk_assinatura_prestador on assinatura (oid_prestador);
create index idx_assinatura_status on assinatura (status);
create index idx_assinatura_period_fim on assinatura (fim_periodo_atual);

alter table assinatura
    add constraint chk_assinatura_status
        check (status in ('TRIAL', 'ATIVA', 'INADIMPLENTE', 'SUSPENSA', 'CANCELADA'));
