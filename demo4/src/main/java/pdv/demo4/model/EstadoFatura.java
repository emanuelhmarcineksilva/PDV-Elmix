package pdv.demo4.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Estado abstrato do padrão State para uma fatura.
 *
 * <p>A fatura é o contexto: ela encaminha consultas e pedidos de quitação para
 * o objeto de estado atual. Cada estado decide como responder e quando trocar
 * por outro estado, evitando espalhar regras de transição pela tela e pelo
 * controlador financeiro.</p>
 *
 * <p>O estado guarda uma referência ao contexto, como no exemplo do AudioPlayer.
 * Ambos são serializáveis para que a referência e o estado atual sejam
 * preservados junto com a fatura no arquivo financeiro.</p>
 */
public abstract class EstadoFatura implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Contexto cujas regras e transições este estado controla. */
    protected final FaturaFinanceira fatura;

    protected EstadoFatura(FaturaFinanceira fatura) {
        this.fatura = Objects.requireNonNull(fatura, "A fatura do estado é obrigatória");
    }

    /**
     * Atualiza o estado de acordo com a data de referência.
     * Estados já quitados continuam quitados; estados vencidos não voltam a abertos.
     */
    public abstract EstadoFatura atualizar(LocalDate hoje);

    /**
     * Executa o pedido de quitação segundo as regras específicas do estado atual.
     */
    public abstract void marcarComoQuitada(LocalDateTime dataPagamento);

    /** Nome estável para apresentar o estado na interface. */
    public abstract String getNome();

    /** Informa se este estado representa uma fatura já quitada. */
    public abstract boolean estaQuitada();

    /**
     * Finaliza a quitação após o estado permitir a operação.
     * Centraliza as duas alterações que precisam acontecer juntas no contexto:
     * registrar a data e instalar o novo objeto de estado.
     */
    protected final void concluirQuitacao(LocalDateTime dataPagamento) {
        validarDataPagamento(dataPagamento);
        fatura.registrarQuitacao(dataPagamento);
        fatura.alterarEstado(new EstadoFaturaQuitada(fatura));
    }

    /** Rejeita datas inválidas antes que qualquer dado da fatura seja alterado. */
    protected final void validarDataPagamento(LocalDateTime dataPagamento) {
        if (dataPagamento == null) {
            throw new IllegalArgumentException("A data de pagamento é obrigatória");
        }
    }
}
