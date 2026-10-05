package pdv.demo4.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Contêiner raiz para persistência .ser.
 * Guarda empresas e movimentações juntas (mesma operação de E/S).
 */
public class DadosFinanceiros implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Empresa> empresas = new ArrayList<>();
    private List<MovimentacaoCaixa> movimentacoes = new ArrayList<>();

    public List<Empresa> getEmpresas() { return empresas; }
    public void setEmpresas(List<Empresa> empresas) { this.empresas = empresas; }

    public List<MovimentacaoCaixa> getMovimentacoes() { return movimentacoes; }
    public void setMovimentacoes(List<MovimentacaoCaixa> movimentacoes) { this.movimentacoes = movimentacoes; }
}
