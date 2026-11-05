create extension if not exists btree_gist;

create table profissional
(
    oid            varchar(36) primary key,
    criado_em      timestamptz  not null,
    atualizado_em  timestamptz  null,
    oid_prestador  varchar(36)  not null,
    criado_por     varchar(36)  not null,
    atualizado_por varchar(36)  null,

    nome           varchar(120) not null,
    ddd_telefone   varchar(2)   null,
    telefone       varchar(9)   null,
    ativo          boolean      not null default true,

    constraint fk_profissional_prestador foreign key (oid_prestador) references prestador (oid)
);

create index idx_profissional_prestador on profissional (oid_prestador);

create table servico
(
    oid              varchar(36) primary key,
    criado_em        timestamptz   not null,
    atualizado_em    timestamptz   null,
    oid_prestador    varchar(36)   not null,
    criado_por       varchar(36)   not null,
    atualizado_por   varchar(36)   null,

    nome             varchar(120)  not null,
    duracao_minutos  integer       not null,
    valor            numeric(15, 2) null,
    ativo            boolean       not null default true,

    constraint fk_servico_prestador foreign key (oid_prestador) references prestador (oid),
    constraint ck_servico_duracao check (duracao_minutos > 0 and duracao_minutos % 30 = 0 and duracao_minutos <= 480),
    constraint ck_servico_valor check (valor is null or valor >= 0)
);

create index idx_servico_prestador on servico (oid_prestador);

create table disponibilidade
(
    oid              varchar(36) primary key,
    criado_em        timestamptz not null,
    atualizado_em    timestamptz null,
    oid_prestador    varchar(36) not null,
    criado_por       varchar(36) not null,
    atualizado_por   varchar(36) null,

    oid_profissional varchar(36) not null,
    dia_semana       smallint    not null,
    hora_inicio      time        not null,
    hora_fim         time        not null,

    constraint fk_disponibilidade_prestador foreign key (oid_prestador) references prestador (oid),
    constraint fk_disponibilidade_profissional foreign key (oid_profissional) references profissional (oid) on delete cascade,
    constraint ck_disponibilidade_dia check (dia_semana between 1 and 7),
    constraint ck_disponibilidade_intervalo check (hora_fim > hora_inicio),
    constraint ck_disponibilidade_meia_hora check (
        extract(second from hora_inicio) = 0 and extract(minute from hora_inicio) in (0, 30)
            and extract(second from hora_fim) = 0 and extract(minute from hora_fim) in (0, 30)
        )
);

create index idx_disponibilidade_profissional on disponibilidade (oid_profissional, dia_semana);

create table agendamento
(
    oid              varchar(36) primary key,
    criado_em        timestamptz  not null,
    atualizado_em    timestamptz  null,
    oid_prestador    varchar(36)  not null,
    criado_por       varchar(36)  not null,
    atualizado_por   varchar(36)  null,

    oid_profissional varchar(36)  not null,
    oid_servico      varchar(36)  not null,
    cliente_nome     varchar(120) not null,
    cliente_ddd      varchar(2)   not null,
    cliente_telefone varchar(9)   not null,
    observacao       varchar(500) null,
    inicio           timestamptz  not null,
    fim              timestamptz  not null,
    status           varchar(20)  not null,
    whatsapp_status  varchar(20)  not null default 'NAO_ENVIADO',
    cancelado_em     timestamptz  null,

    constraint fk_agendamento_prestador foreign key (oid_prestador) references prestador (oid),
    constraint fk_agendamento_profissional foreign key (oid_profissional) references profissional (oid),
    constraint fk_agendamento_servico foreign key (oid_servico) references servico (oid),
    constraint ck_agendamento_intervalo check (fim > inicio),
    constraint ck_agendamento_status check (status in ('AGENDADO', 'CANCELADO')),
    constraint ck_agendamento_whatsapp check (whatsapp_status in ('NAO_ENVIADO', 'ENVIADO_MANUAL')),
    constraint ex_agendamento_sem_sobreposicao exclude using gist (
        oid_profissional with =,
        tstzrange(inicio, fim, '[)') with &&
        ) where (status = 'AGENDADO')
);

create index idx_agendamento_prestador_inicio on agendamento (oid_prestador, inicio);
create index idx_agendamento_profissional_inicio on agendamento (oid_profissional, inicio);
