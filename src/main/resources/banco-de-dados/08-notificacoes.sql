-- =====================================================================
-- 08-notificacoes.sql
-- Modulo 08 - Controle de Notificacoes (Grupo 3)
-- Prefixo das tabelas: TB_NTF_
-- Pode referenciar tabelas dos modulos 01 a 07.
--
-- ID_PESSOA guarda o id da pessoa do modulo 01 (Cadastro Pessoas).
-- ID_REFERENCIA guarda o id do registro de outro modulo que gerou a
-- notificacao (transacao, parcela de divida, meta, conteudo etc.).
-- =====================================================================

-- ---------------------------------------------------------------------
-- Canais de envio (e-mail, SMS, push, aplicativo)
-- ---------------------------------------------------------------------
CREATE TABLE TB_NTF_CANAL (
    ID          VARCHAR2(36)   NOT NULL,
    NOME        VARCHAR2(30)   NOT NULL,
    DESCRICAO   VARCHAR2(200),
    ATIVO       NUMBER(1)      DEFAULT 1 NOT NULL,
    CONSTRAINT PK_NTF_CANAL PRIMARY KEY (ID),
    CONSTRAINT UK_NTF_CANAL_NOME UNIQUE (NOME),
    CONSTRAINT CK_NTF_CANAL_NOME CHECK (NOME IN ('EMAIL', 'SMS', 'PUSH', 'APP')),
    CONSTRAINT CK_NTF_CANAL_ATIVO CHECK (ATIVO IN (0, 1))
);

COMMENT ON TABLE TB_NTF_CANAL IS 'Canais pelos quais as notificacoes podem ser enviadas';
COMMENT ON COLUMN TB_NTF_CANAL.NOME IS 'EMAIL, SMS, PUSH ou APP';
COMMENT ON COLUMN TB_NTF_CANAL.ATIVO IS '1 = canal disponivel, 0 = desativado';

-- ---------------------------------------------------------------------
-- Modelos (templates) de mensagem por tipo de evento
-- ---------------------------------------------------------------------
CREATE TABLE TB_NTF_MODELO (
    ID           VARCHAR2(36)   NOT NULL,
    CODIGO       VARCHAR2(50)   NOT NULL,
    TITULO       VARCHAR2(150)  NOT NULL,
    CONTEUDO     CLOB           NOT NULL,
    TIPO_EVENTO  VARCHAR2(30)   NOT NULL,
    ATIVO        NUMBER(1)      DEFAULT 1 NOT NULL,
    CRIADO_EM    TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_NTF_MODELO PRIMARY KEY (ID),
    CONSTRAINT UK_NTF_MODELO_CODIGO UNIQUE (CODIGO),
    CONSTRAINT CK_NTF_MODELO_TIPO_EVENTO CHECK (TIPO_EVENTO IN (
        'VENCIMENTO_CONTA', 'SALDO_BAIXO', 'TRANSACAO_REALIZADA',
        'RENDIMENTO_INVESTIMENTO', 'PARCELA_DIVIDA', 'META_ATINGIDA',
        'NOVO_CONTEUDO', 'RELATORIO_DISPONIVEL', 'SISTEMA')),
    CONSTRAINT CK_NTF_MODELO_ATIVO CHECK (ATIVO IN (0, 1))
);

COMMENT ON TABLE TB_NTF_MODELO IS 'Modelos de mensagem usados para gerar as notificacoes';
COMMENT ON COLUMN TB_NTF_MODELO.CODIGO IS 'Codigo unico do modelo (ex.: PARCELA_VENCENDO)';
COMMENT ON COLUMN TB_NTF_MODELO.CONTEUDO IS 'Texto do modelo, pode conter marcadores como {nome} e {valor}';
COMMENT ON COLUMN TB_NTF_MODELO.TIPO_EVENTO IS 'Evento do sistema que dispara este modelo';

