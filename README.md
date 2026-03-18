# API RESTful de Gerenciamento de Usuários

Projeto desenvolvido com Java 17, Spring Boot 3, PostgreSQL e Docker para cadastro, atualização, remoção e validação de login de usuários.

## Arquitetura

O projeto segue separação por camadas e responsabilidades:

- `domain/model`: entidade JPA persistida no banco.
- `repository`: acesso a dados com Spring Data JPA.
- `service`: regras de negócio, unicidade e hashing de senha.
- `controller`: exposição dos endpoints REST.
- `dto`: contratos de entrada e saída da API.
- `exception`: exceções de negócio e tratamento global de erros.
- `config`: beans de infraestrutura, como o `PasswordEncoder`.

## Regras implementadas

- `email` e `login` possuem unicidade validada na aplicação e no banco.
- A senha nunca é salva em texto plano.
- O hash da senha é gerado com BCrypt.
- O endpoint de login valida a senha comparando o payload com o hash persistido.
- `dataUltimaAlteracao` é atualizada automaticamente via Hibernate com `@UpdateTimestamp`.
- A entidade não é exposta diretamente pela API.

## Tecnologias

- Java 17
- Spring Boot 3.3.9
- Spring Web
- Spring Data JPA
- Spring Validation
- `spring-security-crypto` para BCrypt
- PostgreSQL
- Docker / Docker Compose
- JUnit 5 / Mockito

## Pré-requisitos

Para execução local sem container:

- Java 17+
- Maven 3.9+
- PostgreSQL disponível

Para execução com containers:

- Docker
- Docker Compose

## Configuração

As variáveis abaixo podem ser definidas no ambiente:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `SERVER_PORT`

Valores padrão configurados em `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pocuser
    username: postgres
    password: postgres
```

## Como executar com Docker Compose

Na raiz do projeto, execute exatamente:

```bash
docker compose up --build
```

Após a inicialização:

- API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

Para derrubar os containers:

```bash
docker compose down
```

Para derrubar containers e remover o volume do banco:

```bash
docker compose down -v
```

## Como executar localmente com Maven

Compile:

```bash
mvn clean package
```

Suba a aplicação:

```bash
mvn spring-boot:run
```

## Endpoints

### Criar usuário

- Método: `POST`
- URL: `/api/usuarios`

```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "login": "joao.silva",
  "senha": "Senha@123"
}
```

### Atualizar usuário

- Método: `PUT`
- URL: `/api/usuarios/{id}`

```json
{
  "nome": "João Silva Atualizado",
  "email": "joao.atualizado@email.com",
  "login": "joao.silva",
  "senha": "NovaSenha@123"
}
```

### Deletar usuário

- Método: `DELETE`
- URL: `/api/usuarios/{id}`

### Login

- Método: `POST`
- URL: `/api/usuarios/login`

```json
{
  "login": "joao.silva",
  "senha": "Senha@123"
}
```

Resposta de sucesso:

```json
{
  "mensagem": "Login realizado com sucesso"
}
```

## Testes

Para rodar os testes automatizados:

```bash
mvn test
```

## Estrutura do projeto

```text
src
├── main
│   ├── java/org/gideao/pocuser
│   │   ├── config
│   │   ├── controller
│   │   ├── domain/model
│   │   ├── dto
│   │   ├── exception
│   │   ├── repository
│   │   └── service
│   └── resources
└── test
```
