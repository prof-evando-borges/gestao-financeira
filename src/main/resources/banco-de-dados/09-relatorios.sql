-- =====================================================================
-- 09-relatorios.sql  (Grupo 4 - Modulo Relatorio, prefixo TB_REL_)
-- Somente CREATE TABLE / COMMENT ON. A limpeza e feita pelo 00-limpeza.sql.
-- =====================================================================

CREATE TABLE TB_REL_ITEM (
    ID              VARCHAR2(36)   NOT NULL,
    ID_RELATORIO    VARCHAR2(36)   NOT NULL,
    CATEGORIA       VARCHAR2(50)   NOT NULL,
    TIPO_OPERACAO   VARCHAR2(20)   NOT NULL,
    VALOR_TOTAL     NUMBER(15,2)   NOT NULL,
    PERCENTUAL      NUMBER(5,2),
    CONSTRAINT PK_REL_ITEM PRIMARY KEY (ID),
    CONSTRAINT CK_REL_ITEM_TIPO_OPERACAO CHECK (TIPO_OPERACAO IN ('RECEITA', 'DESPESA')),
    CONSTRAINT CK_REL_ITEM_VALOR_TOTAL CHECK (VALOR_TOTAL >= 0),
    CONSTRAINT CK_REL_ITEM_PERCENTUAL CHECK (PERCENTUAL BETWEEN 0 AND 100)
);

COMMENT ON TABLE TB_REL_ITEM IS 'Linha consolidada de um relatorio: total e participacao de uma categoria';
COMMENT ON COLUMN TB_REL_ITEM.ID_RELATORIO IS 'Relatorio ao qual o item pertence (FK sera criada junto com TB_REL_RELATORIO)';
COMMENT ON COLUMN TB_REL_ITEM.CATEGORIA IS 'Categoria agrupada no relatorio (ex.: ALIMENTACAO, SALARIO)';
COMMENT ON COLUMN TB_REL_ITEM.TIPO_OPERACAO IS 'RECEITA ou DESPESA';
COMMENT ON COLUMN TB_REL_ITEM.VALOR_TOTAL IS 'Soma dos valores da categoria no periodo do relatorio';
COMMENT ON COLUMN TB_REL_ITEM.PERCENTUAL IS 'Participacao da categoria no total do relatorio (0 a 100)';
