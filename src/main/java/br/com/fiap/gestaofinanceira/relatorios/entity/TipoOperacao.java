package br.com.fiap.gestaofinanceira.relatorios.entity;

/**
 * Natureza do valor consolidado em um item de relatorio.
 * Persistido como texto (EnumType.STRING) em TB_REL_ITEM.TIPO_OPERACAO.
 */
public enum TipoOperacao {
    RECEITA,
    DESPESA
}
