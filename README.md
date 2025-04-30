
# 💰 MoneyTracker

Sistema financeiro para controle de gastos pessoais

O MoneyTracker é uma aplicação desenvolvida para ajudar pessoas a gerenciarem suas finanças de forma inteligente. Com ele, você pode:

✔ Registrar receitas e despesas

✔ Categorizar gastos

✔ Visualizar relatórios mensais

✔ Definir metas de economia

✔ Acessar análises personalizadas


Tudo isso com segurança (via JWT/OAuth2) e uma API documentada para integrações.


# 🛠 Tecnologias Utilizadas

- Java 17
- Spring Boot 3.4.3
- Spring Data JPA
- PostgreSQL
- Spring Security (JWT + OAuth2)
- Lombok
- jUnit 5 + Mockito
- Swagger


# Pré-Requisitos
* ✅ Docker 
* ✅ PgAdmin
* ✅ mvn --version
Você deverá ver a indicação da versão do Maven instalada e a versão do JDK, dentre outras. Observe que o JDK é obrigatório, assim como a definição das variáveis de ambiente JAVA_HOME e M2_HOME.

# 🚀 Como Executar o Projeto

1. Clone o repositório:

```bash
git clone https://github.com/seu-usuario/money-tracker.git
```

2. Criar e configurar o .env
```bash
POSTGRES_USER=
POSTGRES_PASSWORD=
POSTGRES_DB=
SPRING_JPA_HIBERNATE_DDL_AUTO=
```

3. Executar o mvn clean package no terminal:
```bash
mvn clean package
```

4. Executar os containeres:
```bash
docker compose up --build
```


