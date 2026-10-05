package pdv.demo4.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pdv.demo4.model.Configuracao;
import java.io.File;

/**
 * Tela inicial do sistema PDV Elmix.
 * Layout: Header (ELMIX), barras laterais com botões, centro com passagem bíblica.
 */
public class TelaInicialView {

    private BorderPane root;          // Layout principal
    private Label lblPassagem;        // Texto da passagem bíblica no centro
    private Configuracao config;      // Configurações do sistema

    // Botões da barra direita
    private Button btnIniciarVenda;
    private Button btnHistorico;
    private Button btnFecharCaixa;

    // Botões da barra esquerda
    private Button btnConta;
    private Button btnMoney;
    private Button btnGestao;
    private Button btnEmpresas;
    private Button btnSalvarBackup;
    private Button btnConfiguracoes;

    public TelaInicialView(Configuracao config) {
        this.config = config;
        construirTela();
    }

    /**
     * Monta toda a tela inicial.
     */
    private void construirTela() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: linear-gradient(to bottom, derive("
                + config.getCorPrincipal() + ", 5%), derive("
                + config.getCorPrincipal() + ", -10%));");

        // === HEADER ===
        StackPane header = criarHeader();
        root.setTop(header);

        // === CENTRO - Passagem bíblica ===
        VBox centro = criarCentro();
        root.setCenter(centro);

        // === BARRA ESQUERDA ===
        VBox barraEsquerda = criarBarraEsquerda();
        root.setLeft(barraEsquerda);

        // === BARRA DIREITA ===
        VBox barraDireita = criarBarraDireita();
        root.setRight(barraDireita);

        // === FOOTER ===
        VBox footer = criarFooter();
        root.setBottom(footer);
    }

