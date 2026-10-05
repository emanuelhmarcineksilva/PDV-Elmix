package pdv.demo4.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pdv.demo4.model.Configuracao;
import pdv.demo4.model.Venda;
import pdv.demo4.model.ItemVenda;

import java.util.List;
import java.util.Optional;

/**
 * Tela de histórico de vendas.
 * Mostra todas as vendas já realizadas.
 */
public class TelaHistoricoView {

    private BorderPane root;
    private Button btnVoltar;
    private Button btnExcluir;
    private Button btnEditar;
    private ListView<Venda> listaVendas;
    private TextArea txtDetalhes;
    private Configuracao config;

    public TelaHistoricoView(Configuracao config) {
        this.config = config;
        construirTela();
    }

    private void construirTela() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + config.getCorPrincipal() + ";");

        // Header
        btnVoltar = new Button("Voltar");
        btnVoltar.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btnVoltar.setPrefWidth(120);
        btnVoltar.setPrefHeight(40);
        btnVoltar.setStyle("-fx-background-color:#FFF;-fx-text-fill:#2E7D32;-fx-background-radius:12;-fx-cursor:hand;-fx-border-color:#A5D6A7;");
        Label titulo = new Label("Histórico de Vendas");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titulo.setStyle("-fx-text-fill: #1B5E20;");
        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);
        HBox header = new HBox(15, btnVoltar, esp, titulo);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15, 20, 15, 20));
        root.setTop(header);

        btnExcluir = new Button("Excluir venda");
        btnExcluir.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btnExcluir.setPrefWidth(140);
        btnExcluir.setPrefHeight(38);
        btnExcluir.setStyle("-fx-background-color:#FFEBEE;-fx-text-fill:#C62828;-fx-background-radius:10;-fx-cursor:hand;-fx-border-color:#EF9A9A;");

        btnEditar = new Button("Editar venda");
        btnEditar.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        btnEditar.setPrefWidth(140);
        btnEditar.setPrefHeight(38);
        btnEditar.setStyle("-fx-background-color:#E8F5E9;-fx-text-fill:#2E7D32;-fx-background-radius:10;-fx-cursor:hand;-fx-border-color:#A5D6A7;");

        HBox buttons = new HBox(10, btnEditar, btnExcluir);
        buttons.setPadding(new Insets(0, 0, 10, 0));

        // Lista de vendas (esquerda)
        listaVendas = new ListView<>();
        listaVendas.setPrefWidth(350);
        listaVendas.setStyle("-fx-font-size: 18px; -fx-background-color:#FAFFF5;-fx-border-color:#A5D6A7;-fx-border-radius:8;-fx-background-radius:8;");
        listaVendas.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Venda venda, boolean empty) {
                super.updateItem(venda, empty);
                if (empty || venda == null) {
                    setText(null);
                } else {
                    setText(venda.toString());
                }
            }
        });

        VBox listaBox = new VBox(10, buttons, listaVendas);
        listaBox.setPrefWidth(360);

        // Detalhes da venda (direita)
        txtDetalhes = new TextArea("Selecione uma venda para ver os detalhes.");
        txtDetalhes.setEditable(false);
        txtDetalhes.setWrapText(true);
        txtDetalhes.setFont(Font.font("Courier New", 18));
        txtDetalhes.setStyle("-fx-control-inner-background:#FAFFF5;-fx-border-color:#A5D6A7;-fx-border-radius:8;");

        HBox centro = new HBox(15, listaBox, txtDetalhes);
        HBox.setHgrow(txtDetalhes, Priority.ALWAYS);
        centro.setPadding(new Insets(15, 20, 15, 20));
        root.setCenter(centro);
    }

    /**
     * Popula a lista com as vendas.
     */
    public void carregarVendas(List<Venda> vendas) {
        listaVendas.getItems().setAll(vendas);

        listaVendas.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                txtDetalhes.setText("Selecione uma venda para ver os detalhes.");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Venda #").append(selected.getId()).append("\n");
            sb.append("Data: ").append(selected.getDataFormatada()).append("\n\n");
            sb.append("Itens:\n");
            if (selected.getItens().isEmpty()) {
                sb.append("  - Nenhum item registrado\n");
            } else {
                selected.getItens().forEach(item -> sb.append("  - ").append(item.toString()).append("\n"));
            }
            sb.append("\nTotal: R$ ").append(String.format("%.2f", selected.getTotalVenda()));
            sb.append("\nPago:  R$ ").append(String.format("%.2f", selected.getValorPago()));
            sb.append("\nTroco: R$ ").append(String.format("%.2f", selected.getTroco()));
            txtDetalhes.setText(sb.toString());
        });
    }

    public BorderPane getRoot() { return root; }
    public Button getBtnVoltar() { return btnVoltar; }
    public Button getBtnExcluir() { return btnExcluir; }
    public Button getBtnEditar() { return btnEditar; }
    public ListView<Venda> getListaVendas() { return listaVendas; }
}
