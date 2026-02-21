-- V5__checks.sql
-- PostgreSQL CHECK constraints baseadas nos enums do código

-- =========================
-- prestador.tipo_pessoa (TipoPessoa: PF, PJ)
-- =========================
alter table prestador
    add constraint ck_prestador_tipo_pessoa
        check (tipo_pessoa in ('PF', 'PJ'));

-- =========================
-- prestador.status (PrestadorStatus: ATIVO, BLOQUEADO)
-- =========================
alter table prestador
    add constraint ck_prestador_status
        check (status in ('ATIVO', 'BLOQUEADO'));

-- =========================
-- usuario.status (UsuarioStatus: ATIVO, BLOQUEADO)
-- =========================
alter table usuario
    add constraint ck_usuario_status
        check (status in ('ATIVO', 'BLOQUEADO'));

-- =========================
-- assinatura.status (AssinaturaStatus: TRIAL, ATIVA, INADIMPLENTE, SUSPENSA, CANCELADA)
-- =========================
alter table assinatura
    add constraint ck_assinatura_status
        check (status in ('TRIAL', 'ATIVA', 'INADIMPLENTE', 'SUSPENSA', 'CANCELADA'));

-- =========================
-- prestador_usuario.role (PrestadorUsuarioRole: ADMIN, OPERACIONAL)
-- =========================
alter table prestador_usuario
    add constraint ck_prestador_usuario_role
        check (role in ('ADMIN', 'OPERACIONAL'));

-- =========================
-- prestador_usuario.status (PrestadorUsuarioStatus: ATIVO, BLOQUEADO)
-- =========================
alter table prestador_usuario
    add constraint ck_prestador_usuario_status
        check (status in ('ATIVO', 'BLOQUEADO'));

-- =========================
-- plano.status (PlanoStatus: ATIVO, INATIVO)
-- =========================
alter table plano
    add constraint ck_plano_usuario_status
        check (status in ('ATIVO', 'INATIVO'));
