-- =====================================================================
-- 07-educacao.sql · Educacao Financeira (Grupo 1) · Prefixo TB_EDU_
-- Conteudos educacionais (artigo, video e curso online) e o progresso
-- de cada usuario (TB_PES_USUARIO, modulo 01) nesses conteudos.
-- Os IDs VARCHAR2(36) recebem o UUID gerado pela aplicacao.
-- =====================================================================

-- Uma tabela para a hierarquia ConteudoEducacional (Artigo, Video, CursoOnline).
-- A coluna TIPO diz qual e a subclasse (JPA SINGLE_TABLE); as colunas
-- especificas de cada tipo ficam nulas nos outros tipos.
CREATE TABLE TB_EDU_CONTEUDO (
    ID              VARCHAR2(36)   NOT NULL,
    TIPO            VARCHAR2(20)   NOT NULL,
    TITULO          VARCHAR2(150)  NOT NULL,
    DESCRICAO       VARCHAR2(500),
    NIVEL           VARCHAR2(20)   NOT NULL,
    ATIVO           NUMBER(1)      DEFAULT 1 NOT NULL,
    AUTOR           VARCHAR2(100),
    URL_VIDEO       VARCHAR2(300),
    NUMERO_MODULOS  NUMBER(10),

    CONSTRAINT PK_EDU_CONTEUDO PRIMARY KEY (ID),
    CONSTRAINT UK_EDU_CONTEUDO_TITULO UNIQUE (TITULO),
    CONSTRAINT CK_EDU_CONTEUDO_TIPO CHECK (TIPO IN ('ARTIGO', 'VIDEO', 'CURSO')),
    CONSTRAINT CK_EDU_CONTEUDO_NIVEL CHECK (NIVEL IN ('INICIANTE', 'INTERMEDIARIO', 'AVANCADO')),
    CONSTRAINT CK_EDU_CONTEUDO_ATIVO CHECK (ATIVO IN (0, 1)),
    CONSTRAINT CK_EDU_CONTEUDO_MODULOS CHECK (NUMERO_MODULOS IS NULL OR NUMERO_MODULOS > 0)
);

CREATE TABLE TB_EDU_PROGRESSO (
    ID              VARCHAR2(36)   NOT NULL,
    ID_USUARIO      VARCHAR2(36)   NOT NULL,
    ID_CONTEUDO     VARCHAR2(36)   NOT NULL,
    STATUS          VARCHAR2(20)   NOT NULL,
    PERCENTUAL      NUMBER(10)     DEFAULT 0 NOT NULL,
    DATA_INICIO     DATE           NOT NULL,
    DATA_CONCLUSAO  DATE,

    CONSTRAINT PK_EDU_PROGRESSO PRIMARY KEY (ID),
    CONSTRAINT FK_EDU_PROGRESSO_USUARIO FOREIGN KEY (ID_USUARIO) REFERENCES TB_PES_USUARIO (ID),
    CONSTRAINT FK_EDU_PROGRESSO_CONTEUDO FOREIGN KEY (ID_CONTEUDO) REFERENCES TB_EDU_CONTEUDO (ID),
    CONSTRAINT UK_EDU_PROGRESSO_USU_CONT UNIQUE (ID_USUARIO, ID_CONTEUDO),
    CONSTRAINT CK_EDU_PROGRESSO_STATUS CHECK (STATUS IN ('NAO_INICIADO', 'EM_ANDAMENTO', 'CONCLUIDO')),
    CONSTRAINT CK_EDU_PROGRESSO_PERCENTUAL CHECK (PERCENTUAL BETWEEN 0 AND 100),
    CONSTRAINT CK_EDU_PROGRESSO_DATAS CHECK (DATA_CONCLUSAO IS NULL OR DATA_CONCLUSAO >= DATA_INICIO)
);

COMMENT ON TABLE TB_EDU_CONTEUDO IS 'Conteudos de educacao financeira: artigos, videos e cursos online';
COMMENT ON COLUMN TB_EDU_CONTEUDO.TIPO IS 'Subclasse do conteudo: ARTIGO, VIDEO ou CURSO';
COMMENT ON COLUMN TB_EDU_CONTEUDO.ATIVO IS '1 = disponivel para novos progressos, 0 = desativado';
COMMENT ON TABLE TB_EDU_PROGRESSO IS 'Progresso de cada usuario em cada conteudo (um registro por usuario e conteudo)';
