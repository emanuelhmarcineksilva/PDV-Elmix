package pdv.demo4.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pdv.demo4.model.Configuracao;

/**
 * Tela de informações da conta/loja.
 * Mostra nome, endereço e link.
 */
public class TelaContaView {

    private BorderPane root;
    private Button btnVoltar;
    private Configuracao config;

    public TelaContaView(Configuracao config) {
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
        btnVoltar.setStyle("-fx-background-color:#FFF;-fx-text-fill:#2E7D32;-fx-background-radius:12;-fx-border-radius:12;-fx-cursor:hand;-fx-border-color:#A5D6A7;");
        Label titulo = new Label("Conta da Loja");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titulo.setStyle("-fx-text-fill: #1B5E20;");
        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);
        HBox header = new HBox(15, btnVoltar, esp, titulo);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -5%);");
        root.setTop(header);

        // Centro - informações
        Label lblNome = criarLabel("Nome da Loja:", true);
        Label lblNomeValor = criarLabel(config.getNomeLoja(), false);
        Label lblEndereco = criarLabel("Endereço:", true);
        Label lblEnderecoValor = criarLabel("R. Sebastião Stancki da Luz Júnior, 70\nJardim Amélia, Pinhais - PR, 83330-360", false);

        Hyperlink linkMaps = new Hyperlink("Ver no Google Maps");
        linkMaps.setFont(Font.font("Arial", FontWeight.NORMAL, 22));
        linkMaps.setStyle("-fx-text-fill: #2E7D32;");
        linkMaps.setOnAction(e -> {
            try { java.awt.Desktop.getDesktop().browse(new java.net.URI("https://www.google.com/maps/place/Elmix")); }
            catch (Exception ex) { /* ignora */ }
        });

        Label lblSite = criarLabel("Site:", true);
        String siteUrl = config.getLinkSite().isEmpty() ? "Ainda não configurado" : config.getLinkSite();
        Label lblSiteValor = criarLabel(siteUrl, false);

        VBox centro = new VBox(12, lblNome, lblNomeValor, lblEndereco, lblEnderecoValor, linkMaps, lblSite, lblSiteValor);
        centro.setAlignment(Pos.TOP_LEFT);
        centro.setPadding(new Insets(30, 40, 30, 40));
        root.setCenter(centro);
    }

    private Label criarLabel(String texto, boolean negrito) {
        Label l = new Label(texto);
        l.setFont(Font.font("Arial", negrito ? FontWeight.BOLD : FontWeight.NORMAL, 22));
        l.setStyle("-fx-text-fill: #000;");
        l.setWrapText(true);
        return l;
    }

    public BorderPane getRoot() { return root; }
    public Button getBtnVoltar() { return btnVoltar; }
}
