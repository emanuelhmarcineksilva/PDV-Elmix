package pdv.demo4.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pdv.demo4.model.Configuracao;

/**
 * Tela de configurações do sistema.
 * Permite mudar cores, nome da loja, bordas e animações.
 */
public class TelaConfigView {

    private BorderPane root;
    private Configuracao config;
    private Button btnVoltar, btnSalvar, btnRestaurar;
    private TextField txtNomeLoja, txtCorPrincipal, txtCorBotoes, txtCorTextoBotoes, txtLinkSite, txtRaioBorda;
    private CheckBox chkBordaArredondada, chkAnimacoes;

    public TelaConfigView(Configuracao config) {
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
        Label titulo = new Label("Configurações");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titulo.setStyle("-fx-text-fill: #1B5E20;");
        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);
        HBox header = new HBox(15, btnVoltar, esp, titulo);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15, 20, 15, 20));
        root.setTop(header);

        // Centro - campos de configuração
        txtNomeLoja = criarCampo("Nome da Loja", config.getNomeLoja());
        txtCorPrincipal = criarCampo("Cor Principal (hex)", config.getCorPrincipal());
        txtCorBotoes = criarCampo("Cor dos Botões (hex)", config.getCorBotoes());
        txtCorTextoBotoes = criarCampo("Cor Texto Botões (hex)", config.getCorTextoBotoes());
        txtLinkSite = criarCampo("Link do Site", config.getLinkSite());
        txtRaioBorda = criarCampo("Raio da Borda", String.valueOf(config.getRaioBorada()));

        chkBordaArredondada = new CheckBox("Borda Arredondada");
        chkBordaArredondada.setFont(Font.font("Arial", 18));
        chkBordaArredondada.setSelected(config.isBordaArredondada());
        chkBordaArredondada.setStyle("-fx-text-fill: #000;");

        chkAnimacoes = new CheckBox("Animações Ativas");
        chkAnimacoes.setFont(Font.font("Arial", 18));
        chkAnimacoes.setSelected(config.isAnimacoesAtivas());
        chkAnimacoes.setStyle("-fx-text-fill: #000;");

        btnSalvar = criarBotaoVerde("Salvar");
        btnRestaurar = criarBotaoVerde("Restaurar Padrão");

        HBox linhaBotoes = new HBox(15, btnSalvar, btnRestaurar);
        linhaBotoes.setAlignment(Pos.CENTER);
        linhaBotoes.setPadding(new Insets(15, 0, 0, 0));

        VBox centro = new VBox(10,
            criarLinha("Nome da Loja:", txtNomeLoja),
            criarLinha("Cor Principal:", txtCorPrincipal),
            criarLinha("Cor Botões:", txtCorBotoes),
            criarLinha("Cor Texto Botões:", txtCorTextoBotoes),
            criarLinha("Link Site:", txtLinkSite),
            criarLinha("Raio Borda:", txtRaioBorda),
            chkBordaArredondada, chkAnimacoes,
            linhaBotoes
        );
        centro.setPadding(new Insets(25, 40, 25, 40));

        ScrollPane scroll = new ScrollPane(centro);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + config.getCorPrincipal() + ";");
        root.setCenter(scroll);
    }

    private TextField criarCampo(String prompt, String valor) {
        TextField t = new TextField(valor);
        t.setPromptText(prompt);
        t.setFont(Font.font("Arial", 18));
        t.setPrefWidth(350);
        t.setStyle("-fx-background-color:#FAFFF5;-fx-border-color:#A5D6A7;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:6;");
        return t;
    }

    private HBox criarLinha(String label, TextField campo) {
        Label l = new Label(label);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        l.setStyle("-fx-text-fill: #000;");
        l.setPrefWidth(220);
        HBox h = new HBox(10, l, campo);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    private Button criarBotaoVerde(String texto) {
        Button btn = new Button(texto);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btn.setPrefHeight(50);
        btn.setPrefWidth(200);
        String n = "-fx-background-color:#2E7D32;-fx-text-fill:#FFF;-fx-background-radius:12;-fx-cursor:hand;";
        String h = "-fx-background-color:#1B5E20;-fx-text-fill:#FFF;-fx-background-radius:12;-fx-cursor:hand;";
        btn.setStyle(n);
        btn.setOnMouseEntered(e -> btn.setStyle(h));
        btn.setOnMouseExited(e -> btn.setStyle(n));
        return btn;
    }

    // --- Getters ---
    public BorderPane getRoot() { return root; }
    public Button getBtnVoltar() { return btnVoltar; }
    public Button getBtnSalvar() { return btnSalvar; }
    public Button getBtnRestaurar() { return btnRestaurar; }
    public TextField getTxtNomeLoja() { return txtNomeLoja; }
    public TextField getTxtCorPrincipal() { return txtCorPrincipal; }
    public TextField getTxtCorBotoes() { return txtCorBotoes; }
    public TextField getTxtCorTextoBotoes() { return txtCorTextoBotoes; }
    public TextField getTxtLinkSite() { return txtLinkSite; }
    public TextField getTxtRaioBorda() { return txtRaioBorda; }
    public CheckBox getChkBordaArredondada() { return chkBordaArredondada; }
    public CheckBox getChkAnimacoes() { return chkAnimacoes; }
}
