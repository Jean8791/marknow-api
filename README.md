# marknow-api

API do **MarkNow**, um SaaS de agenda para prestadores de serviço (salões, clínicas pequenas, serviços técnicos) com vários profissionais atendendo. O produto é multi-tenant por prestador, com entrada em beta fechado por convite e cobrança por planos.

O front-end está em [marknow-web](https://github.com/Jean8791/marknow-web).

> **Status:** MVP funcional. Contas, planos, assinaturas, convites e a agenda interna (profissionais, serviços, disponibilidade e agendamentos) estão prontos. O escopo completo está no [PRD](docs/PRD.md) e as decisões técnicas em [DECISIONS.md](DECISIONS.md).

## Stack

- **Java 17** e **Quarkus 3**
- Quarkus REST + Jackson, Hibernate ORM com Panache e Hibernate Validator
- **PostgreSQL 16** com migrations no **Flyway**
- Autenticação **JWT** (SmallRye JWT)
- Quarkus Scheduler, SmallRye Health e **OpenAPI/Swagger**
- Docker Compose para os bancos de dev e de teste

## Funcionalidades

- **Autenticação:** login com JWT e cadastro de prestador a partir de um **convite** (código alfanumérico de 10 caracteres, com validade e limite de usos), escolhendo um dos planos ativos (`GET /auth/planos`).
- **Limite de tentativas:** `POST /auth/*` aceita 10 tentativas por IP a cada 15 minutos (configurável); acima disso responde `429` com `Retry-After`.
- **Multi-tenant:** um filtro de requisição identifica o usuário e o prestador pelo token, confere se o usuário está ativo e se a assinatura do prestador permite o acesso. Usuários e dados do prestador só são encontrados dentro do prestador logado; o super admin opera fora do tenant.
- **Perfis:** super admin (plataforma), `ADMIN` e `OPERACIONAL` (por prestador). O admin do prestador gerencia os usuários da unidade, mas não pode excluir nem bloquear a si mesmo.
- **Planos e assinaturas:** planos configuráveis pelo super admin e assinaturas com ciclo `TRIAL → ATIVA → ATRASADO → INADIMPLENTE`, além de `SUSPENSA` e `CANCELADA`.
- **Agenda interna (slots de 30 minutos):**
  - **profissionais** com disponibilidade semanal em janelas (ex.: seg 08:00–12:00 e 14:00–18:00), sempre em :00 ou :30;
  - **serviços** com duração múltipla de 30 minutos;
  - **agendamentos** com cliente (nome e WhatsApp) obrigatório: criar, remarcar (horário e/ou profissional) e cancelar, sempre dentro da disponibilidade e nunca no passado;
  - cálculo de **horários livres** por profissional, serviço e data;
  - **sem sobreposição garantida no banco:** constraint `EXCLUDE USING gist` com `tstzrange(inicio, fim)` por profissional, valendo só para agendamentos ativos. A API confere antes e, se duas requisições concorrentes passarem juntas, o Postgres barra a segunda (SQLSTATE `23P01`), que vira `409`;
  - **quota mensal do plano:** aviso a partir de 80% e bloqueio de novos agendamentos em 100%;
  - **WhatsApp manual:** o front monta o link `wa.me` com a mensagem pronta e a API registra o status (`NAO_ENVIADO` / `ENVIADO_MANUAL`);
  - fuso fixo `America/Sao_Paulo`: a API recebe e devolve horários locais (`2026-11-10T09:00`) e grava `timestamptz`.
- **Cobrança automática de status:** um job agendado a cada 5 minutos verifica vencimento e período de tolerância. Passado o vencimento, a assinatura fica atrasada; passada a tolerância, ela fica inadimplente e o prestador é bloqueado.
- **Cadastros:** prestadores (com logo e endereço), usuários e convites.
- **Validações próprias:** CPF/CNPJ, telefone brasileiro e e-mail.
- **Erros padronizados** com códigos de erro e **correlation id** em toda resposta, para rastrear requisições nos logs.

## Arquitetura

```
br.com.codenest.marknow
├── resource/     endpoints REST
├── service/      regras de negócio (auth, assinatura, convite, cadastro, agenda...)
├── repository/   acesso a dados com Panache
├── entity/       entidades JPA (com entidade base por tenant)
├── dto/          requests e responses por domínio
├── mapper/       conversão entidade ↔ DTO
├── security/     filtro de tenant e assinatura
├── validation/   anotações de validação (CPF/CNPJ, telefone, e-mail)
├── exception/    mapeadores de exceção e correlation id
└── sql/ util/    consultas dinâmicas e utilitários
```

## Endpoints

| Recurso | Rotas |
|---|---|
| Autenticação | `POST /auth/login` · `POST /auth/cadastro` |
| Autenticação (públicas) | `GET /auth/planos` |
| Prestadores | `GET/POST /prestador` · `DELETE /prestador/{oid}` (super admin) · `GET/PUT /prestador/{oid}` (admin do próprio prestador) |
| Usuários | `GET/POST /usuario` · `GET/PUT/DELETE /usuario/{oid}` (admin do prestador) |
| Planos | `GET/POST /plano` · `GET/PUT /plano/{oid}` (super admin) |
| Assinaturas | `GET /assinatura` (super admin) |
| Convites | `GET/POST /convite` (super admin) |
| Profissionais | `GET /profissional` · `GET /profissional/{oid}` (admin e operacional) · `POST /profissional` · `PUT/DELETE /profissional/{oid}` (admin) |
| Serviços | `GET /servico` · `GET /servico/{oid}` (admin e operacional) · `POST /servico` · `PUT/DELETE /servico/{oid}` (admin) |
| Agendamentos | `GET /agendamento?de=&ate=&oidProfissional=` · `GET /agendamento/horarios-livres` · `GET /agendamento/uso` · `POST /agendamento` · `PUT /agendamento/{oid}/remarcar` · `POST /agendamento/{oid}/cancelar` · `POST /agendamento/{oid}/whatsapp-enviado` (admin e operacional) |

As listagens são paginadas (`page`, `size`), aceitam `search` e ordenam apenas por campos de uma lista permitida (`sort`).

A documentação interativa fica em `/q/swagger-ui` (em dev, `http://localhost:9696/q/swagger-ui`).

## Como rodar

### Pré-requisitos

- JDK 17+ (compila também no JDK 25)
- Docker e Docker Compose

### 1. Subir os bancos

O `docker-compose.yaml` lê as variáveis do Postgres de `../.env.development` e `../.env.test`. Crie esses arquivos com, por exemplo:

```env
POSTGRES_DB=marknow
POSTGRES_USER=marknow
POSTGRES_PASSWORD=marknow
```

E suba:

```bash
docker compose up -d
```

O banco de dev fica em `localhost:5432` e o de testes em `localhost:5433`.

### 2. Gerar o par de chaves do JWT

As chaves não são versionadas. Gere um par em `src/main/resources/jwt/`:

```bash
mkdir -p src/main/resources/jwt && cd src/main/resources/jwt
openssl genrsa -out rsaPrivateKey.pem 2048
openssl pkcs8 -topk8 -nocrypt -inform pem -in rsaPrivateKey.pem -outform pem -out privateKey.pem
openssl rsa -pubout -in privateKey.pem -out publicKey.pem
rm rsaPrivateKey.pem
```

### 3. Rodar em modo dev

```bash
./mvnw quarkus:dev
```

A API sobe em `http://localhost:9696`. O Flyway roda as migrations na inicialização e cria um super admin de bootstrap (`admin@marknow.com.br`, veja `V6__insert_user.sql`), com a senha `admin123` só para uso local. Planos novos nascem inativos: ative um plano antes de testar o cadastro por convite. A migration `V11__agenda.sql` cria a extensão `btree_gist`, usada pela constraint de não sobreposição; o usuário do banco precisa de permissão para criar extensões (o Postgres do `docker-compose` e o Neon permitem).

### Variáveis de ambiente (produção)

| Variável | Uso |
|---|---|
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `DB_SSLMODE` | Conexão com o PostgreSQL |
| `MARKNOW_JWT_PRIVATE_KEY_LOCATION`, `MARKNOW_JWT_PUBLIC_KEY_LOCATION` | Caminho das chaves JWT fora do classpath |
| `MARKNOW_SUPER_ADMIN_SENHA` | Senha do super admin. Em produção, se não for definida e a senha ainda for a padrão, o super admin é bloqueado na inicialização |
| `CORS_ORIGINS` | Origens liberadas para o front (padrão `http://localhost:3000`) |
| `MARKNOW_AUTH_CABECALHO_IP` | Cabeçalho com o IP real do cliente atrás de proxy (ex.: `CF-Connecting-IP`) |
| `MARKNOW_AUTH_LIMITE_TENTATIVAS`, `MARKNOW_AUTH_JANELA_MINUTOS` | Limite de tentativas em `/auth` |
| `PORT` | Porta HTTP (padrão `9696`) |

### Testes

Testes unitários, não precisam de banco:

```bash
./mvnw test
```
