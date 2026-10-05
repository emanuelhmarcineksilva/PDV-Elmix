package pdv.demo4.controller;

import pdv.demo4.model.*;
import pdv.demo4.util.PersistenciaUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controla empresas e movimentações financeiras.
 * Persistência única em dados_elmix/financeiro.ser (DadosFinanceiros),
 * compatível também com caminho spec dados/financeiro.ser via fallback.
 *
 * Thread-safe: métodos de escrita sincronizados.
 */
public class FinanceiroController {

    private static final String ARQUIVO_FINANCEIRO = "financeiro.ser";
    // legado / spec alternativo
    private static final String PASTA_SPEC = "dados";

    private DadosFinanceiros dados;

    public FinanceiroController() {
        this.dados = carregarDados();
    }

    // ---------- Persistência ----------

    private synchronized DadosFinanceiros carregarDados() {
        Object obj = PersistenciaUtil.carregar(ARQUIVO_FINANCEIRO);
        if (obj instanceof DadosFinanceiros) {
            return (DadosFinanceiros) obj;
        }
        // tenta fallback do caminho spec antigo "dados/financeiro.ser"
        try {
            java.io.File f = new java.io.File(PASTA_SPEC + java.io.File.separator + ARQUIVO_FINANCEIRO);
            if (f.exists()) {
                try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(f))) {
                    Object o2 = ois.readObject();
                    if (o2 instanceof DadosFinanceiros) {
                        DadosFinanceiros df = (DadosFinanceiros) o2;
                        // migra para pasta padrão
                        this.dados = df;
                        salvarDados();
                        return df;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Fallback leitura financeiro.ser falhou: " + e.getMessage());
        }
        return new DadosFinanceiros();
    }

    public synchronized void salvarDados() {
        PersistenciaUtil.salvar(dados, ARQUIVO_FINANCEIRO);
    }

    public synchronized void restaurarDados(DadosFinanceiros novosDados) {
        if (novosDados == null) {
            throw new IllegalArgumentException("Dados financeiros ausentes no backup");
        }
        dados = novosDados;
        if (dados.getEmpresas() == null) dados.setEmpresas(new ArrayList<>());
        if (dados.getMovimentacoes() == null) dados.setMovimentacoes(new ArrayList<>());
        salvarDados();
    }

    public synchronized DadosFinanceiros getDadosParaBackup() {
        return dados;
    }

    public synchronized void recarregar() {
        this.dados = carregarDados();
    }

    // ---------- Empresas (RF-02) ----------

    public List<Empresa> getEmpresas() {
        return new ArrayList<>(dados.getEmpresas());
    }

    public List<Empresa> getEmpresasAtivas() {
        return dados.getEmpresas().stream().filter(Empresa::isAtivo).collect(Collectors.toList());
    }

    /**
     * Cadastra nova empresa e persiste. Retorna a empresa criada.
     * Usado pelo atalho + Cadastrar Novo (atualiza .ser e retorna para seleção automática).
     */
    public synchronized Empresa cadastrarEmpresa(String nomeRazao, String cnpjCpf) {
        return cadastrarEmpresa(nomeRazao, cnpjCpf, null);
    }

    public synchronized Empresa cadastrarEmpresa(String nomeRazao, String cnpjCpf, String telefone) {
        if (nomeRazao == null || nomeRazao.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da empresa é obrigatório");
        }
        Empresa e = new Empresa(nomeRazao.trim(), cnpjCpf, telefone);
        dados.getEmpresas().add(e);
        salvarDados();
        return e;
    }

    public synchronized void atualizarEmpresa(Empresa empresa) {
        if (empresa == null || buscarEmpresaPorId(empresa.getId()).isEmpty()) {
            throw new IllegalArgumentException("Empresa não encontrada");
        }
        salvarDados();
    }

    public synchronized boolean excluirEmpresa(Empresa empresa) {
        if (empresa == null) {
            return false;
        }
        boolean atualizada = dados.getEmpresas().stream()
                .filter(e -> e.getId().equals(empresa.getId()))
                .findFirst()
                .map(e -> {
                    e.setAtivo(false);
                    return true;
                })
                .orElse(false);
        if (atualizada) {
            salvarDados();
        }
        return atualizada;
    }

    public Optional<Empresa> buscarEmpresaPorId(String id) {
        return dados.getEmpresas().stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public synchronized void removerEntradaVenda(int codigoVenda) {
        if (codigoVenda <= 0) {
            return;
        }
        String prefixo = "Venda #" + codigoVenda + " -";
        boolean removida = dados.getMovimentacoes().removeIf(m ->
                m.getTipo() == TipoMovimentacao.ENTRADA
                        && m.getOrigem() == OrigemMovimentacao.PDV_AUTOMATICO
                        && m.getDescricao() != null
                        && m.getDescricao().startsWith(prefixo));
        if (removida) {
            salvarDados();
        }
    }

    public synchronized void sincronizarEntradaVenda(Venda venda) {
        if (venda == null) {
            return;
        }
        removerEntradaVenda(venda.getId());
        if (venda.getItens() != null && !venda.getItens().isEmpty() && venda.getTotalVenda() > 0) {
            registrarEntradaVenda(venda.getTotalVenda(), "Venda #" + venda.getId() + " - " + venda.getItens().size() + " item(ns)");
        }
    }

    // ---------- Movimentações ----------

    public List<MovimentacaoCaixa> getMovimentacoes() {
        return new ArrayList<>(dados.getMovimentacoes());
    }

    /**
     * RF-01: registra entrada automática de venda do PDV.
     */
    public synchronized MovimentacaoCaixa registrarEntradaVenda(double valorBruto, String descricaoVenda) {
        if (valorBruto <= 0) return null;
        BigDecimal valor = BigDecimal.valueOf(valorBruto).setScale(2, RoundingMode.HALF_UP);
        String desc = descricaoVenda != null ? descricaoVenda : "Venda PDV";
        MovimentacaoCaixa m = new MovimentacaoCaixa(
                TipoMovimentacao.ENTRADA,
                valor,
                LocalDateTime.now(),
                desc,
                null,
                OrigemMovimentacao.PDV_AUTOMATICO
        );
        dados.getMovimentacoes().add(m);
        salvarDados();
        return m;
    }

    public synchronized MovimentacaoCaixa registrarEntradaVenda(Venda venda) {
        if (venda == null) return null;
        String desc = "Venda #" + venda.getId() + " - " + venda.getItens().size() + " item(ns)";
        return registrarEntradaVenda(venda.getTotalVenda(), desc);
    }

    /**
     * RF-03: lançamento manual de saída (despesa).
     */
    public synchronized MovimentacaoCaixa registrarSaida(BigDecimal valor, Empresa empresa,
                                                         LocalDateTime dataHora, String descricao) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser positivo");
        }
        if (empresa == null) {
            throw new IllegalArgumentException("Fornecedor/Empresa é obrigatório");
        }
        MovimentacaoCaixa m = new MovimentacaoCaixa(
                TipoMovimentacao.SAIDA,
                valor.setScale(2, RoundingMode.HALF_UP),
                dataHora != null ? dataHora : LocalDateTime.now(),
                descricao,
                empresa,
                OrigemMovimentacao.MANUAL
        );
        dados.getMovimentacoes().add(m);
        salvarDados();
        return m;
    }

