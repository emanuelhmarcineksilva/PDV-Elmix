package pdv.demo4.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Pacote único de restauração do sistema.
 */
public class BackupCompleto implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Venda> vendas = new ArrayList<>();
    private DadosFinanceiros dadosFinanceiros = new DadosFinanceiros();
    private Configuracao configuracao;

    public List<Venda> getVendas() { return vendas; }
    public void setVendas(List<Venda> vendas) { this.vendas = vendas == null ? new ArrayList<>() : vendas; }
    public DadosFinanceiros getDadosFinanceiros() { return dadosFinanceiros; }
    public void setDadosFinanceiros(DadosFinanceiros dadosFinanceiros) { this.dadosFinanceiros = dadosFinanceiros; }
    public Configuracao getConfiguracao() { return configuracao; }
    public void setConfiguracao(Configuracao configuracao) { this.configuracao = configuracao; }
}
