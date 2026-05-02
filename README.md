# GastroHub — FIAP PosTech

Backend de gerenciamento de usuários — entrega da **Fase 1** do curso de **Arquitetura e Desenvolvimento Java** da FIAP PosTech.

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)
![Testes](https://img.shields.io/badge/Testes-40%20passing-brightgreen?logo=junit5&logoColor=white)

---

## Status do Projeto

✅ **Fase 1 concluída** — backend de gerenciamento de usuários entregue com autenticação JWT, testes automatizados e documentação Swagger.

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.2.3 |
| Banco de dados | PostgreSQL 16 |
| Autenticação | Spring Security + JWT (JJWT 0.12.6) |
| Hash de senha | Argon2id |
| Documentação | Swagger UI (SpringDoc OpenAPI 2.3.0) |
| Container | Docker + Docker Compose |
| Testes | JUnit 5 + Mockito + Spring Security Test |

---

## Como Executar

**Pré-requisito:** Docker instalado.

```bash
# Subir banco + aplicação
docker-compose up -d

# Apenas o banco (para rodar a aplicação localmente)
docker-compose up -d postgres
mvn spring-boot:run
```

Aplicação disponível em: `http://localhost:8080`

Para parar:
```bash
docker-compose down        # mantém os dados
docker-compose down -v     # apaga os dados do banco
```

---

## Endpoints

Base URL: `http://localhost:8080/v1`

> Endpoints marcados com 🔒 exigem `Authorization: Bearer <token>` — obtido no login.

| Método | Rota | Descrição |
|---|---|---|
| POST | `/usuarios` | Criar usuário (público) |
| POST | `/usuarios/login` | Obter token JWT (público) |
| GET | `/usuarios` | Listar usuários 🔒 |
| GET | `/usuarios/{id}` | Buscar por ID 🔒 |
| GET | `/usuarios/buscar?nome=X` | Buscar por nome 🔒 |
| PUT | `/usuarios/{id}` | Atualizar dados 🔒 |
| PATCH | `/usuarios/{id}/senha` | Trocar senha 🔒 |
| DELETE | `/usuarios/{id}` | Excluir usuário 🔒 |

Documentação completa com exemplos: `http://localhost:8080/swagger-ui/index.html`

---

## Variáveis de Ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `JWT_SEGREDO` | *(obrigatório)* | Chave para assinatura dos tokens JWT |
| `ADMIN_SENHA` | `admin123` | Senha do administrador provisionado na inicialização |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5435/techchallenge` | URL do banco |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Usuário do banco |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Senha do banco |

> O Docker Compose já define essas variáveis automaticamente. Em produção, `JWT_SEGREDO` deve ser definido explicitamente.

---

## Testes

```bash
mvn test
```

40 testes automatizados (unitários + integração MVC). Utilizam H2 in-memory — sem necessidade de Docker ou PostgreSQL.

---

## Postman

Importe `postman/gastrohub-fase1.postman_collection.json` para ter acesso a 14 requisições cobrindo todos os endpoints e principais cenários de erro.
