
# FinCore AI 💳

API REST para gerenciamento de contas e transações financeiras, desenvolvida com Java e Spring Boot.

O **FinCore AI** é um projeto de portfólio voltado ao desenvolvimento backend, com foco em arquitetura organizada, regras de negócio, persistência de dados, testes automatizados e documentação de APIs.

O projeto também foi pensado para evoluir futuramente com funcionalidades de inteligência artificial.

## 🚀 Tecnologias utilizadas

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- PostgreSQL
- Maven
- Bean Validation
- JUnit e Mockito
- Testcontainers
- Docker
- Swagger / OpenAPI
- Git e GitHub

## ✅ Funcionalidades implementadas

### Gerenciamento de contas

- Criar contas financeiras
- Listar contas cadastradas
- Buscar conta por UUID
- Atualizar dados de uma conta
- Excluir contas

### Gerenciamento de transações

- Registrar receitas (`INCOME`)
- Registrar despesas (`EXPENSE`)
- Consultar transações por UUID
- Listar transações com paginação
- Filtrar transações por conta
- Filtrar transações por tipo
- Ordenar transações pela data de criação

### Regras de negócio

- Receitas aumentam o saldo da conta
- Despesas diminuem o saldo da conta
- Transações de despesa são bloqueadas quando não há saldo suficiente
- Validação dos dados recebidos pela API
- Tratamento global de exceções e respostas de erro

## 🏗️ Organização do projeto

O código está organizado por domínio de negócio:

```text
src/
├── main/
│   └── java/com/fincore/api/
│       ├── account/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── model/
│       │   ├── repository/
│       │   └── service/
│       ├── transaction/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── model/
│       │   ├── repository/
│       │   └── service/
│       ├── config/
│       └── exception/
└── test/
    └── java/com/fincore/api/
        └── transaction/
            ├── integration/
            └── service/
```

Essa estrutura separa as responsabilidades de cada camada e facilita a manutenção e evolução do sistema.

## 📡 Endpoints da API

### Contas

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/accounts` | Criar conta |
| GET | `/api/v1/accounts` | Listar contas |
| GET | `/api/v1/accounts/{id}` | Buscar conta por ID |
| PUT | `/api/v1/accounts/{id}` | Atualizar conta |
| DELETE | `/api/v1/accounts/{id}` | Excluir conta |

### Transações

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/transactions` | Criar transação |
| GET | `/api/v1/transactions` | Listar e filtrar transações |
| GET | `/api/v1/transactions/{id}` | Buscar transação por ID |

A listagem de transações aceita os parâmetros opcionais:

- `accountId`: filtra por conta
- `type`: filtra por `INCOME` ou `EXPENSE`
- `page`: número da página, começando em `0`
- `size`: quantidade de registros por página, entre `1` e `100`

Exemplo:

```http
GET /api/v1/transactions?type=EXPENSE&page=0&size=10
```

## 📖 Documentação Swagger

Com a aplicação em execução, a documentação interativa está disponível em:

**http://localhost:8080/swagger-ui/index.html**

A especificação OpenAPI pode ser consultada em:

**http://localhost:8080/v3/api-docs**

O Swagger permite visualizar os endpoints, consultar os modelos de dados e testar requisições diretamente pelo navegador.

## 🧪 Testes automatizados

O projeto possui testes unitários e testes de integração.

### Testes unitários

Os testes de serviço utilizam JUnit e Mockito para verificar regras de negócio, incluindo operações financeiras e validações.

### Testes de integração

Os testes utilizam Testcontainers para executar verificações de persistência com PostgreSQL em um ambiente isolado.

**Testes implementados nas TASK-012 e TASK-013:**

- 24 testes unitários
- 3 testes de integração
- 27 testes no total

Para executar os testes:

```powershell
.\mvnw.cmd test
```

Para executar apenas os testes de integração:

```powershell
.\mvnw.cmd -Dtest=TransactionIntegrationTest test
```

> Os testes de integração precisam do Docker em execução.

## ⚙️ Como executar o projeto

### Pré-requisitos

- Java 21
- PostgreSQL
- Docker Desktop (para os testes de integração)
- Git

### 1. Clonar o repositório

```bash
git clone https://github.com/Thisanchez2013/fincore-ai.git
cd fincore-ai
```

### 2. Preparar o banco de dados

Crie um banco PostgreSQL chamado:

```text
fincore_db
```

Configure a conexão com o PostgreSQL nas propriedades da aplicação, utilizando as credenciais do seu ambiente.

### 3. Executar a aplicação

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

A API estará disponível em:

```text
http://localhost:8080
```

### 4. Acessar o Swagger

Abra no navegador:

```text
http://localhost:8080/swagger-ui/index.html
```

## 🔮 Próximas evoluções

O FinCore AI continuará evoluindo com novas funcionalidades, melhorias de arquitetura e possíveis recursos de inteligência artificial aplicados à análise financeira.

## 👨‍💻 Autor

Desenvolvido como projeto de estudo e portfólio profissional em Java Backend.

GitHub: [Thisanchez2013](https://github.com/Thisanchez2013)

---

**FinCore AI — Construindo uma API financeira com Java, boas práticas e evolução contínua.**
