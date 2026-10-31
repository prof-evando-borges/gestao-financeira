# Banco de dados

Cada módulo tem **um único script**, com nome fixo. Os números definem a ordem de execução: um script só pode criar chave estrangeira para tabelas do próprio módulo ou de módulos com **número menor**.

## Ordem dos scripts (por dependência)

| Ordem | Script | Módulo | Grupo | Prefixo das tabelas | Dependências previstas |
|---|---|---|---|---|---|
| 00 | `00-limpeza.sql` | Apaga as tabelas do projeto | professor | - | - |
| 01 | `01-pessoas.sql` | Cadastro Pessoas | 8 | `TB_PES_` | nenhuma (base de todos) |
| 02 | `02-contas.sql` | Contas Bancárias | 2 | `TB_CTA_` | Pessoas (titular da conta) |
| 03 | `03-transacoes.sql` | Transações Financeiras | 7 | `TB_TRN_` | Pessoas, Contas (origem e destino) |
| 04 | `04-investimentos.sql` | Investimentos | 6 | `TB_INV_` | Pessoas, Contas (aplicação e resgate) |
| 05 | `05-dividas.sql` | Gestão de Dívidas | 9 | `TB_DIV_` | Pessoas, Contas (débito das parcelas) |
| 06 | `06-planejamentos.sql` | Planejamentos Futuros | 5 | `TB_PLN_` | Pessoas, Contas; metas podem usar Investimentos e Dívidas |
| 07 | `07-educacao.sql` | Educação Financeira | 1 | `TB_EDU_` | Pessoas (progresso nos conteúdos) |
| 08 | `08-notificacoes.sql` | Controle de Notificações | 3 | `TB_NTF_` | Pessoas; eventos de qualquer módulo anterior |
| 09 | `09-relatorios.sql` | Relatório | 4 | `TB_REL_` | consolida dados de todos os módulos |

A coluna "Dependências previstas" é uma proposta a partir do mural da Entrega 1. A regra que vale é a da ordem: **só referenciar números menores**. Se o seu módulo precisar de uma tabela de um módulo com número maior, fale com o professor antes de escrever o script.

## Regras do script

1. **Só criação.** `CREATE TABLE`, `COMMENT ON` e, se quiser, `INSERT` de dados iniciais. Nada de `DROP` (a limpeza é feita pelo `00-limpeza.sql`).
2. **Sem PL/SQL.** Cada comando termina com `;`. Não use blocos `BEGIN ... END;`, triggers, sequences, nem a barra `/`.
3. **Tabelas:** `TB_<PREFIXO>_<NOME>`, em maiúsculas e no singular. Ex.: `TB_TRN_CATEGORIA`.
4. **Colunas:** maiúsculas com `_`. A chave primária se chama `ID` e é `VARCHAR2(36)` com UUID gerado pela aplicação (`@GeneratedValue(strategy = GenerationType.UUID)`). A coluna de chave estrangeira se chama `ID_<TABELA_REFERENCIADA>` (ex.: `ID_PESSOA`).
5. **Constraints com nome,** usando o prefixo do módulo, com até 30 caracteres:
   - `PK_<PREFIXO>_<TABELA>` - chave primária
   - `FK_<PREFIXO>_<TABELA>_<REFERENCIA>` - chave estrangeira
   - `UK_<PREFIXO>_<TABELA>_<COLUNA>` - unique
   - `CK_<PREFIXO>_<TABELA>_<COLUNA>` - check
6. **Ordem dentro do script:** crie primeiro as tabelas que são referenciadas pelas outras.
7. **Só altere o seu script.** Scripts de outros módulos e os arquivos `00-limpeza.sql` e `executar-todos.sql` não podem aparecer no seu PR.

## Tipos: Oracle x Java

O projeto usa `spring.jpa.hibernate.ddl-auto=validate`: a aplicação não sobe se a entidade não bater com a tabela. Use estes pares:

