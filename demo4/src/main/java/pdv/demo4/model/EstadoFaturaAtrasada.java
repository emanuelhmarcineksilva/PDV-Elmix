package pdv.demo4.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Estado de uma fatura não quitada cuja data de vencimento já passou.
 *
 * <p>Uma fatura atrasada ainda pode ser quitada. Consultas futuras mantêm este
 * estado até a transição explícita para quitada.</p>
 */
public final class EstadoFaturaAtrasada extends EstadoFatura {
    private static final long serialVersionUID = 1L;

    public EstadoFaturaAtrasada(FaturaFinanceira fatura) {
        super(fatura);
    }

    @Override
    public EstadoFatura atualizar(LocalDate hoje) {
        Objects.requireNonNull(hoje, "A data atual é obrigatória");
        return this;
    }

    @Override
    public void marcarComoQuitada(LocalDateTime dataPagamento) {
        concluirQuitacao(dataPagamento);
    }

    @Override
    public String getNome() {
        return "ATRASADA";
    }

    @Override
    public boolean estaQuitada() {
        return false;
    }
}
