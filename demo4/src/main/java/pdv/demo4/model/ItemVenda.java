package pdv.demo4.model;

import java.io.Serializable;

/**
 * Representa um item dentro de uma venda.
 * Cada item tem nome do produto e valor.
 */
public class ItemVenda implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomeProduto; // Nome do produto (ex: "Mel Orgânico")
    private double valor;       // Valor do item

    public ItemVenda(String nomeProduto, double valor) {
        // Se o nome estiver vazio, coloca "Diversos" como padrão
        this.nomeProduto = (nomeProduto == null || nomeProduto.trim().isEmpty()) ? "Diversos" : nomeProduto.trim();
        this.valor = valor;
    }

    // --- Getters e Setters ---

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return nomeProduto + " - R$ " + String.format("%.2f", valor);
    }
}
