# PRD — MarkNow (Starter) v1.0
**Status:** Aprovado para kickoff (escopo do MVP travado)  
**Modelo:** SaaS (multi-tenant por Prestador)  
**Entrada:** Closed Beta por convite (single-use)  
**WhatsApp:** Manual via link (sem API no MVP)  
**Slots:** Fixos de 30 minutos (no MVP)

---

## 1) Visão
Criar um SaaS de agenda interna para prestadores de serviços no Brasil, com múltiplos profissionais e múltiplos serviços, operado por equipe (admin/operacional), com comunicação via WhatsApp com 1 clique (manual), garantindo controle de custos e estabilidade via planos e quotas.

---

## 2) Problema
Prestadores gerenciam agenda no WhatsApp/caderno e sofrem com:
- conflitos de horário
- retrabalho de remarcação/cancelamento
- falta de governança (quem pode mexer na agenda)
- operação pouco escalável

---

## 3) Público-alvo
Pequenos negócios com agenda recorrente e mais de 1 pessoa atendendo:
- estética/salões
- clínicas pequenas
- serviços técnicos

---

## 4) Objetivos e sucesso do MVP
### Objetivos
- Agendamento interno rápido e sem conflito
- Onboarding controlado (beta fechado por convite)
- WhatsApp presente sem automação/custo imprevisível

### Métricas mínimas
- **0** sobreposições por profissional
- marcação em **< 30s** (interno)
- setup completo do prestador em **< 30 min**
- baixo número de **bloqueios por quota**

---

## 5) Escopo do MVP

### 5.1 Inclui

#### A) Acesso e controle
- Cadastro por **convite** (single-use)
- **Planos** configuráveis apenas pelo **System Admin**
- **Quotas por plano** com:
    - avisar em **80%**
    - bloquear em **100%** (bloqueio server-side)
- Status de assinatura por prestador:
    - `TRIAL`, `ATIVA`, `SUSPENSA`, `CANCELADA`

#### B) Operação de agenda
- CRUD de **profissionais**
- CRUD de **serviços**
- **Disponibilidade** por janelas (ex.: 08–12, 14–18)
- **Slots do sistema fixos:** 30 min
- Agendamento interno:
    - criar / remarcar / cancelar
- Cliente obrigatório no agendamento:
    - **nome + telefone**
- Visualização:
    - agenda por profissional (dia/semana simples)
- Auditoria básica:
    - quem fez o quê e quando

#### C) WhatsApp
- WhatsApp **manual** via link (deep link `wa.me`) com texto pré-preenchido:
    - para **cliente**
    - para **operacional/profissional** (interno)

---

### 5.2 Não inclui (fora do MVP)
- Link público de autoagendamento
- WhatsApp automático via API (apenas piloto futuro)
- Pagamento integrado
- Integrações externas (Google Calendar etc.)
- CRM/marketing avançado

---

## 6) Papéis e permissões

### System Admin (interno)
- CRUD Planos
- Criar convites
- Gerenciar prestadores/assinaturas
- Suporte: reset de credenciais

### Admin do Prestador
- Configurar profissionais/serviços/disponibilidade
- Criar usuários operacionais do mesmo prestador

### Operacional
- Criar/remarcar/cancelar agendamentos
- Enviar WhatsApp manual (cliente e interno)

> Observação: **Usuário ≠ Profissional.** Usuário executa ações; Profissional é o “recurso de agenda”.

---

## 7) Regras de negócio críticas

### Slot e janelas
- Slot fixo no MVP: **30 min**
- Janelas são definidas pelo usuário (HH:mm)
- Validações obrigatórias:
    - `hora_fim > hora_inicio`
    - minutos somente **00 ou 30**
    - duração da janela múltipla de 30

### Conflito de agenda
- Não permitir sobreposição por profissional
- Garantia no Postgres via **EXCLUDE constraint** (intervalo com `tstzrange`)

### Quotas (avisar e bloquear)
- 80%: alertar no sistema
- 100%: bloquear a ação que consome a quota
    - ex.: criar agendamento se estourou `agendamentos/mês`

### WhatsApp (MVP)
- Apenas manual via link
- Sistema mantém status de comunicação (mínimo):
    - `NAO_ENVIADO`, `ENVIADO_MANUAL`

---

## 8) Plano Beta (padrão)
**Nome:** BETA  
**Limites:**
- Profissionais: **5**
- Usuários operacionais: **5**
- Serviços: **20**
- Agendamentos/mês: **500**
  **Features:**
- WhatsApp manual: **habilitado**
- WhatsApp automático: **desabilitado**
  **Quota policy:** avisar 80% / bloquear 100%

---

## 9) Riscos e mitigação
- **Custo imprevisível de mensagens:** mitigado por WhatsApp manual no MVP + quotas por plano
- **Instabilidade por escala:** mitigado por beta fechado + rate limit + métricas básicas
- **Complexidade excessiva:** mitigado por slot fixo 30 min e sem link público

---

## 10) Roadmap (3 fases)
- **Fase 1 (MVP Beta):** convites + planos/quotas + agenda interna + WhatsApp manual
- **Fase 2:** melhorias operacionais (relatórios simples, busca, auditoria visível)
- **Fase 3:** piloto WhatsApp automático (fila + limites) e/ou cobrança integrada

---

## 11) Checklist de PRD aprovado
O PRD está pronto para iniciar quando:
- Escopo do MVP está fechado (inclui/não inclui)
- Limites do Plano Beta estão definidos
- Papéis e permissões estão definidos
- Regras de slot/janelas e conflito estão escritas
- Métricas mínimas de sucesso estão definidas
