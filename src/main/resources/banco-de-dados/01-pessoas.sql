-- =====================================================================
-- 01-pessoas.sql · Cadastro Pessoas (Grupo 8) · Prefixo TB_PES_
-- Base de todos os modulos: os demais referenciam TB_PES_USUARIO.
-- Os IDs VARCHAR2(36) recebem o UUID gerado pela aplicacao.
-- =====================================================================
CREATE TABLE TB_PES_USUARIO (
    ID VARCHAR2(36) NOT NULL,
    NOME VARCHAR2(100) NOT NULL,
    CPF VARCHAR2(11) NOT NULL,
    EMAIL VARCHAR2(150),
    TELEFONE VARCHAR2(20),

    CONSTRAINT PK_PES_USUARIO PRIMARY KEY (ID),
    CONSTRAINT UK_PES_USUARIO_CPF UNIQUE (CPF)
);

CREATE TABLE TB_PES_ENDERECO (
    ID VARCHAR2(36) NOT NULL,
    ID_USUARIO VARCHAR2(36) NOT NULL,
    CEP VARCHAR2(8) NOT NULL,
    LOGRADOURO VARCHAR2(150) NOT NULL,
    NUMERO VARCHAR2(10) NOT NULL,
    COMPLEMENTO VARCHAR2(100),
    BAIRRO VARCHAR2(100),
    CIDADE VARCHAR2(100) NOT NULL,
    ESTADO VARCHAR2(2) NOT NULL,

    CONSTRAINT PK_PES_ENDERECO PRIMARY KEY (ID),

    CONSTRAINT FK_PES_ENDERECO_USUARIO
        FOREIGN KEY (ID_USUARIO)
            REFERENCES TB_PES_USUARIO(ID)
);

COMMENT ON TABLE TB_PES_USUARIO IS 'Pessoas (usuarios) cadastradas no sistema';
COMMENT ON TABLE TB_PES_ENDERECO IS 'Enderecos de cada usuario (1:N)';
