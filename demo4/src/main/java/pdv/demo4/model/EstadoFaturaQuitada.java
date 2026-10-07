package pdv.demo4.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Estado final da fatura depois do pagamento.
 *
 * <p>O estado quitado é imutável do ponto de vista do ciclo de vida: não retorna
 * a aberto/atrasado e rejeita uma segunda tentativa de quitação.</p>
 */
public final class EstadoFaturaQuitada extends EstadoFatura {
    private static final long serialVersionUID = 1L;

    public EstadoFaturaQuitada(FaturaFinanceira fatura) {
        super(fatura);
    }

    @Override
    public EstadoFatura atualizar(LocalDate hoje) {
        Objects.requireNonNull(hoje, "A data atual é obrigatória");
        return this;
    }

    @Override
    public void marcarComoQuitada(LocalDateTime dataPagamento) {
        validarDataPagamento(dataPagamento);
        throw new IllegalStateException("A fatura já foi quitada");
    }

    @Override
    public String getNome() {
        return "QUITADA";
    }

    @Override
    public boolean estaQuitada() {
        return true;
    }
}