| Oracle | Java | Observação |
|---|---|---|
| `VARCHAR2(36)` | `String` | ids (UUID) e chaves estrangeiras |
| `NUMBER(10)` | `Integer` | quantidades, contadores |
| `NUMBER(15,2)` | `BigDecimal` | **valores em dinheiro**. Não use `Double` |
| `VARCHAR2(n)` | `String` | `@Column(length = n)` |
| `CLOB` | `String` com `@Lob` | textos longos |
| `NUMBER(1)` | `Boolean` | com `CHECK (COLUNA IN (0, 1))` |
| `DATE` | `LocalDate` | só a data |
| `TIMESTAMP` | `LocalDateTime` | data e hora |
| `VARCHAR2(n)` | `enum` com `@Enumerated(EnumType.STRING)` | nunca `ORDINAL` |

## Exemplo completo (módulo fictício "Cartões", prefixo `CRT`)

Supondo que o módulo 01 tenha criado a tabela `TB_PES_USUARIO`:

```sql
-- 99-cartoes.sql (exemplo)
CREATE TABLE TB_CRT_CARTAO (
    ID             VARCHAR2(36)   NOT NULL,
    ID_PESSOA      VARCHAR2(36)   NOT NULL,
    NUMERO         VARCHAR2(16)   NOT NULL,
    LIMITE         NUMBER(15,2)   NOT NULL,
    BANDEIRA       VARCHAR2(20)   NOT NULL,
    ATIVO          NUMBER(1)      DEFAULT 1 NOT NULL,
    DATA_VALIDADE  DATE           NOT NULL,
    CRIADO_EM      TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_CRT_CARTAO PRIMARY KEY (ID),
    CONSTRAINT UK_CRT_CARTAO_NUMERO UNIQUE (NUMERO),
    CONSTRAINT FK_CRT_CARTAO_PESSOA FOREIGN KEY (ID_PESSOA) REFERENCES TB_PES_USUARIO (ID),
    CONSTRAINT CK_CRT_CARTAO_ATIVO CHECK (ATIVO IN (0, 1))
);

COMMENT ON TABLE TB_CRT_CARTAO IS 'Cartões de crédito do cliente';
```

```java
package br.com.fiap.gestaofinanceira.cartoes.entity;

@Entity
@Table(name = "TB_CRT_CARTAO")
@Getter
@Setter
@NoArgsConstructor
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID", length = 36)
    private String id;

    // Tabela de OUTRO módulo: guarde só o id (sem @ManyToOne)
    @Column(name = "ID_PESSOA", nullable = false, length = 36)
    private String idPessoa;

    @Column(name = "NUMERO", nullable = false, length = 16)
    private String numero;

    @Column(name = "LIMITE", nullable = false, precision = 15, scale = 2)
    private BigDecimal limite;

    @Enumerated(EnumType.STRING)
    @Column(name = "BANDEIRA", nullable = false, length = 20)
    private Bandeira bandeira;

    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo;

    @Column(name = "DATA_VALIDADE", nullable = false)
    private LocalDate dataValidade;

    @Column(name = "CRIADO_EM", nullable = false, insertable = false, updatable = false)
    private LocalDateTime criadoEm;
}
```

```java
package br.com.fiap.gestaofinanceira.cartoes.repository;

public interface CartaoRepository extends JpaRepository<Cartao, String> {
}
```

Entre tabelas do **mesmo** módulo, use `@ManyToOne` / `@OneToMany` normalmente. Para tabelas de **outro** módulo, guarde apenas o id: assim cada grupo mexe só no próprio pacote e ninguém quebra o código do outro.

## Como rodar no seu Oracle

1. No SQL Developer, conecte com o seu usuário da FIAP.
2. Abra `executar-todos.sql` e use **Executar Script (F5)**. Ele roda o `00-limpeza.sql` e depois os scripts 01 a 09 em ordem.
3. Suba a aplicação. Se alguma entidade não bater com a tabela, o erro de inicialização mostra a tabela e a coluna.

## Verificação automática (GitHub Actions)

Todo pull request roda o build no GitHub com um Oracle novo em container: os scripts 01 a 09 são executados em ordem e a aplicação sobe com `ddl-auto=validate`. **PR com o check vermelho não é aprovado.**
