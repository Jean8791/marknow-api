# marknow-api

API do **MarkNow**, um SaaS de agenda para prestadores de serviço (salões, clínicas pequenas, serviços técnicos) com vários profissionais atendendo. O produto é multi-tenant por prestador, com entrada em beta fechado por convite e cobrança por planos.

O front-end está em [marknow-web](https://github.com/Jean8791/marknow-web).

> **Status:** MVP em desenvolvimento. A base de contas, planos, assinaturas e convites está pronta; o módulo de agenda é a próxima fase. O escopo completo está no [PRD](docs/PRD.md) e as decisões técnicas em [DECISIONS.md](DECISIONS.md).

## Stack

- **Java 17** e **Quarkus 3**
- Quarkus REST + Jackson, Hibernate ORM com Panache e Hibernate Validator
- **PostgreSQL 16** com migrations no **Flyway**
- Autenticação **JWT** (SmallRye JWT)
- Quarkus Scheduler, SmallRye Health e **OpenAPI/Swagger**
- Docker Compose para os bancos de dev e de teste

## Funcionalidades

- **Autenticação:** login com JWT e cadastro de prestador a partir de um **convite de uso único**.
- **Multi-tenant:** um filtro de requisição identifica o usuário e o prestador pelo token, confere se o usuário está ativo e se a assinatura do prestador permite o acesso. O super admin opera fora do tenant.
- **Planos e assinaturas:** planos configuráveis pelo super admin e assinaturas com ciclo `TRIAL → ATIVA → ATRASADO → INADIMPLENTE`, além de `SUSPENSA` e `CANCELADA`.
- **Cobrança automática de status:** um job agendado a cada 5 minutos verifica vencimento e período de tolerância. Passado o vencimento, a assinatura fica atrasada; passada a tolerância, ela fica inadimplente e o prestador é bloqueado.
- **Cadastros:** prestadores (com logo e endereço), usuários e convites.
- **Validações próprias:** CPF/CNPJ, telefone brasileiro e e-mail.
- **Erros padronizados** com códigos de erro e **correlation id** em toda resposta, para rastrear requisições nos logs.

## Arquitetura

```
br.com.codenest.marknow
├── resource/     endpoints REST
├── service/      regras de negócio (auth, assinatura, convite, cadastro...)
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
| Prestadores | `GET/POST /prestador` · `GET/PUT/DELETE /prestador/{oid}` |
| Usuários | `GET/POST /usuario` · `GET/DELETE /usuario/{oid}` · `PUT /usuario` |
| Planos | `GET/POST /plano` · `GET/PUT /plano/{oid}` |
| Assinaturas | `GET /assinatura` |
| Convites | `GET/POST /convite` |

Com a aplicação rodando em dev, a documentação interativa fica em `http://localhost:9696/q/swagger-ui`.

## Como rodar

### Pré-requisitos

- JDK 17+
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

A API sobe em `http://localhost:9696`. Em dev, o Flyway roda as migrations na inicialização e cria um super admin de bootstrap (veja `V6__insert_user.sql`). Troque essa senha antes de usar em qualquer ambiente que não seja o local.

### Testes

```bash
./mvnw test
```
