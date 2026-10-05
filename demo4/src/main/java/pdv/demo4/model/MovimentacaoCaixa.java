package pdv.demo4.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Lançamento financeiro no fluxo de caixa.
 * Pode ser ENTRADA automática (venda PDV) ou SAIDA manual (despesa).
 */
public class MovimentacaoCaixa implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private TipoMovimentacao tipo;
    private BigDecimal valor;          // sempre positivo; sinal dado pelo tipo
    private LocalDateTime dataHora;
    private String descricao;
    private Empresa empresa;           // vínculo; ~obrigatório para SAIDA
    private OrigemMovimentacao origem;

    public MovimentacaoCaixa(TipoMovimentacao tipo, BigDecimal valor,
                             LocalDateTime dataHora, String descricao,
                             Empresa empresa, OrigemMovimentacao origem) {
        this.id = UUID.randomUUID().toString();
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora == null ? LocalDateTime.now() : dataHora;
        this.descricao = descricao == null ? "" : descricao.trim();
        this.empresa = empresa;
        this.origem = origem;
    }

    // getters / setters
    public String getId() { return id; }
    public TipoMovimentacao getTipo() { return tipo; }
    public void setTipo(TipoMovimentacao tipo) { this.tipo = tipo; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public Empresa getEmpresa() { return empresa; }
    public void setEmpresa(Empresa empresa) { this.empresa = empresa; }
    public OrigemMovimentacao getOrigem() { return origem; }
    public void setOrigem(OrigemMovimentacao origem) { this.origem = origem; }

    public String getDataFormatada() {
        return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getValorFormatado() {
        String sinal = tipo == TipoMovimentacao.SAIDA ? "-" : "+";
        return sinal + " R$ " + String.format("%.2f", valor);
    }

    @Override
    public String toString() {
        String forn = empresa != null ? " | " + empresa.getNomeRazao() : "";
        return "[" + tipo + "] " + getDataFormatada() + forn + " | " + descricao + " | " + getValorFormatado();
    }
}
