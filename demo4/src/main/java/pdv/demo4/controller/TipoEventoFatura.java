package pdv.demo4.controller;

/** Tipos de notificações que o domínio financeiro publica sobre uma fatura. */
public enum TipoEventoFatura {
    FATURA_CRIADA,
    FATURA_QUITADA,
    LEMBRETE_VENCIMENTO,
    VENCIMENTO_HOJE,
    FATURA_ATRASADA
}
