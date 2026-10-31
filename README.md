# Gestão Financeira - Projeto da sala 2ESPZ

API de finanças pessoais construída pela turma inteira: **9 grupos, 9 módulos, um único repositório**.

Java 21 · Spring Boot 4.1 · Spring Data JPA · Oracle

## Arquitetura: monólito modular


```
src/main/java/br/com/fiap/gestaofinanceira
├── GestaoFinanceiraApplication.java
├── compartilhado/          ← professor (tratamento global de exceções)
├── pessoas/                ← Grupo 8
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   └── dto/
├── contas/                 ← Grupo 2
├── transacoes/             ← Grupo 7
├── investimentos/          ← Grupo 6
├── dividas/                ← Grupo 9
├── planejamentos/          ← Grupo 5
├── educacao/               ← Grupo 1
├── notificacoes/           ← Grupo 3
└── relatorios/             ← Grupo 4

src/main/resources/banco-de-dados
├── 00-limpeza.sql          ← professor
├── 01-pessoas.sql          ← um script por módulo, numerado por dependência
├── ...
├── 09-relatorios.sql
└── executar-todos.sql      ← professor
```

### Regras de ouro

1. **Cada grupo só altera o próprio pacote e o próprio script.** `pom.xml`, `application.properties`, `compartilhado/` e `.github/` são do professor; precisa mudar algo neles? Abra uma issue.
2. **Outro módulo se referencia pelo id, não pela entidade.** Use `String idPessoa`, não `@ManyToOne Usuario`. Relacionamentos JPA só entre entidades do mesmo módulo.
3. **Para usar regras de outro módulo, chame o `service` dele**, nunca o `repository`.
4. **Endpoints com o nome do módulo:** `/api/v1/<modulo>/...` (ex.: `/api/v1/contas/{id}`).
5. **O banco é criado pelos scripts.** O Hibernate só valida (`ddl-auto=validate`).

## Módulos

| Ordem | Módulo | Grupo | Pacote | Branch | Script | Prefixo |
|---|---|---|---|---|---|---|
| 01 | Cadastro Pessoas | 8 | `pessoas` | `feature/pessoas` | `01-pessoas.sql` | `TB_PES_` |
| 02 | Contas Bancárias | 2 | `contas` | `feature/contas` | `02-contas.sql` | `TB_CTA_` |
| 03 | Transações Financeiras | 7 | `transacoes` | `feature/transacoes` | `03-transacoes.sql` | `TB_TRN_` |
| 04 | Investimentos | 6 | `investimentos` | `feature/investimentos` | `04-investimentos.sql` | `TB_INV_` |
| 05 | Gestão de Dívidas | 9 | `dividas` | `feature/dividas` | `05-dividas.sql` | `TB_DIV_` |
| 06 | Planejamentos Futuros | 5 | `planejamentos` | `feature/planejamentos` | `06-planejamentos.sql` | `TB_PLN_` |
| 07 | Educação Financeira | 1 | `educacao` | `feature/educacao` | `07-educacao.sql` | `TB_EDU_` |
| 08 | Controle de Notificações | 3 | `notificacoes` | `feature/notificacoes` | `08-notificacoes.sql` | `TB_NTF_` |
| 09 | Relatório | 4 | `relatorios` | `feature/relatorios` | `09-relatorios.sql` | `TB_REL_` |

Convenções de banco, tipos e um exemplo completo de script + entidade: [`src/main/resources/banco-de-dados/README.md`](src/main/resources/banco-de-dados/README.md).

## Como rodar

1. Clone e entre na branch de desenvolvimento:
   ```bash
   git clone <url-do-repositorio>
   cd gestao-financeira
   git checkout development
   ```
2. Copie `src/main/resources/application-local.properties.exemplo` para `application-local.properties` (mesma pasta) e coloque **o seu** usuário e senha do Oracle. Esse arquivo está no `.gitignore` e nunca vai para o GitHub.
3. No SQL Developer, rode `src/main/resources/banco-de-dados/executar-todos.sql` (F5) no seu usuário.
4. Rode `GestaoFinanceiraApplication` pelo IntelliJ (ou `mvn spring-boot:run`).

## Como contribuir

Fluxo de branches, padrão de commits e regras de pull request: [CONTRIBUTING.md](CONTRIBUTING.md).
