package pdv.demo4.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma venda completa com seus itens.
 * Guarda data/hora, lista de itens, valor pago e troco.
 */
public class Venda implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;                        // Identificador da venda
    private List<ItemVenda> itens;         // Lista de itens vendidos
    private double totalVenda;             // Total da venda
    private double valorPago;              // Quanto o cliente pagou
    private double troco;                  // Troco devolvido
    private LocalDateTime dataHora;        // Data e hora da venda

    public Venda(int id) {
        this.id = id;
        this.itens = new ArrayList<>();
        this.totalVenda = 0.0;
        this.valorPago = 0.0;
        this.troco = 0.0;
        this.dataHora = LocalDateTime.now();
    }

    /**
     * Cria uma nova venda a partir deste protótipo, copiando os itens sem compartilhar estado.
     */
    public Venda copiarComoNova(int novoId) {
        Venda copia = new Venda(novoId);
        for (ItemVenda item : itens) {
            copia.adicionarItem(new ItemVenda(item.getNomeProduto(), item.getValor()));
        }
        return copia;
    }

    /**
     * Adiciona um item à venda e recalcula o total.
     */
    public void adicionarItem(ItemVenda item) {
        itens.add(item);
        recalcularTotal();
    }

    /**
     * Remove um item da venda pelo índice.
     */
    public void removerItem(int indice) {
        if (indice >= 0 && indice < itens.size()) {
            itens.remove(indice);
            recalcularTotal();
        }
    }

    /**
     * Recalcula o total somando todos os itens.
     */
    private void recalcularTotal() {
        totalVenda = itens.stream().mapToDouble(ItemVenda::getValor).sum();
    }

    /**
     * Finaliza a venda calculando o troco.
     */
    public void finalizarVenda(double valorPago) {
        this.valorPago = valorPago;
        this.troco = valorPago - totalVenda;
        this.dataHora = LocalDateTime.now();
    }

    // --- Getters e Setters ---

    public int getId() {
        return id;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public double getTotalVenda() {
        return totalVenda;
    }

    public double getValorPago() {
        return valorPago;
    }

    public double getTroco() {
        return troco;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setValorPago(double valorPago) {
        this.valorPago = valorPago;
        this.troco = valorPago - totalVenda;
    }

    public void atualizarPagamento(double valorPago) {
        setValorPago(valorPago);
    }

    /**
     * Retorna a data formatada para exibição.
     */
    public String getDataFormatada() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dataHora.format(fmt);
    }

    @Override
    public String toString() {
        return "Venda #" + id + " | " + getDataFormatada() + " | Total: R$ " + String.format("%.2f", totalVenda);
    }
}
