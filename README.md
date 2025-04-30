
# 💰 MoneyTracker

Sistema financeiro para controle de gastos pessoais

O MoneyTracker é uma aplicação desenvolvida para ajudar pessoas a gerenciarem suas finanças de forma inteligente. Com ele, você pode:

✔ Registrar receitas e despesas

✔ Categorizar gastos

✔ Visualizar relatórios mensais

✔ Definir metas de economia

✔ Acessar análises personalizadas


Tudo isso com segurança (via JWT/OAuth2) e uma API documentada para integrações.


# 🛠 Tecnologias Utilizadas Back-end

- Java 17
- Spring Boot 3.4.3
- Spring Data JPA
- PostgreSQL
- Spring Security (JWT + OAuth2)
- Lombok
- jUnit 5 + Mockito
- Swagger

# 🛠 Tecnologias Utilizadas Front-end
- TypeScript
- React
- Tailwind CSS
- Vite


# Pré-Requisitos
* ✅ Node
* ✅ Docker 
* ✅ PgAdmin
* ✅ mvn --version
Você deverá ver a indicação da versão do Maven instalada e a versão do JDK, dentre outras. Observe que o JDK é obrigatório, assim como a definição das variáveis de ambiente JAVA_HOME e M2_HOME.

# 🚀 Como Executar o Projeto COM Docker

1. Clone o repositório:

```bash
git clone https://github.com/seu-usuario/money-tracker.git
```

2. Criar e configurar o .env
```bash
DB_USER=
DB_PORT=
DB_PASSWORD=
DB_HOST=
SENDGRID_API_KEY=
JWT_SECRET_KEY=

API_URL=
```

3. Executar o mvn clean package no terminal:
```bash
mvn clean package
```

4. Executar os containeres:
```bash
docker compose up --build
```

# 🚀 Como Executar o Projeto SEM Docker

### Rodar o backend

1. Clone o repositório:

```bash
git clone https://github.com/seu-usuario/money-tracker.git
```

2. Dar run no MoneyTrackerApplication.java

### Rodar o frontend

3. Acessar a pasta frontend
```bash
 cd .\moneyTracker-frontend\
```
4. Executar esse comando
```bash
 npm run dev
```
