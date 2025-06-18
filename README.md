# NextMove

NextMove é uma plataforma de microserviços em Spring Boot focada em gestão financeira pessoal. O sistema permite que usuários cadastrem, monitorem e controlem suas receitas e despesas de forma simples e segura.

A arquitetura é projetada para escalabilidade e manutenção facilitada, utilizando Eureka para descoberta de serviços, API Gateway para autenticação e roteamento, RabbitMQ para comunicação assíncrona (como envio de e-mails de boas-vindas e lembretes automáticos) e OpenFeign para integrações síncronas entre serviços.

Os dados são armazenados em PostgreSQL para informações relacionais e MongoDB para histórico de notificações, garantindo eficiência e organização. O Transaction Service inclui um scheduler que dispara automaticamente lembretes para transações próximas do vencimento.

NextMove entrega uma solução modular, robusta e preparada para crescimento, com foco em clareza, segurança e integração eficiente.

---

## 🚀 Serviços

### 1. Service Registry (Eureka Server)
- Atua como **núcleo de descoberta de serviços**.
- Permite que todos os microsserviços se registrem e se descubram dinamicamente.
- Facilita escalabilidade, balanceamento de carga e tolerância a falhas.

**Tecnologias:**
- Java 23
- Spring Boot
- Spring Cloud Netflix Eureka Server
- Spring Boot Actuator

---

### 2. API Gateway
- Funciona como **ponto único de entrada** para o ecossistema NextMove.
- Gerencia roteamento de requisições para os microsserviços.
- Implementa políticas de segurança, como validação centralizada de tokens JWT.
- Trata erros de autenticação, autorização e padroniza respostas.
- Garante desacoplamento entre clientes externos e os microsserviços internos.

**Tecnologias:**
- Java 23
- Spring Boot
- Spring Cloud Gateway (WebFlux)
- Spring Security
- JWT (validação dos tokens)
- Eureka Client
- Lombok

---

### 3. Auth Service
- Gerencia o **cadastro e autenticação de usuários**.
- Responsável por gerar tokens JWT.
- Persistência de dados de usuários em banco relacional.
- Integração assíncrona via RabbitMQ para disparo de **e-mails de boas-vindas** sempre que um novo usuário é cadastrado.

**Tecnologias:**
- Java 23
- Spring Boot
- Spring Security
- Spring Web
- JWT (java-jwt)
- Spring Data JPA + PostgreSQL
- Flyway (migrações)
- RabbitMQ (spring-boot-starter-amqp)
- Eureka Client
- OpenAPI (springdoc-openapi)
- Lombok

---

### 4. Transaction Service
- Gerencia **transações financeiras dos usuários** com persistência em banco relacional.
- Realiza integração síncrona com o **Auth Service via OpenFeign** para buscar dados de usuários.
- Integração assíncrona via RabbitMQ para disparo de **e-mails de lembrete de vencimento**.
- Contém um **scheduler** que executa periodicamente para verificar transações que vencem nos próximos 2 dias e envia lembretes automaticamente.

**Tecnologias:**
- Java 23
- Spring Boot
- Spring Web
- Spring Data JPA + PostgreSQL
- Flyway (migrações)
- RabbitMQ (spring-boot-starter-amqp)
- OpenFeign
- Eureka Client
- OpenAPI (springdoc-openapi)
- Lombok

---

### 5. Notification Service
- Responsável por todo o processo de **envio de notificações por e-mail**.
- Suporte a **templates HTML utilizando Thymeleaf**.
- Persistência de um **histórico de e-mails enviados no MongoDB**, permitindo auditoria e rastreabilidade.
- Consome mensagens do RabbitMQ, processando eventos como:
    - Envio de e-mail de **boas-vindas** (disparado pelo Auth Service).
    - Envio de e-mail de **lembrete de vencimento de transações** (disparado pelo Transaction Service).

**Tecnologias:**
- Java 23
- Spring Boot
- Spring Boot Mail + Thymeleaf
- Spring Data MongoDB
- RabbitMQ (spring-boot-starter-amqp)
- Eureka Client
- Lombok

---

## 🛠️ Tecnologias e ferramentas comuns

- Java 23
- Spring Boot 3.4.6
- Spring Cloud 2024.0.0
- Maven
- Docker e Docker Compose
- RabbitMQ (mensageria assíncrona)
- PostgreSQL (banco de dados relacional)
- MongoDB (armazenamento NoSQL para histórico de notificações)
- Flyway (versionamento de banco)
- Spring Boot Actuator (monitoramento e métricas)
- OpenAPI (springdoc) (documentação REST)
- Lombok (redução de boilerplate)
- dotenv-java (configuração de variáveis de ambiente locais)

---

## 🔗 Integração e arquitetura

- Os microsserviços se registram automaticamente no **Eureka Server**.
- O **API Gateway** é responsável pela validação de tokens, roteamento e segurança das requisições.
- Comunicação assíncrona entre serviços via **RabbitMQ**, utilizando Exchanges, filas e routing keys.
- Comunicação síncrona (REST) via **OpenFeign** para integrações diretas (ex.: Transaction Service consultando o Auth Service).
- Configurações segregadas por ambiente com suporte a **Spring Profiles** e **dotenv-java**.
- Um **scheduler interno no Transaction Service** processa periodicamente as transações e dispara lembretes de vencimento de forma automatizada.


---

## 📁 Estrutura do Projeto

O NextMove é um monorepo organizado em duas grandes categorias:

- **infra/** → Serviços de infraestrutura (API Gateway, Service Registry e scripts auxiliares).
- **services/** → Microsserviços de domínio (Auth, Transaction e Notification).

### 🔥 Estrutura Geral

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

## 📌 Endpoints da API

### Autenticação
- `POST /auth/login` - Autenticação de usuários (gera token JWT)
- `POST /auth/register` - Cadastro de novos usuários

### Transações Financeiras
- `POST /transaction` - Cadastro de transações
- `GET /transaction` - Listagem das transações do usuário
- `GET /transaction/{id}` - Detalhamento de uma transação
- `PATCH /transaction/{id}` - Atualização parcial de transação
- `PUT /transaction/{id}` - Atualização completa de transação
- `DELETE /transaction/{id}` - Exclusão de transação

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Para contribuir:
- Faça um **fork** do repositório
- Crie uma nova **branch** para sua funcionalidade (`git checkout -b nova-feature`)
- Realize as alterações e envie um **pull request**

---