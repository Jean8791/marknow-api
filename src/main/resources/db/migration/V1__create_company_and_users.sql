-- V1__create_company_and_users.sql
-- PostgreSQL

-- =========================
-- COMPANY (GLOBAL)
-- =========================
create table company
(
    oid        varchar(36) primary key,
    name       varchar(120) not null,
    cnpj       varchar(14),
    status     varchar(20)  not null default 'ACTIVE',

    created_by varchar(36),
    updated_by varchar(36),
    created_at timestamptz  not null default now(),
    updated_at timestamptz  not null default now(),

    constraint chk_company_status
        check (status in ('ACTIVE', 'DISABLED'))
);

-- unique cnpj (permite múltiplos NULL)
create unique index uk_company_cnpj on company (cnpj);
create index idx_company_status on company (status);


-- =========================
-- USER_ACCOUNT (GLOBAL)
-- =========================
create table user_account
(
    oid           varchar(36) primary key,
    email         varchar(255) not null,
    full_name     varchar(120) not null,
    password_hash varchar(255) not null,
    status        varchar(20)  not null default 'ACTIVE',

    created_by    varchar(36),
    updated_by    varchar(36),
    created_at    timestamptz  not null default now(),
    updated_at    timestamptz  not null default now(),

    constraint chk_user_account_status
        check (status in ('ACTIVE', 'DISABLED'))
);

-- unique case-insensitive para email
create unique index uk_user_account_email_ci on user_account (lower(email));
create index idx_user_account_status on user_account (status);


-- =========================
-- COMPANY_USER (VÍNCULO)
-- =========================
create table company_user
(
    oid         varchar(36) primary key,

    company_oid varchar(36) not null,
    user_oid    varchar(36) not null,

    role        varchar(20) not null,
    status      varchar(20) not null default 'ACTIVE',

    created_by  varchar(36),
    updated_by  varchar(36),
    created_at  timestamptz not null default now(),
    updated_at  timestamptz not null default now(),

    constraint fk_company_user_company
        foreign key (company_oid) references company (oid) on delete cascade,

    constraint fk_company_user_user
        foreign key (user_oid) references user_account (oid) on delete cascade,

    constraint chk_company_user_role
        check (role in ('ADMIN', 'MEMBER')),

    constraint chk_company_user_status
        check (status in ('ACTIVE', 'INVITED', 'DISABLED'))
);

-- impede duplicar o mesmo usuário na mesma empresa
create unique index uk_company_user_company_user
    on company_user (company_oid, user_oid);

create index idx_company_user_company_status on company_user (company_oid, status);
create index idx_company_user_user on company_user (user_oid);
create index idx_company_user_role on company_user (role);
