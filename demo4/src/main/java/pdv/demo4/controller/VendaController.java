package pdv.demo4.controller;

import pdv.demo4.model.ItemVenda;
import pdv.demo4.model.Venda;
import pdv.demo4.util.PersistenciaUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de vendas.
 * Gerencia a criação, finalização e persistência das vendas.
 */
public class VendaController {

    private static final String ARQUIVO_VENDAS = "vendas.ser"; // Arquivo de persistência
    private List<Venda> vendas;     // Lista de todas as vendas
    private Venda vendaAtual;       // Venda em andamento
    private int proximoId;          // Próximo ID de venda
    private static int ultimoIdGlobal;

    public VendaController() {
        // Carrega vendas salvas
        vendas = PersistenciaUtil.carregarLista(ARQUIVO_VENDAS);
        // Define o próximo ID baseado nas vendas existentes
        proximoId = vendas.stream().mapToInt(Venda::getId).max().orElse(0) + 1;
        synchronized (VendaController.class) {
            ultimoIdGlobal = Math.max(ultimoIdGlobal, proximoId - 1);
        }
    }

    /**
     * Inicia uma nova venda.
     */
    public synchronized Venda novaVenda() {
        return novaVenda(null);
    }

    /**
     * Inicia uma nova venda copiando os itens de um protótipo, se informado.
     */
    public synchronized Venda novaVenda(Venda prototipo) {
        int novoId;
        synchronized (VendaController.class) {
            proximoId = Math.max(proximoId, ultimoIdGlobal + 1);
            novoId = proximoId++;
            ultimoIdGlobal = novoId;
        }
        vendaAtual = prototipo == null ? new Venda(novoId) : prototipo.copiarComoNova(novoId);
        return vendaAtual;
    }

    /**
     * Adiciona um item à venda atual.
     */
    public void adicionarItem(String nomeProduto, double valor) {
        if (vendaAtual != null) {
            vendaAtual.adicionarItem(new ItemVenda(nomeProduto, valor));
        }
    }

    /**
     * Remove um item da venda atual pelo índice.
     */
    public void removerItem(int indice) {
        if (vendaAtual != null) {
            vendaAtual.removerItem(indice);
        }
    }

    /**
     * Finaliza a venda atual, calcula o troco, salva e retorna.
     */
    public synchronized Venda finalizarVenda(double valorPago) {
        if (vendaAtual != null && !vendaAtual.getItens().isEmpty()) {
            vendaAtual.finalizarVenda(valorPago);
            Venda finalizada = vendaAtual;
            synchronized (VendaController.class) {
                List<Venda> vendasPersistidas = PersistenciaUtil.carregarLista(ARQUIVO_VENDAS);
                vendasPersistidas.add(finalizada);
                vendas = vendasPersistidas;
                salvarVendas();
            }
            vendaAtual = null;
            return finalizada;
        }
        return null;
    }

    /**
     * Retorna a venda em andamento.
     */
    public Venda getVendaAtual() {
        return vendaAtual;
    }

    /**
     * Retorna todas as vendas salvas.
     */
    public List<Venda> getTodasVendas() {
        return vendas;
    }

    /**
     * Atualiza a lista em memória com as vendas persistidas por outros PDVs.
     */
    public synchronized void recarregar() {
        vendas = PersistenciaUtil.carregarLista(ARQUIVO_VENDAS);
        proximoId = vendas.stream().mapToInt(Venda::getId).max().orElse(0) + 1;
        synchronized (VendaController.class) {
            ultimoIdGlobal = Math.max(ultimoIdGlobal, proximoId - 1);
        }
    }

    public synchronized void restaurarVendas(List<Venda> novasVendas) {
        vendas = novasVendas == null ? new java.util.ArrayList<>() : new java.util.ArrayList<>(novasVendas);
        proximoId = vendas.stream().mapToInt(Venda::getId).max().orElse(0) + 1;
        vendaAtual = null;
        salvarVendas();
    }

    public boolean atualizarVenda(Venda venda) {
        if (venda == null) {
            return false;
        }
        int index = -1;
        for (int i = 0; i < vendas.size(); i++) {
            if (vendas.get(i).getId() == venda.getId()) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            vendas.set(index, venda);
            salvarVendas();
            new FinanceiroController().sincronizarEntradaVenda(venda);
            return true;
        }
        return false;
    }

