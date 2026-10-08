package pdv.demo4.controller;

import pdv.demo4.model.FaturaFinanceira;

import java.util.Objects;

/** Informação enviada pelo publisher aos observadores inscritos. */
public final class EventoFatura {
    private final TipoEventoFatura tipo;
    private final FaturaFinanceira fatura;
    private final long diasEmRelacaoAoVencimento;

    public EventoFatura(TipoEventoFatura tipo, FaturaFinanceira fatura,
                        long diasEmRelacaoAoVencimento) {
        this.tipo = Objects.requireNonNull(tipo, "O tipo do evento é obrigatório");
        this.fatura = Objects.requireNonNull(fatura, "A fatura do evento é obrigatória");
        this.diasEmRelacaoAoVencimento = diasEmRelacaoAoVencimento;
    }

    public TipoEventoFatura getTipo() {
        return tipo;
    }

    public FaturaFinanceira getFatura() {
        return fatura;
    }

    /**
     * Dias até o vencimento para lembretes ou dias de atraso para faturas vencidas.
     * Para eventos de criação e quitação, o valor é zero.
     */
    public long getDiasEmRelacaoAoVencimento() {
        return diasEmRelacaoAoVencimento;
    }
}
