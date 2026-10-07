package pdv.demo4.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Contexto do padrão State: mantém os dados da fatura e delega ao estado atual
 * as regras de vencimento e quitação.
 *
 * <p>O estado começa como aberta, muda para atrasada quando uma consulta observa
 * que o vencimento passou e termina como quitada quando o pagamento é aceito.
 * Os próprios objetos de estado realizam essas transições.</p>
 */
public class FaturaFinanceira implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private final BigDecimal valor;
    private final LocalDate dataVencimento;
    private final String descricao;
    private final Empresa empresa;
    private final LocalDateTime criadaEm;
    private LocalDateTime quitadaEm;
    private EstadoFatura estado;

    public FaturaFinanceira(BigDecimal valor, LocalDate dataVencimento, String descricao, Empresa empresa) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        this.id = UUID.randomUUID().toString();
        this.valor = valor;
        this.dataVencimento = Objects.requireNonNull(dataVencimento, "A data de vencimento é obrigatória");
        this.descricao = descricao == null ? "" : descricao.trim();
        this.empresa = Objects.requireNonNull(empresa, "Fornecedor/Empresa é obrigatório");
        this.criadaEm = LocalDateTime.now();
        // A fatura é o contexto e entrega a si mesma ao estado, como no exemplo
        // do AudioPlayer, para que o estado possa efetuar transições no contexto.
        this.estado = new EstadoFaturaAberta(this);
    }

    public String getId() { return id; }
    public BigDecimal getValor() { return valor; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public String getDescricao() { return descricao; }
    public Empresa getEmpresa() { return empresa; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public LocalDateTime getQuitadaEm() { return quitadaEm; }

    /**
     * Retorna o estado atual usando a data do sistema.
     * A consulta também permite que a própria fatura avance de aberta para atrasada.
     */
    public EstadoFatura getEstado() {
        return getEstado(LocalDate.now());
    }

    /**
     * Retorna o estado usando uma data fornecida, útil para regras de negócio e testes
     * determinísticos sem depender do relógio do computador.
     */
    public EstadoFatura getEstado(LocalDate hoje) {
        estado = estado.atualizar(Objects.requireNonNull(hoje, "A data atual é obrigatória"));
        return estado;
    }

    /**
     * Pede ao estado atual para efetuar a quitação. A data do pagamento também
     * atualiza o vencimento antes da delegação, de modo que estados aberta e
     * atrasada possam aceitar o pagamento, enquanto quitada o rejeita.
     */
    public void marcarComoQuitada(LocalDateTime dataPagamento) {
        if (dataPagamento == null) {
            throw new IllegalArgumentException("A data de pagamento é obrigatória");
        }
        estado = estado.atualizar(dataPagamento.toLocalDate());
        estado.marcarComoQuitada(dataPagamento);
    }

    /**
     * Instala o novo estado. Visibilidade de pacote mantém a transição sob
     * controle dos estados deste modelo, não da interface ou do controlador.
     */
    void alterarEstado(EstadoFatura novoEstado) {
        this.estado = Objects.requireNonNull(novoEstado, "O novo estado é obrigatório");
    }

    /** Registra a data de quitação após a validação feita pelo estado atual. */
    void registrarQuitacao(LocalDateTime dataPagamento) {
        quitadaEm = dataPagamento;
    }

    /** Atalho semântico para consumidores que só precisam habilitar ações. */
    public boolean estaQuitada() {
        return getEstado().estaQuitada();
    }
}