    public boolean removerVenda(Venda venda) {
        if (venda == null) {
            return false;
        }
        boolean removida = vendas.removeIf(v -> v.getId() == venda.getId());
        if (removida) {
            salvarVendas();
            new FinanceiroController().removerEntradaVenda(venda.getId());
        }
        return removida;
    }

    /**
     * Filtra vendas por período (para backup e relatórios).
     */
    public List<Venda> getVendasPorPeriodo(LocalDate inicio, LocalDate fim) {
        return vendas.stream()
                .filter(v -> {
                    LocalDate dataVenda = v.getDataHora().toLocalDate();
                    return !dataVenda.isBefore(inicio) && !dataVenda.isAfter(fim);
                })
                .collect(Collectors.toList());
    }

    /**
     * Gera o relatório TXT de fechamento do caixa (vendas do dia).
     * Formato simples e fácil de entender.
     */
    public String gerarRelatorioFechamento() {
        LocalDate hoje = LocalDate.now();
        List<Venda> vendasHoje = getVendasPorPeriodo(hoje, hoje);

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("       FECHAMENTO DE CAIXA - ELMIX\n");
        sb.append("==========================================\n");
        sb.append("Data: ").append(hoje.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        sb.append("------------------------------------------\n\n");

        double totalDia = 0;

        if (vendasHoje.isEmpty()) {
            sb.append("Nenhuma venda realizada hoje.\n");
        } else {
            for (Venda v : vendasHoje) {
                sb.append("Venda #").append(v.getId()).append(" - ").append(v.getDataFormatada()).append("\n");
                for (ItemVenda item : v.getItens()) {
                    sb.append("  • ").append(item.toString()).append("\n");
                }
                sb.append("  Total: R$ ").append(String.format("%.2f", v.getTotalVenda())).append("\n\n");
                totalDia += v.getTotalVenda();
            }
        }

        sb.append("------------------------------------------\n");
        sb.append("Total de vendas: ").append(vendasHoje.size()).append("\n");
        sb.append("TOTAL DO DIA: R$ ").append(String.format("%.2f", totalDia)).append("\n");
        sb.append("==========================================\n");

        return sb.toString();
    }

    /**
     * Salva o relatório de fechamento em um arquivo TXT.
     */
    public boolean salvarRelatorioFechamento(String caminhoArquivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {
            writer.write(gerarRelatorioFechamento());
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar relatório: " + e.getMessage());
            return false;
        }
    }

    /**
     * Gera relatório de backup por período.
     */
    public String gerarRelatorioBackup(LocalDate inicio, LocalDate fim) {
        List<Venda> vendasPeriodo = getVendasPorPeriodo(inicio, fim);

        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("       BACKUP DE VENDAS - ELMIX\n");
        sb.append("==========================================\n");
        sb.append("Período: ").append(inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sb.append(" até ").append(fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
        sb.append("------------------------------------------\n\n");

        double totalPeriodo = 0;

        for (Venda v : vendasPeriodo) {
            sb.append("Venda #").append(v.getId()).append(" - ").append(v.getDataFormatada()).append("\n");
            for (ItemVenda item : v.getItens()) {
                sb.append("  • ").append(item.toString()).append("\n");
            }
            sb.append("  Total: R$ ").append(String.format("%.2f", v.getTotalVenda())).append("\n\n");
            totalPeriodo += v.getTotalVenda();
        }

        sb.append("------------------------------------------\n");
        sb.append("Total de vendas no período: ").append(vendasPeriodo.size()).append("\n");
        sb.append("TOTAL DO PERÍODO: R$ ").append(String.format("%.2f", totalPeriodo)).append("\n");
        sb.append("==========================================\n");

        return sb.toString();
    }

    /**
     * Salva backup em TXT.
     */
    public boolean salvarBackup(String caminhoArquivo, LocalDate inicio, LocalDate fim) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {
            writer.write(gerarRelatorioBackup(inicio, fim));
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar backup: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retorna os totais de vendas de segunda a domingo da semana atual.
     */
    public double[] getTotaisSemanaAtual() {
        double[] totais = new double[7]; // Segunda a Domingo
        LocalDate hoje = LocalDate.now();
        // Acha a segunda-feira da semana atual
        LocalDate segunda = hoje.minusDays(hoje.getDayOfWeek().getValue() - 1);
        for (int i = 0; i < 7; i++) {
            LocalDate dia = segunda.plusDays(i);
            totais[i] = getVendasPorPeriodo(dia, dia).stream().mapToDouble(Venda::getTotalVenda).sum();
        }
        return totais;
    }

    /**
     * Retorna os totais das 4 semanas do mês atual (1-7, 8-14, 15-21, 22-fim).
     */
    public double[] getTotaisSemanasMesAtual() {
        double[] totais = new double[4];
        LocalDate hoje = LocalDate.now();
        int ano = hoje.getYear();
        int mes = hoje.getMonthValue();
        
        // Semana 1: 1 a 7
        totais[0] = getVendasPorPeriodo(LocalDate.of(ano, mes, 1), LocalDate.of(ano, mes, 7)).stream().mapToDouble(Venda::getTotalVenda).sum();
        // Semana 2: 8 a 14
        totais[1] = getVendasPorPeriodo(LocalDate.of(ano, mes, 8), LocalDate.of(ano, mes, 14)).stream().mapToDouble(Venda::getTotalVenda).sum();
        // Semana 3: 15 a 21
        totais[2] = getVendasPorPeriodo(LocalDate.of(ano, mes, 15), LocalDate.of(ano, mes, 21)).stream().mapToDouble(Venda::getTotalVenda).sum();
        // Semana 4: 22 a fim do mes
        int ultimoDia = hoje.lengthOfMonth();
        totais[3] = getVendasPorPeriodo(LocalDate.of(ano, mes, 22), LocalDate.of(ano, mes, ultimoDia)).stream().mapToDouble(Venda::getTotalVenda).sum();
        
        return totais;
    }

    /**
     * Gera o resumo de faturamento financeiro.
     */
    public String gerarResumoFinanceiro() {
        double[] semana = getTotaisSemanaAtual();
        double totalSemana = 0;
        for (double d : semana) totalSemana += d;
        
        double[] mes = getTotaisSemanasMesAtual();
        double totalMes = 0;
        for (double d : mes) totalMes += d;
        
        StringBuilder sb = new StringBuilder();
        sb.append("==========================================\n");
        sb.append("      RESUMO FINANCEIRO - ELMIX\n");
        sb.append("==========================================\n");
        sb.append("Gerado em: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");
        sb.append("------------------------------------------\n\n");
        
        sb.append("FATURAMENTO SEMANAL (Segunda a Domingo):\n");
        String[] dias = {"Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado", "Domingo"};
        for (int i = 0; i < 7; i++) {
            sb.append(String.format("  • %-10s: R$ %.2f\n", dias[i], semana[i]));
        }
        sb.append(String.format("  TOTAL DA SEMANA: R$ %.2f\n\n", totalSemana));
        
        sb.append("FATURAMENTO MENSAL (Por Período):\n");
        sb.append(String.format("  • Semana 1 (01-07): R$ %.2f\n", mes[0]));
        sb.append(String.format("  • Semana 2 (08-14): R$ %.2f\n", mes[1]));
        sb.append(String.format("  • Semana 3 (15-21): R$ %.2f\n", mes[2]));
        sb.append(String.format("  • Semana 4 (22-fim): R$ %.2f\n", mes[3]));
        sb.append(String.format("  TOTAL DO MÊS: R$ %.2f\n\n", totalMes));
        
        sb.append("------------------------------------------\n");
        sb.append(String.format("Média diária (esta semana): R$ %.2f\n", totalSemana / 7));
        sb.append(String.format("Média semanal (este mês): R$ %.2f\n", totalMes / 4));
        sb.append("==========================================\n");
        
        return sb.toString();
    }

    /**
     * Salva o resumo de faturamento financeiro em TXT.
     */
    public boolean salvarResumoFinanceiro(String caminhoArquivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(caminhoArquivo))) {
            writer.write(gerarResumoFinanceiro());
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar resumo financeiro: " + e.getMessage());
            return false;
        }
    }

    /**
     * Persiste a lista de vendas no arquivo .ser
     */
    private void salvarVendas() {
        PersistenciaUtil.salvarLista(vendas, ARQUIVO_VENDAS);
    }
}
