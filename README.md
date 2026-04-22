# GastroHub — FIAP PosTech

Backend de gerenciamento de usuários desenvolvido como entrega da **Fase 1** do curso de **Arquitetura e Desenvolvimento Java** da FIAP PosTech.

---

## Sumário

- [Sobre o Projeto](#sobre-o-projeto)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Entidades](#entidades)
- [Endpoints da API](#endpoints-da-api)
- [Regras de Negócio](#regras-de-negócio)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Testes](#testes)
- [Documentação Interativa (Swagger)](#documentação-interativa-swagger)
- [Collection Postman](#collection-postman)

---

## Sobre o Projeto

Sistema de gestão para restaurantes — a **Fase 1** foca exclusivamente no backend de gerenciamento de usuários.

O sistema suporta dois tipos de usuário:
- **DONO_RESTAURANTE** — proprietário de um estabelecimento
- **CLIENTE** — consumidor final

As funcionalidades entregues nesta fase são:
- Cadastro de usuário
- Listagem e busca de usuários
- Atualização de dados cadastrais
- Troca de senha com confirmação da senha atual
- Validação de login (autenticação simples)
- Exclusão de usuário

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.2.3 |
| Banco de dados (produção) | PostgreSQL 16 |
| Banco de dados (testes) | H2 in-memory |
| ORM | Spring Data JPA / Hibernate |
| Validação | Bean Validation (Jakarta) |
| Hash de senha | Argon2id (Spring Security) |
| Autenticação | Spring Security + JWT (JJWT 0.12.6) |
| Documentação | SpringDoc OpenAPI 2.3.0 (Swagger UI) |
| Build | Maven 3.9+ |
| Container | Docker + Docker Compose |
| Testes | JUnit 5 + Mockito + Spring Security Test |
| Utilitários | Lombok |

---

## Arquitetura

O projeto segue a arquitetura em camadas do Spring Boot, respeitando os princípios SOLID:

```
br.com.gastrohub
├── controller/       # Camada HTTP — recebe requisições e retorna respostas
├── service/          # Regras de negócio (interface + implementação)
├── repository/       # Acesso ao banco de dados (Spring Data JPA)
├── entity/           # Entidades JPA mapeadas para o banco
├── enums/            # Enumerações do domínio
├── dto/              # Objetos de transferência de dados (Request / Response)
├── exception/        # Exceções customizadas e handler global de erros
├── security/         # JWT: JwtService, JwtAuthenticationFilter, UsuarioDetailsService
└── config/           # Configurações: OpenApiConfig, SegurancaConfig
```

### Fluxo de uma requisição

```
Cliente HTTP
    │
    ▼
Controller  →  valida entrada (@Valid)
    │
    ▼
Service     →  aplica regras de negócio
    │
    ▼
Repository  →  persiste / consulta no banco
    │
    ▼
Banco de dados (PostgreSQL)
```

---

## Entidades

### usuarios

| Coluna | Tipo | Descrição |
|---|---|---|
| id | BIGINT (PK) | Identificador único |
| nome | VARCHAR | Nome completo |
| email | VARCHAR (UNIQUE) | E-mail do usuário |
| login | VARCHAR (UNIQUE) | Login de acesso |
| senha | VARCHAR | Senha em hash Argon2id |
| tipo | VARCHAR | `DONO_RESTAURANTE` ou `CLIENTE` |
| data_criacao | TIMESTAMP | Data de cadastro |
| data_ultima_alteracao | TIMESTAMP | Data da última modificação |

---

## Endpoints da API

Base URL: `http://localhost:8080`

> **Nota:** Todos os endpoints estão versionados sob `/v1/` para suportar evoluções futuras da API de forma retrocompatível.

### Usuários

#### Criar usuário
```
POST /usuarios
```
**Body:**
```json
{
  "nome": "Roberto Rodrigues",
  "email": "rrodriguez@email.com",
  "login": "rrodriguez",
  "senha": "password123",
  "tipo": "CLIENTE"
}
```
**Respostas:**
- `201 Created` — usuário criado com sucesso
- `400 Bad Request` — dados inválidos ou faltando
- `409 Conflict` — login ou e-mail já cadastrado

---

#### Buscar usuário por ID
```
GET /v1/usuarios/{id}
```
**Respostas:**
- `200 OK` — retorna os dados do usuário
- `404 Not Found` — usuário não encontrado

---

#### Listar todos os usuários
```
GET /v1/usuarios
```
**Respostas:**
- `200 OK` — retorna lista de usuários

---

#### Buscar usuários por nome
```
GET /v1/usuarios/buscar?nome=Roberto
```
Retorna usuários cujo nome contenha o texto informado (busca case-insensitive).

**Query Parameters:**
- `nome` (obrigatório) — texto para buscar no nome do usuário

**Respostas:**
- `200 OK` — retorna lista de usuários encontrados (pode estar vazia)

**Exemplo:**
```bash
curl "http://localhost:8080/v1/usuarios/buscar?nome=Silva"
```

---

#### Atualizar dados do usuário
```
PUT /v1/usuarios/{id}
```
Apenas os campos informados serão atualizados. Login e tipo não podem ser alterados.

**Body (todos os campos são opcionais):**
```json
{
  "nome": "Roberto Rodrigues",
  "email": "rrodriguez.santos@email.com"
}
```
**Respostas:**
- `200 OK` — dados atualizados
- `400 Bad Request` — dados inválidos
- `404 Not Found` — usuário não encontrado
- `409 Conflict` — e-mail já em uso

---

#### Excluir usuário
```
DELETE /v1/usuarios/{id}
```
**Respostas:**
- `204 No Content` — usuário excluído
- `404 Not Found` — usuário não encontrado

---

#### Trocar senha
```
PATCH /v1/usuarios/{id}/senha
```
**Body:**
```json
{
  "senhaAtual": "senha123",
  "novaSenha": "novaSenha456"
}
```
**Respostas:**
- `204 No Content` — senha alterada com sucesso
- `401 Unauthorized` — senha atual incorreta
- `404 Not Found` — usuário não encontrado

---

#### Validar login
```
POST /v1/usuarios/login
```
**Body:**
```json
{
  "login": "rrodriguez",
  "senha": "password123"
}
```
**Respostas:**
- `200 OK` — credenciais válidas, retorna token JWT Bearer (expira em 30 minutos)
- `401 Unauthorized` — login ou senha incorretos

---

### Formato de erro padrão (RFC 7807)

Todos os erros seguem o padrão **RFC 7807 - Problem Details for HTTP APIs**:

```json
{
  "type": "https://api.gastrohub.com/errors/usuario-nao-encontrado",
  "title": "Usuário não encontrado",
  "status": 404,
  "detail": "Usuário com ID 99 não encontrado"
}
```

Para erros de validação (`400`), há um campo adicional com os erros por campo:
```json
{
  "type": "https://api.gastrohub.com/errors/validacao",
  "title": "Erro de validação",
  "status": 400,
  "detail": "Erro de validação nos campos",
  "erros": {
    "email": "Email inválido",
    "senha": "Senha deve ter no mínimo 6 caracteres"
  }
}
```

**Campos do ProblemDetail:**
- `type` — URI que identifica o tipo de erro
- `title` — Título legível do erro
- `status` — Código HTTP
- `detail` — Descrição detalhada do erro
- `erros` (opcional) — Mapa de erros por campo em validações

---

## Regras de Negócio

- A **senha nunca é retornada** nas respostas da API
- A senha é armazenada com **hash Argon2id** — nunca em texto puro
- **Login é único** por usuário — tentativa de duplicata retorna `409`
- **E-mail é único** por usuário — tentativa de duplicata retorna `409`
- Para trocar a senha, é obrigatório informar a **senha atual correta**
- Campos `null` não são incluídos nas respostas JSON
- Endpoints protegidos exigem **token JWT Bearer** — obtido no login (expira em 30 minutos)
- Sem token → `401 Unauthorized`

---

## Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalados:

- [Docker](https://www.docker.com/) e [Docker Compose](https://docs.docker.com/compose/)
- [Java 21](https://adoptium.net/) — necessário apenas para rodar sem Docker
- [Maven 3.9+](https://maven.apache.org/) — necessário apenas para rodar sem Docker

---

## Como Executar

### Opção 1 — Apenas o banco (desenvolvimento local)

Sobe somente o PostgreSQL. A aplicação é executada diretamente na sua máquina.

```bash
# 1. Subir o banco de dados
docker-compose up -d postgres

# 2. Executar a aplicação
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

---

### Opção 2 — Ambiente completo com Docker

Sobe o banco **e** a aplicação em containers.

```bash
docker-compose up -d
```

Aguarde alguns segundos para o container da aplicação iniciar após o banco estar saudável.

A aplicação estará disponível em: `http://localhost:8080`

---

### Derrubar o ambiente

```bash
# Parar os containers (mantém os dados)
docker-compose down

# Parar e remover os dados do banco
docker-compose down -v
```

---

### Executar os testes

```bash
mvn test
```

---

### Build do projeto

```bash
mvn clean package
```

O JAR gerado estará em `target/gastrohub-1.0.0.jar`.

---

## Variáveis de Ambiente

A aplicação suporta as seguintes variáveis de ambiente para customização (com valores padrão para desenvolvimento local):

| Variável | Padrão | Descrição |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5435/gastrohub` | URL de conexão com o banco |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Senha do banco |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Estratégia de criação do schema |

> O Docker Compose já configura essas variáveis automaticamente para comunicação entre containers.

---

## Testes

O projeto possui **32 testes automatizados** divididos em:

| Tipo | Classe | Descrição |
|---|---|---|
| Unitário | `UsuarioServiceTest` | Testa as regras de negócio isoladas com Mockito |
| Integração (MVC) | `UsuarioControllerTest` | Testa os endpoints HTTP com MockMvc |
| Integração (contexto) | `GastroHubApplicationTest` | Verifica se o contexto Spring sobe corretamente |

Os testes utilizam **H2 in-memory** e não dependem de Docker ou PostgreSQL.

```bash
mvn test
```

Resultado esperado:
```
Tests run: 32, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Documentação Interativa (Swagger)

Com a aplicação rodando, acesse:

```
http://localhost:8080/swagger-ui/index.html
```

A interface permite visualizar e testar todos os endpoints diretamente pelo navegador, sem necessidade de ferramentas externas.

O JSON da especificação OpenAPI está disponível em:
```
http://localhost:8080/v3/api-docs
```

---

## Collection Postman

O arquivo `postman/gastrohub-fase1.postman_collection.json` contém uma collection pronta com **14 requisições** cobrindo todos os endpoints e principais cenários de erro.

### Como importar

1. Abra o Postman
2. Clique em **File → Import** (ou `Ctrl+I`)
3. Selecione o arquivo `postman/gastrohub-fase1.postman_collection.json`
4. A collection **"GastroHub - Fase 1 | Usuários"** estará disponível

### Fluxo sugerido de teste

1. **Criar usuário** (`POST /usuarios`) — cria um usuário e salva o ID retornado
2. **Validar login** (`POST /usuarios/login`) — confirma as credenciais
3. **Buscar por ID** (`GET /usuarios/{id}`) — busca os dados do usuário
4. **Atualizar dados** (`PUT /usuarios/{id}`) — altera nome e/ou e-mail
5. **Trocar senha** (`PATCH /usuarios/{id}/senha`) — altera a senha
6. **Excluir usuário** (`DELETE /usuarios/{id}`) — remove o usuário

---

## Estrutura do Projeto

```
.
├── Dockerfile                          # Build multi-stage (Maven + JRE Alpine)
├── docker-compose.yml                  # Orquestração de containers
├── pom.xml                             # Dependências e build Maven
├── postman/
│   └── gastrohub-fase1.postman_collection.json
└── src/
    ├── main/
    │   ├── java/br/com/gastrohub/
    │   │   ├── GastroHubApplication.java
    │   │   ├── config/
    │   │   │   ├── OpenApiConfig.java
    │   │   │   └── SegurancaConfig.java
    │   │   ├── controller/
    │   │   │   └── UsuarioController.java
    │   │   ├── dto/
    │   │   │   ├── request/
    │   │   │   │   ├── AtualizarUsuarioRequest.java
    │   │   │   │   ├── CriarUsuarioRequest.java
    │   │   │   │   ├── TrocarSenhaRequest.java
    │   │   │   │   └── ValidarLoginRequest.java
    │   │   │   └── response/
    │   │   │       └── UsuarioResponse.java
    │   │   ├── entity/
    │   │   │   └── Usuario.java
    │   │   ├── enums/
    │   │   │   └── TipoUsuarioEnum.java
    │   │   ├── exception/
    │   │   │   ├── DadosJaCadastradosException.java
    │   │   │   ├── GlobalExceptionHandler.java
    │   │   │   ├── LoginOuSenhaInvalidosException.java
    │   │   │   ├── SenhaAtualInvalidaException.java
    │   │   │   └── UsuarioNaoEncontradoException.java
    │   │   ├── repository/
    │   │   │   └── UsuarioRepository.java
    │   │   └── service/
    │   │       ├── UsuarioService.java
    │   │       └── UsuarioServiceImpl.java
    │   └── resources/
    │       └── application.properties
    └── test/
        ├── java/br/com/gastrohub/
        │   ├── GastroHubApplicationTest.java
        │   ├── controller/
        │   │   └── UsuarioControllerTest.java
        │   └── service/
        │       └── UsuarioServiceTest.java
        └── resources/
            └── application-test.properties
```
