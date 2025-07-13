# NextMove

![Java](https://img.shields.io/badge/Java-23-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.6-brightgreen)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![MongoDB](https://img.shields.io/badge/MongoDB-7-green)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3--management-orange)

NextMove é uma plataforma de gestão financeira pessoal baseada em arquitetura de microserviços, desenvolvida com Spring Boot, que permite aos usuários registrar, monitorar e gerenciar receitas e despesas de forma simples, segura e escalável.

A solução conta com API Gateway para roteamento e autenticação, Service Registry (Eureka) para descoberta de serviços, comunicação assíncrona via RabbitMQ e síncrona via OpenFeign, além de persistência em PostgreSQL e MongoDB. O Transaction Service possui um scheduler que dispara lembretes automáticos de transações próximas ao vencimento, promovendo maior controle financeiro ao usuário.

---

## 📑 Sumário

- [Visão Geral](#visão-geral)
- [Badges](#badges)
- [Variáveis de Ambiente](#variáveis-de-ambiente)
- [Arquitetura dos Serviços](#arquitetura-dos-serviços)
- [Tecnologias e Ferramentas](#tecnologias-e-ferramentas)
- [Build, Execução e Parada](#build-execução-e-parada)
- [Banco de Dados e Migrações](#banco-de-dados-e-migrações)
- [Comunicação entre Serviços](#comunicação-entre-serviços)
- [Segurança](#segurança)
- [Testes](#testes)
- [Documentação da API](#documentação-da-api)
- [Arquitetura de Filas e Eventos](#arquitetura-de-filas-e-eventos)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Práticas de Produção](#práticas-de-produção)
- [Endpoints Principais e Exemplos](#endpoints-principais-e-exemplos)
- [Contribuição](#contribuição)
- [Licença](#licença)
- [Contato](#contato)

---

## Visão Geral

NextMove foi projetado para ser robusto, seguro e preparado para crescer, com foco em desempenho, integração eficiente e experiência do usuário. A plataforma oferece:

- Cadastro e autenticação de usuários com tokens JWT
- Gerenciamento de receitas e despesas
- Lembretes automáticos de transações próximas ao vencimento
- Notificações por e-mail com templates HTML
- Auditoria e rastreabilidade de notificações

![Logo NextMove](https://github.com/user-attachments/assets/7940e72d-8612-4d9b-90ab-c3c8825623c5)

---

## Badges

![Java](https://img.shields.io/badge/Java-23-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.6-brightgreen)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)
![MongoDB](https://img.shields.io/badge/MongoDB-7-green)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3--management-orange)

---

## Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto com as seguintes variáveis (exemplo):

```env
# PostgreSQL Auth
POSTGRES_USER_AUTH=nextmove
POSTGRES_PASSWORD_AUTH=nextmove
POSTGRES_DB_AUTH=auth_db
POSTGRES_PORT_AUTH=5432

# PostgreSQL Transaction
POSTGRES_USER_TRANS=nextmove
POSTGRES_PASSWORD_TRANS=nextmove
POSTGRES_DB_TRANS=trans_db
POSTGRES_PORT_TRANS=5433

# MongoDB
MONGO_INITDB_ROOT_USERNAME=nextmove
MONGO_INITDB_ROOT_PASSWORD=nextmove
MONGO_PORT=27017

# RabbitMQ
RABBITMQ_DEFAULT_USER=guest
RABBITMQ_DEFAULT_PASS=guest
RABBITMQ_PORT=5672
RABBITMQ_MANAGEMENT_PORT=15672
```

Cada microserviço possui um arquivo `application.properties.example` com exemplos de configuração. Copie para `application.properties` e ajuste conforme necessário. Principais variáveis:
- `api.security.token.secret` (JWT Secret, igual em todos os serviços)
- Configurações de SMTP para Notification Service
- URLs de conexão com bancos

---

## Arquitetura dos Serviços

### 1. Service Registry (Eureka Server)
- Descoberta dinâmica de microsserviços
- Facilita escalabilidade, balanceamento de carga e tolerância a falhas

**Tecnologias:** Java 23, Spring Boot, Spring Cloud Netflix Eureka Server, Spring Boot Actuator

### 2. API Gateway
- Ponto único de entrada para o ecossistema NextMove
- Roteamento, autenticação e padronização de respostas
- Validação centralizada de tokens JWT
- CORS configurado para origens específicas

**Tecnologias:** Java 23, Spring Boot, Spring Cloud Gateway (WebFlux), Spring Security, JWT, Eureka Client, Lombok, Resilience4j

### 3. Auth Service
- Cadastro e autenticação de usuários
- Geração de tokens JWT
- Integração assíncrona via RabbitMQ para envio de e-mails de boas-vindas
- Persistência em PostgreSQL
- Migrações automáticas com Flyway

**Tecnologias:** Java 23, Spring Boot, Spring Security, JWT, Spring Data JPA + PostgreSQL, Flyway, RabbitMQ, Eureka Client, OpenAPI, Lombok

### 4. Transaction Service
- Gerenciamento de transações financeiras
- Integração síncrona com Auth Service via OpenFeign
- Scheduler para lembretes automáticos de vencimento
- Persistência em PostgreSQL
- Migrações automáticas com Flyway

**Tecnologias:** Java 23, Spring Boot, Spring Data JPA + PostgreSQL, Flyway, RabbitMQ, OpenFeign, Eureka Client, OpenAPI, Lombok

### 5. Notification Service
- Envio de notificações por e-mail com templates HTML (Thymeleaf)
- Histórico de e-mails enviados no MongoDB
- Consome eventos do RabbitMQ

**Tecnologias:** Java 23, Spring Boot, Spring Boot Mail + Thymeleaf, Spring Data MongoDB, RabbitMQ, Eureka Client, Lombok

---

## Tecnologias e Ferramentas

- Java 23, Spring Boot 3.4.6, Spring Cloud 2024.0.0
- Maven Wrapper em todos os serviços
- Docker & Docker Compose
- RabbitMQ, PostgreSQL, MongoDB
- Flyway, Spring Boot Actuator, OpenAPI (springdoc), Lombok, dotenv-java, Resilience4j

---

## Build, Execução e Parada

- **Subir toda a stack:**
  ```bash
  cd infra/scripts
  ./start-all.sh
  ```
- **Parar toda a stack:**
  ```bash
  cd infra/scripts
  ./stop-all.sh
  ```
- **Rodar um serviço isoladamente:**
  ```bash
  cd <caminho-do-serviço>
  ./mvnw spring-boot:run
  ```
- **Rodar testes:**
  ```bash
  cd <caminho-do-serviço>
  ./mvnw test
  ```

---

## Banco de Dados e Migrações

- Auth Service e Transaction Service usam PostgreSQL, com migrações Flyway.
- Notification Service usa MongoDB.
- Tabelas e coleções são criadas automaticamente ao subir os serviços.

---

## Comunicação entre Serviços

- **Service Discovery:** Todos os serviços se registram no Eureka.
- **API Gateway:** Centraliza autenticação, roteamento e segurança.
- **RabbitMQ:**
  - Eventos como `user.created` e `transaction.reminder` são enviados para filas específicas.
  - Notification Service consome essas filas para enviar e-mails.
- **OpenFeign:** Comunicação REST síncrona entre Transaction Service e Auth Service.

---

## Segurança

- **JWT:**
  - Auth Service gera tokens JWT ao autenticar usuários.
  - API Gateway valida o JWT em todas as requisições (exceto login e registro).
  - O segredo do JWT deve ser igual em todos os serviços que validam tokens.
- **Senhas:**
  - Armazenadas com BCrypt.
  - O fluxo de autenticação é stateless.
- **CORS:**
  - Configurado no API Gateway para permitir origens específicas (exemplo: `http://localhost:5173`).

---

## Testes

- Testes unitários e de integração com JUnit 5, Mockito e Spring Boot Test.
- Testes de integração de segurança no API Gateway.
- Testes de controllers e serviços nos microserviços.
- Para rodar todos os testes:
  ```bash
  cd <caminho-do-serviço>
  ./mvnw test
  ```

---

## Documentação da API

- Todos os serviços expõem documentação OpenAPI/Swagger em `/v3/api-docs` e `/swagger-ui.html`.
- Recomenda-se acessar a documentação para detalhes de payloads e respostas.

---

## Arquitetura de Filas e Eventos

- **Exchanges:** `nextmove.direct`, `nextmove.direct.retry`, `nextmove.direct.dlq`
- **Filas:**
  - `user.created.queue`, `user.created.queue.retry`, `user.created.queue.dlq`
  - `transaction.reminder.queue`, `transaction.reminder.queue.retry`, `transaction.reminder.queue.dlq`
- **Eventos:**
  - `user.created` → Envio de e-mail de boas-vindas
  - `transaction.reminder` → Envio de lembrete de vencimento
- **Dead Letter Queues (DLQ)** e **Retry Queues** para resiliência

---

## Estrutura do Projeto

Monorepo organizado em duas grandes categorias:

- **infra/** → Serviços de infraestrutura (API Gateway, Service Registry, scripts)
- **services/** → Microsserviços de domínio (Auth, Transaction, Notification)

```plaintext
infra/
├── api-gateway/
├── scripts/
├── service-registry/

services/
├── auth-service/
├── notification-service/
└── transaction-service/

Arquivos na raiz:
├── .env.example
├── .gitignore
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## Práticas de Produção

- Use secrets seguros para JWT e bancos.
- Configure variáveis de ambiente para produção.
- Monitore os serviços via Actuator.
- Use volumes Docker para persistência dos bancos.
- Utilize profiles do Spring para separar ambientes (`dev`, `prod`).

---

## Endpoints Principais e Exemplos

### Autenticação
- `POST /auth/login` — Autenticação de usuários (gera token JWT)
  - **Payload:**
    ```json
    {
      "username": "user@email.com",
      "password": "suaSenha"
    }
    ```
  - **Resposta:**
    ```json
    {
      "token": "<jwt-token>"
    }
    ```
- `POST /auth/register` — Cadastro de novos usuários
  - **Payload:**
    ```json
    {
      "name": "Nome do Usuário",
      "username": "user@email.com",
      "password": "suaSenha"
    }
    ```
  - **Resposta:**
    ```json
    {
      "message": "User registered successfully!"
    }
    ```

### Transações Financeiras
- `POST /transaction` — Cadastro de transações
- `GET /transaction` — Listagem das transações do usuário
- `GET /transaction/{id}` — Detalhamento de uma transação
- `PATCH /transaction/{id}` — Atualização parcial de transação
- `PUT /transaction/{id}` — Atualização completa de transação
- `DELETE /transaction/{id}` — Exclusão de transação

Consulte a documentação Swagger para exemplos detalhados de payloads e respostas.

---

## Contribuição

Contribuições são bem-vindas! Para contribuir:
1. Faça um **fork** do repositório
2. Crie uma nova **branch** para sua funcionalidade (`git checkout -b minha-feature`)
3. Realize as alterações e envie um **pull request**

---

## Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## Contato

Dúvidas, sugestões ou feedback? Entre em contato:
- [LinkedIn](https://www.linkedin.com/in/eduardo-toste)
