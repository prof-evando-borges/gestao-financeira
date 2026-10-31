-- =====================================================================
-- 03-transacoes.sql  |  Modulo: Transacoes Financeiras (grupo 7)
-- Prefixo: TB_TRN_   |  Pode referenciar os modulos 01 e 02
-- =====================================================================

-- ---------------------------------------------------------------------
-- TB_TRN_CATEGORIA: classifica as transacoes (ex.: Salario, Alimentacao)
-- Criada primeiro porque e referenciada por TB_TRN_TRANSACAO.
-- ---------------------------------------------------------------------
CREATE TABLE TB_TRN_CATEGORIA (
    ID           VARCHAR2(36)  NOT NULL,
    NOME         VARCHAR2(100) NOT NULL,
    TIPO_PADRAO  VARCHAR2(10)  NOT NULL,
    CONSTRAINT PK_TRN_CATEGORIA PRIMARY KEY (ID),
    CONSTRAINT UK_TRN_CATEGORIA_NOME UNIQUE (NOME),
    CONSTRAINT CK_TRN_CATEGORIA_TIPO CHECK (TIPO_PADRAO IN ('RECEITA', 'DESPESA'))
);

COMMENT ON TABLE  TB_TRN_CATEGORIA              IS 'Categorias de movimentacao financeira';
COMMENT ON COLUMN TB_TRN_CATEGORIA.ID           IS 'Chave primaria (UUID)';
COMMENT ON COLUMN TB_TRN_CATEGORIA.NOME         IS 'Nome da categoria (unico)';
COMMENT ON COLUMN TB_TRN_CATEGORIA.TIPO_PADRAO  IS 'Tipo padrao: RECEITA ou DESPESA';

-- ---------------------------------------------------------------------
-- TB_TRN_TRANSACAO: receitas e despesas (coluna TIPO e o discriminador)
-- ID_PESSOA  -> modulo 01 (TB_PES_USUARIO)
-- ID_CONTA   -> modulo 02 (TB_CTA_CONTA)
-- ---------------------------------------------------------------------
CREATE TABLE TB_TRN_TRANSACAO (
    ID              VARCHAR2(36)   NOT NULL,
    ID_CATEGORIA    VARCHAR2(36)   NOT NULL,
    ID_PESSOA       VARCHAR2(36)   NOT NULL,
    ID_CONTA        VARCHAR2(36),
    DESCRICAO       VARCHAR2(150)  NOT NULL,
    VALOR           NUMBER(15,2)   NOT NULL,
    DATA_TRANSACAO  DATE           NOT NULL,
    TIPO            VARCHAR2(10)   NOT NULL,
    PAGA            NUMBER(1),
    DATA_CRIACAO    TIMESTAMP      NOT NULL,
    CONSTRAINT PK_TRN_TRANSACAO PRIMARY KEY (ID),
    CONSTRAINT FK_TRN_TRANSACAO_CATEGORIA FOREIGN KEY (ID_CATEGORIA)
        REFERENCES TB_TRN_CATEGORIA (ID),
    -- Tabelas dos grupos 8 (Pessoas) e 2 (Contas), ja integradas na development
    CONSTRAINT FK_TRN_TRANSACAO_PESSOA FOREIGN KEY (ID_PESSOA)
        REFERENCES TB_PES_USUARIO (ID),
    CONSTRAINT FK_TRN_TRANSACAO_CONTA FOREIGN KEY (ID_CONTA)
        REFERENCES TB_CTA_CONTA (ID),
    CONSTRAINT CK_TRN_TRANSACAO_VALOR CHECK (VALOR > 0),
    CONSTRAINT CK_TRN_TRANSACAO_TIPO  CHECK (TIPO IN ('RECEITA', 'DESPESA')),
    CONSTRAINT CK_TRN_TRANSACAO_PAGA  CHECK (PAGA IN (0, 1))
);

COMMENT ON TABLE  TB_TRN_TRANSACAO                IS 'Movimentacoes financeiras (receitas e despesas)';
COMMENT ON COLUMN TB_TRN_TRANSACAO.ID             IS 'Chave primaria (UUID)';
COMMENT ON COLUMN TB_TRN_TRANSACAO.ID_CATEGORIA   IS 'FK para TB_TRN_CATEGORIA';
COMMENT ON COLUMN TB_TRN_TRANSACAO.ID_PESSOA      IS 'Id da pessoa dona da transacao (modulo 01)';
COMMENT ON COLUMN TB_TRN_TRANSACAO.ID_CONTA       IS 'Id da conta bancaria usada (modulo 02), opcional';
COMMENT ON COLUMN TB_TRN_TRANSACAO.DESCRICAO      IS 'Descricao da movimentacao';
COMMENT ON COLUMN TB_TRN_TRANSACAO.VALOR          IS 'Valor em reais (sempre positivo)';
COMMENT ON COLUMN TB_TRN_TRANSACAO.DATA_TRANSACAO IS 'Data em que a movimentacao ocorreu';
COMMENT ON COLUMN TB_TRN_TRANSACAO.TIPO           IS 'Discriminador: RECEITA ou DESPESA';
COMMENT ON COLUMN TB_TRN_TRANSACAO.PAGA           IS 'Somente despesas: 1 = paga, 0 = pendente; NULL para receitas';
COMMENT ON COLUMN TB_TRN_TRANSACAO.DATA_CRIACAO   IS 'Data e hora do registro';

-- ---------------------------------------------------------------------
-- Dados iniciais (UUIDs fixos)
-- ---------------------------------------------------------------------
INSERT INTO TB_TRN_CATEGORIA (ID, NOME, TIPO_PADRAO) VALUES ('11111111-0000-4000-8000-000000000001', 'Salario', 'RECEITA');
INSERT INTO TB_TRN_CATEGORIA (ID, NOME, TIPO_PADRAO) VALUES ('11111111-0000-4000-8000-000000000002', 'Freelance', 'RECEITA');
INSERT INTO TB_TRN_CATEGORIA (ID, NOME, TIPO_PADRAO) VALUES ('11111111-0000-4000-8000-000000000003', 'Alimentacao', 'DESPESA');
INSERT INTO TB_TRN_CATEGORIA (ID, NOME, TIPO_PADRAO) VALUES ('11111111-0000-4000-8000-000000000004', 'Transporte', 'DESPESA');
INSERT INTO TB_TRN_CATEGORIA (ID, NOME, TIPO_PADRAO) VALUES ('11111111-0000-4000-8000-000000000005', 'Moradia', 'DESPESA');
INSERT INTO TB_TRN_CATEGORIA (ID, NOME, TIPO_PADRAO) VALUES ('11111111-0000-4000-8000-000000000006', 'Lazer', 'DESPESA');
