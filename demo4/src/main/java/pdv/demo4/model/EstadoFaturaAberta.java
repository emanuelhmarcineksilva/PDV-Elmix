package pdv.demo4.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Estado inicial da fatura, enquanto ainda não venceu.
 *
 * <p>Uma fatura vence no fim do próprio dia de vencimento. Ao consultar uma
 * data posterior, este objeto troca o estado do contexto para {@link EstadoFaturaAtrasada}.</p>
 */
public final class EstadoFaturaAberta extends EstadoFatura {
    private static final long serialVersionUID = 1L;

    public EstadoFaturaAberta(FaturaFinanceira fatura) {
        super(fatura);
    }

    @Override
    public EstadoFatura atualizar(LocalDate hoje) {
        Objects.requireNonNull(hoje, "A data atual é obrigatória");
        if (fatura.getDataVencimento().isBefore(hoje)) {
            EstadoFatura novoEstado = new EstadoFaturaAtrasada(fatura);
            fatura.alterarEstado(novoEstado);
            return novoEstado;
        }
        return this;
    }

    @Override
    public void marcarComoQuitada(LocalDateTime dataPagamento) {
        concluirQuitacao(dataPagamento);
    }

    @Override
    public String getNome() {
        return "ABERTA";
    }

    @Override
    public boolean estaQuitada() {
        return false;
    }
}