    /**
     * Cria o header com o nome "ELMIX" e imagem de folhas atrás.
     */
    private StackPane criarHeader() {
        Label lblTitulo = new Label(config.getNomeLoja());
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        lblTitulo.setStyle("-fx-text-fill: #C62828;"); // Vermelho suave

        HBox headerBox = new HBox(lblTitulo);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20, 0, 10, 0));

        StackPane headerStack = new StackPane();
        headerStack.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -5%);");

        try {
            ImageView imgTopo = new ImageView(new Image(new File("hanging-vines-creepers-leaves-forest-shrubs-ai-generated.png").toURI().toString()));
            imgTopo.setPreserveRatio(true);
            imgTopo.setFitWidth(1000);
            StackPane.setAlignment(imgTopo, Pos.TOP_CENTER);
            imgTopo.setOpacity(0.8);
            headerStack.getChildren().add(imgTopo);
        } catch (Exception e) {
            System.err.println("Imagem folha topo não encontrada.");
        }

        headerStack.getChildren().add(headerBox);
        return headerStack;
    }

    /**
     * Cria o centro com a passagem bíblica.
     */
    private VBox criarCentro() {
        lblPassagem = new Label(
                "\"Amarás ao Senhor teu Deus de todo o teu coração, " +
                "e de toda a tua alma, e de todo o teu pensamento. " +
                "Este é o primeiro e grande mandamento. " +
                "E o segundo, semelhante a este, é: " +
                "Amarás o teu próximo como a ti mesmo.\"\n\n" +
                "— Mateus 22:37-39"
        );
        lblPassagem.setFont(Font.font("Georgia", FontWeight.NORMAL, 26));
        lblPassagem.setStyle("-fx-text-fill: #1B5E20;"
                + "-fx-line-spacing: 6px;");
        lblPassagem.setTextAlignment(TextAlignment.CENTER);
        lblPassagem.setWrapText(true);
        lblPassagem.setMaxWidth(800);

        VBox card = new VBox(20);
        card.setAlignment(Pos.CENTER);
        
        try {
            ImageView cardDeco = new ImageView(new Image(new File("green-ivy-vine-isolated-on-a-transparent-background-showcasing-lush-leaves-and-natural-beauty-perfect-for-botanical-and-nature-themed-projects-png.png").toURI().toString()));
            cardDeco.setPreserveRatio(true);
            cardDeco.setFitHeight(70);
            card.getChildren().add(cardDeco);
        } catch (Exception e) {
            // Caso falhe, apenas ignora o ícone decorativo
        }
        
        card.getChildren().add(lblPassagem);
        card.setPadding(new Insets(45, 60, 45, 60));
        card.setStyle("-fx-background-color: rgba(255, 255, 255, 0.7);"
                + "-fx-background-radius: 25;"
                + "-fx-border-color: rgba(129, 199, 132, 0.3);"
                + "-fx-border-radius: 25;"
                + "-fx-border-width: 1.5;"
                + "-fx-effect: dropshadow(gaussian, rgba(27,94,32,0.1), 15, 0, 0, 4);");

        VBox centro = new VBox(card);
        centro.setAlignment(Pos.CENTER);
        centro.setPadding(new Insets(40));

        return centro;
    }

    /**
     * Cria a barra lateral esquerda com botões: Conta, Salvar Backup, Configurações.
     */
    private VBox criarBarraEsquerda() {
        btnConta = criarBotao("Conta");
        btnMoney = criarBotao("Money");
        btnGestao = criarBotao("Gestão");
        btnEmpresas = criarBotao("Empresas");
        btnSalvarBackup = criarBotao("Salvar Backup");
        btnConfiguracoes = criarBotao("Configurações");

        VBox barra = new VBox(10, btnConta, btnMoney, btnGestao, btnEmpresas, btnSalvarBackup, btnConfiguracoes);
        barra.setAlignment(Pos.TOP_CENTER);
        barra.setPadding(new Insets(30, 15, 30, 15));
        barra.setPrefWidth(220);
        barra.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -10%);");

        return barra;
    }

    public Button getBtnEmpresas() { return btnEmpresas; }

    /**
     * Cria a barra lateral direita com botões: Iniciar Venda, Histórico, Fechar Caixa.
     */
    private VBox criarBarraDireita() {
        btnIniciarVenda = criarBotao("Iniciar Venda");
        btnHistorico = criarBotao("Histórico");
        btnFecharCaixa = criarBotao("Fechar Caixa");

        // Espaçador para colocar os botões do meio para baixo
        Region espacador = new Region();
        VBox.setVgrow(espacador, Priority.ALWAYS);

        VBox barra = new VBox(10, espacador, btnIniciarVenda, btnHistorico, btnFecharCaixa);
        barra.setAlignment(Pos.BOTTOM_CENTER);
        barra.setPadding(new Insets(30, 15, 30, 15));
        barra.setPrefWidth(220);
        barra.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -10%);");

        return barra;
    }

    /**
     * Cria o footer com créditos.
     */
    private VBox criarFooter() {
        Label lblCredito = new Label("Orquestrado por Emanuel Henrique");
        lblCredito.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        lblCredito.setStyle("-fx-text-fill: #1B5E20;");

        Hyperlink linkGithub = new Hyperlink("https://github.com/emanuelhmarcineksilva");
        linkGithub.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
        linkGithub.setStyle("-fx-text-fill: #2E7D32;");
        // Abre o link no navegador ao clicar
        linkGithub.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(linkGithub.getText()));
            } catch (Exception ex) {
                System.err.println("Não foi possível abrir o link: " + ex.getMessage());
            }
        });

        VBox footer = new VBox(2, lblCredito, linkGithub);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(10, 0, 15, 0));
        footer.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -5%);");

        return footer;
    }

    /**
     * Cria um botão estilizado padrão do sistema.
     */
    private Button criarBotao(String texto) {
        Button btn = new Button(texto);
        btn.setPrefWidth(200);
        btn.setPrefHeight(50);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        String raio = config.isBordaArredondada() ? String.valueOf(config.getRaioBorada()) : "0";
        String estiloNormal = "-fx-background-color: " + config.getCorBotoes() + ";"
                + "-fx-text-fill: " + config.getCorTextoBotoes() + ";"
                + "-fx-background-radius: " + raio + ";"
                + "-fx-border-radius: " + raio + ";"
                + "-fx-cursor: hand;"
                + "-fx-border-color: #A5D6A7;"
                + "-fx-border-width: 1;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 6, 0, 0, 3);";

        String estiloHover = "-fx-background-color: derive(" + config.getCorBotoes() + ", -10%);"
                + "-fx-text-fill: " + config.getCorTextoBotoes() + ";"
                + "-fx-background-radius: " + raio + ";"
                + "-fx-border-radius: " + raio + ";"
                + "-fx-cursor: hand;"
                + "-fx-border-color: #81C784;"
                + "-fx-border-width: 1.5;"
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 8, 0, 0, 4);";

        btn.setStyle(estiloNormal);

        // Efeito hover: botão escurece levemente
        btn.setOnMouseEntered(e -> btn.setStyle(estiloHover));
        btn.setOnMouseExited(e -> btn.setStyle(estiloNormal));

        return btn;
    }

    // --- Getters dos componentes para o controller conectar eventos ---

    public BorderPane getRoot() { return root; }
    public Button getBtnIniciarVenda() { return btnIniciarVenda; }
    public Button getBtnHistorico() { return btnHistorico; }
    public Button getBtnFecharCaixa() { return btnFecharCaixa; }
    public Button getBtnConta() { return btnConta; }
    public Button getBtnMoney() { return btnMoney; }
    public Button getBtnGestao() { return btnGestao; }
    public Button getBtnSalvarBackup() { return btnSalvarBackup; }
    public Button getBtnConfiguracoes() { return btnConfiguracoes; }
    public Label getLblPassagem() { return lblPassagem; }

    /**
     * Atualiza os estilos quando a configuração muda.
     */
    public void atualizarEstilos(Configuracao novaConfig) {
        this.config = novaConfig;
        construirTela();
    }
}