-- ---------------------------------------------------------------------
-- Preferencias da pessoa: quais eventos recebe e por qual canal
-- ---------------------------------------------------------------------
CREATE TABLE TB_NTF_PREFERENCIA (
    ID                 VARCHAR2(36)   NOT NULL,
    ID_PESSOA          VARCHAR2(36)   NOT NULL,
    ID_CANAL           VARCHAR2(36)   NOT NULL,
    TIPO_EVENTO        VARCHAR2(30)   NOT NULL,
    HABILITADO         NUMBER(1)      DEFAULT 1 NOT NULL,
    ANTECEDENCIA_DIAS  NUMBER(10)     DEFAULT 0 NOT NULL,
    ATUALIZADO_EM      TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_NTF_PREFERENCIA PRIMARY KEY (ID),
    CONSTRAINT UK_NTF_PREFERENCIA_EVENTO UNIQUE (ID_PESSOA, ID_CANAL, TIPO_EVENTO),
    CONSTRAINT FK_NTF_PREFERENCIA_CANAL FOREIGN KEY (ID_CANAL) REFERENCES TB_NTF_CANAL (ID),
    CONSTRAINT FK_NTF_PREFERENCIA_PESSOA FOREIGN KEY (ID_PESSOA) REFERENCES TB_PES_USUARIO (ID),
    CONSTRAINT CK_NTF_PREFERENCIA_EVENTO CHECK (TIPO_EVENTO IN (
        'VENCIMENTO_CONTA', 'SALDO_BAIXO', 'TRANSACAO_REALIZADA',
        'RENDIMENTO_INVESTIMENTO', 'PARCELA_DIVIDA', 'META_ATINGIDA',
        'NOVO_CONTEUDO', 'RELATORIO_DISPONIVEL', 'SISTEMA')),
    CONSTRAINT CK_NTF_PREFERENCIA_HABILITADO CHECK (HABILITADO IN (0, 1)),
    CONSTRAINT CK_NTF_PREFERENCIA_DIAS CHECK (ANTECEDENCIA_DIAS BETWEEN 0 AND 30)
);

COMMENT ON TABLE TB_NTF_PREFERENCIA IS 'Preferencias de recebimento de notificacoes de cada pessoa';
COMMENT ON COLUMN TB_NTF_PREFERENCIA.ID_PESSOA IS 'Id da pessoa (modulo 01 - Cadastro Pessoas)';
COMMENT ON COLUMN TB_NTF_PREFERENCIA.ANTECEDENCIA_DIAS IS 'Quantos dias antes do evento a pessoa quer ser avisada';

-- ---------------------------------------------------------------------
-- Notificacoes geradas para as pessoas
-- ---------------------------------------------------------------------
CREATE TABLE TB_NTF_NOTIFICACAO (
    ID             VARCHAR2(36)   NOT NULL,
    ID_PESSOA      VARCHAR2(36)   NOT NULL,
    ID_CANAL       VARCHAR2(36)   NOT NULL,
    ID_MODELO      VARCHAR2(36),
    TITULO         VARCHAR2(150)  NOT NULL,
    MENSAGEM       CLOB           NOT NULL,
    TIPO_EVENTO    VARCHAR2(30)   NOT NULL,
    PRIORIDADE     VARCHAR2(10)   DEFAULT 'MEDIA' NOT NULL,
    STATUS         VARCHAR2(20)   DEFAULT 'PENDENTE' NOT NULL,
    ORIGEM         VARCHAR2(30)   NOT NULL,
    ID_REFERENCIA  VARCHAR2(36),
    AGENDADA_PARA  TIMESTAMP,
    ENVIADA_EM     TIMESTAMP,
    LIDA_EM        TIMESTAMP,
    CRIADO_EM      TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_NTF_NOTIFICACAO PRIMARY KEY (ID),
    CONSTRAINT FK_NTF_NOTIFICACAO_CANAL FOREIGN KEY (ID_CANAL) REFERENCES TB_NTF_CANAL (ID),
    CONSTRAINT FK_NTF_NOTIFICACAO_PESSOA FOREIGN KEY (ID_PESSOA) REFERENCES TB_PES_USUARIO (ID),
    CONSTRAINT FK_NTF_NOTIFICACAO_MODELO FOREIGN KEY (ID_MODELO) REFERENCES TB_NTF_MODELO (ID),
    CONSTRAINT CK_NTF_NOTIFICACAO_EVENTO CHECK (TIPO_EVENTO IN (
        'VENCIMENTO_CONTA', 'SALDO_BAIXO', 'TRANSACAO_REALIZADA',
        'RENDIMENTO_INVESTIMENTO', 'PARCELA_DIVIDA', 'META_ATINGIDA',
        'NOVO_CONTEUDO', 'RELATORIO_DISPONIVEL', 'SISTEMA')),
    CONSTRAINT CK_NTF_NOTIFICACAO_PRIORIDADE CHECK (PRIORIDADE IN ('BAIXA', 'MEDIA', 'ALTA')),
    CONSTRAINT CK_NTF_NOTIFICACAO_STATUS CHECK (STATUS IN ('PENDENTE', 'ENVIADA', 'LIDA', 'FALHA', 'CANCELADA')),
    CONSTRAINT CK_NTF_NOTIFICACAO_ORIGEM CHECK (ORIGEM IN (
        'PESSOAS', 'CONTAS', 'TRANSACOES', 'INVESTIMENTOS', 'DIVIDAS',
        'PLANEJAMENTOS', 'EDUCACAO', 'NOTIFICACOES'))
);