    public synchronized boolean removerMovimentacao(String id) {
        boolean removed = dados.getMovimentacoes().removeIf(m -> m.getId().equals(id));
        if (removed) salvarDados();
        return removed;
    }

    // ---------- Consultas / Dashboard (RF-04) ----------

    public List<MovimentacaoCaixa> filtrarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return dados.getMovimentacoes().stream()
                .filter(m -> {
                    LocalDate d = m.getDataHora().toLocalDate();
                    boolean afterInicio = inicio == null || !d.isBefore(inicio);
                    boolean beforeFim = fim == null || !d.isAfter(fim);
                    return afterInicio && beforeFim;
                })
                .sorted(Comparator.comparing(MovimentacaoCaixa::getDataHora))
                .collect(Collectors.toList());
    }

    /** Totais para dashboard: [0]=entradas, [1]=saídas, [2]=saldo */
    public BigDecimal[] calcularTotais(List<MovimentacaoCaixa> lista) {
        BigDecimal entradas = lista.stream()
                .filter(m -> m.getTipo() == TipoMovimentacao.ENTRADA)
                .map(MovimentacaoCaixa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saidas = lista.stream()
                .filter(m -> m.getTipo() == TipoMovimentacao.SAIDA)
                .map(MovimentacaoCaixa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldo = entradas.subtract(saidas);
        return new BigDecimal[]{entradas, saidas, saldo};
    }

    public BigDecimal[] calcularTotaisPorPeriodo(LocalDate inicio, LocalDate fim) {
        return calcularTotais(filtrarPorPeriodo(inicio, fim));
    }

    /** Agrupa saídas por empresa para pizza/rosca */
    public Map<Empresa, BigDecimal> agruparGastosPorFornecedor(List<MovimentacaoCaixa> lista) {
        Map<Empresa, BigDecimal> map = new LinkedHashMap<>();
        lista.stream()
                .filter(m -> m.getTipo() == TipoMovimentacao.SAIDA && m.getEmpresa() != null)
                .forEach(m -> map.merge(m.getEmpresa(), m.getValor(), BigDecimal::add));
        return map;
    }

    public Map<Empresa, BigDecimal> agruparGastosPorFornecedor(LocalDate inicio, LocalDate fim) {
        return agruparGastosPorFornecedor(filtrarPorPeriodo(inicio, fim));
    }

    /** Atalhos de período do spec */
    public LocalDate[] intervaloHoje() {
        LocalDate hoje = LocalDate.now();
        return new LocalDate[]{hoje, hoje};
    }

    public LocalDate[] intervaloUltimos7Dias() {
        LocalDate hoje = LocalDate.now();
        return new LocalDate[]{hoje.minusDays(6), hoje};
    }

    public LocalDate[] intervaloMesAtual() {
        LocalDate hoje = LocalDate.now();
        return new LocalDate[]{hoje.withDayOfMonth(1), hoje.withDayOfMonth(hoje.lengthOfMonth())};
    }

    // ---------- Seed para demo (útil p/ primeiro uso) ----------

    public boolean isVazio() {
        return dados.getMovimentacoes().isEmpty() && dados.getEmpresas().isEmpty();
    }

    public synchronized void seedDemoSeVazio() {
        if (!isVazio()) return;
        Empresa e1 = cadastrarEmpresa("Distribuidora Grãos Naturais", "12.345.678/0001-90");
        Empresa e2 = cadastrarEmpresa("Fornecedor Mel Orgânico", "");
        // entradas demo (últimos dias)
        registrarEntradaVenda(320.50, "Venda PDV - demo");
        registrarEntradaVenda(180.00, "Venda PDV - demo");
        // saídas demo
        registrarSaida(new BigDecimal("200.00"), e1, LocalDateTime.now().minusDays(1), "Compra de castanhas a granel");
        registrarSaida(new BigDecimal("85.50"), e2, LocalDateTime.now().minusDays(2), "Reposição mel");
    }
}
