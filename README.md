# 🏦 Banking System: Arquitetura Backend com Java 21 & MySQL

> **Status:** Concluído (Projeto de Arquitetura de Software)

Este projeto é um sistema de backend bancário robusto, desenvolvido para explorar o ecossistema **Java 21 (LTS)** e a persistência de dados em ambientes relacionais. O foco principal foi a implementação de uma lógica de negócio resiliente, garantindo a integridade financeira através do padrão **DAO (Data Access Object)** e do gerenciamento manual de transações SQL.

## 🚀 Tecnologias Utilizadas
* **Linguagem:** Java 21 (LTS)
* **Banco de Dados:** MySQL 8.0.45
* **Gerenciador de Dependências:** Maven
* **Conectividade:** JDBC (Java Database Connectivity)
* **Driver:** `mysql-connector-j` (v8.3.0)

## 🏗️ Arquitetura e Camadas
Diferente de sistemas básicos de estudo, este projeto utiliza uma estrutura desacoplada para garantir manutenibilidade:

1.  **Model:** Utilização de **Polimorfismo** e Herança com `ContaCorrente` e `ContaPoupanca`, aplicando validações rigorosas de estado nos objetos de domínio.
2.  **DAO (Data Access Object):** Camada de persistência que isola o SQL. Implementa o uso de **Atomicidade (Transactions)** com `commit` e `rollback` para operações financeiras críticas.
3.  **Service:** Camada intermediária que orquestra as regras de negócio, garantindo que o fluxo de transferências e rendimentos seja validado antes da persistência física.
4.  **Exceptions:** Hierarquia de exceções personalizadas para controle de fluxo e erros de negócio (ex: `SaldoInsuficienteException`).

## ✨ Funcionalidades e Diferenciais
* **Transações Financeiras:** Operações de Saque, Depósito e Transferência com validação de limite bancário em tempo real.
* **Segurança Lógica:** Uso de `PreparedStatement` em todas as interações com o banco para proteção contra **SQL Injection**.
* **Gestão de Autenticação:** Sistema de login integrado para acesso restrito às funcionalidades da conta.
* **Processamento em Lote:** Lógica automatizada para aplicação de rendimentos em massa para contas do tipo Poupança.
* **Segurança de Credenciais:** Configuração de acesso ao banco via arquivo `.properties` externo, protegido por `.gitignore`.

## ⚙️ Como rodar o projeto
1. **Clone o repositório.**
2. **Banco de Dados:** Importe os scripts SQL localizados na pasta `/database` para o seu MySQL.
3. **Configuração:** - Localize o arquivo `src/main/resources/config.properties.example`.
   - Renomeie para `config.properties`.
   - Insira seu `user` e `password` do MySQL local.
4. **Build:** No diretório raiz, execute `mvn clean install` para carregar as dependências.
5. **Run:** Inicie a aplicação através da classe `Main.java`.

---

### 📝 Notas de Desenvolvimento
* **Foco Didático:** O sistema utiliza armazenamento de senhas em texto plano para focar na lógica de persistência e POO. Em evoluções futuras, prevê-se a implementação de Hashing (BCrypt).
* **Escalabilidade:** A separação em interfaces permite a substituição do provedor de dados com impacto zero na lógica de negócio da aplicação.

---
**Desenvolvido por [Henrique Oliveira Pires](https://github.com/hrnq-op)** 🚀  
*Focado em construir sistemas resilientes e arquiteturas backend profissionais.*