COMMENT ON TABLE TB_NTF_NOTIFICACAO IS 'Notificacoes geradas para as pessoas';
COMMENT ON COLUMN TB_NTF_NOTIFICACAO.ID_PESSOA IS 'Id da pessoa destinataria (modulo 01 - Cadastro Pessoas)';
COMMENT ON COLUMN TB_NTF_NOTIFICACAO.ID_MODELO IS 'Modelo usado para gerar a mensagem (opcional)';
COMMENT ON COLUMN TB_NTF_NOTIFICACAO.ORIGEM IS 'Modulo que gerou o evento da notificacao';
COMMENT ON COLUMN TB_NTF_NOTIFICACAO.ID_REFERENCIA IS 'Id do registro do modulo de origem que gerou a notificacao';
COMMENT ON COLUMN TB_NTF_NOTIFICACAO.AGENDADA_PARA IS 'Data e hora programadas para o envio';

-- ---------------------------------------------------------------------
-- Historico de tentativas de envio de cada notificacao
-- ---------------------------------------------------------------------
CREATE TABLE TB_NTF_ENVIO (
    ID                VARCHAR2(36)   NOT NULL,
    ID_NOTIFICACAO    VARCHAR2(36)   NOT NULL,
    NUMERO_TENTATIVA  NUMBER(10)     NOT NULL,
    STATUS            VARCHAR2(20)   NOT NULL,
    MENSAGEM_ERRO     VARCHAR2(500),
    DATA_ENVIO        TIMESTAMP      DEFAULT SYSTIMESTAMP NOT NULL,
    CONSTRAINT PK_NTF_ENVIO PRIMARY KEY (ID),
    CONSTRAINT UK_NTF_ENVIO_TENTATIVA UNIQUE (ID_NOTIFICACAO, NUMERO_TENTATIVA),
    CONSTRAINT FK_NTF_ENVIO_NOTIFICACAO FOREIGN KEY (ID_NOTIFICACAO) REFERENCES TB_NTF_NOTIFICACAO (ID),
    CONSTRAINT CK_NTF_ENVIO_TENTATIVA CHECK (NUMERO_TENTATIVA > 0),
    CONSTRAINT CK_NTF_ENVIO_STATUS CHECK (STATUS IN ('SUCESSO', 'FALHA'))
);

COMMENT ON TABLE TB_NTF_ENVIO IS 'Historico das tentativas de envio de cada notificacao';
COMMENT ON COLUMN TB_NTF_ENVIO.NUMERO_TENTATIVA IS 'Numero sequencial da tentativa (1, 2, 3...)';

-- ---------------------------------------------------------------------
-- Dados iniciais: canais disponiveis
-- ---------------------------------------------------------------------
INSERT INTO TB_NTF_CANAL (ID, NOME, DESCRICAO, ATIVO) VALUES ('7c1e8a52-0b4f-4c55-9d1a-1f0e6a3b2c01', 'EMAIL', 'Envio por e-mail', 1);
INSERT INTO TB_NTF_CANAL (ID, NOME, DESCRICAO, ATIVO) VALUES ('7c1e8a52-0b4f-4c55-9d1a-1f0e6a3b2c02', 'SMS', 'Envio por mensagem de texto', 1);
INSERT INTO TB_NTF_CANAL (ID, NOME, DESCRICAO, ATIVO) VALUES ('7c1e8a52-0b4f-4c55-9d1a-1f0e6a3b2c03', 'PUSH', 'Notificacao push no celular', 1);
INSERT INTO TB_NTF_CANAL (ID, NOME, DESCRICAO, ATIVO) VALUES ('7c1e8a52-0b4f-4c55-9d1a-1f0e6a3b2c04', 'APP', 'Caixa de entrada dentro do aplicativo', 1);
