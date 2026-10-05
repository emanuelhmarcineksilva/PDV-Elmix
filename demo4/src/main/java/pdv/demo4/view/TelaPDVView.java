







































































package pdv.demo4.view;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import pdv.demo4.model.Configuracao;
import pdv.demo4.model.ItemVenda;

import java.io.File;
import java.util.function.Consumer;

public class TelaPDVView {

    private StackPane stackRoot;
    private BorderPane root;
    private Configuracao config;
    private TextField txtProduto, txtValor, txtValorPago;
    private ListView<ItemVenda> listaItens;
    private ObservableList<ItemVenda> itensObservavel;
    private Consumer<Integer> onItemRemoveHandler;
    private Label lblTotal, lblTroco, lblStatus, lblVendaId;
    private Button btnAdicionarItem, btnNovaVenda, btnFinalizarVenda, btnVoltar;
    private final boolean modoCompacto;
    private final boolean modoModal;
    private final boolean exibirCabecalho;

    public TelaPDVView(Configuracao config) {
        this(config, false, false, true);
    }

    public TelaPDVView(Configuracao config, boolean modoCompacto) {
        this(config, modoCompacto, false, true);
    }

    public TelaPDVView(Configuracao config, boolean modoCompacto, boolean modoModal) {
        this(config, modoCompacto, modoModal, true);
    }

    public TelaPDVView(Configuracao config, boolean modoCompacto, boolean modoModal, boolean exibirCabecalho) {
        this.config = config;
        this.modoCompacto = modoCompacto;
        this.modoModal = modoModal;
        this.exibirCabecalho = exibirCabecalho;
        construirTela();
    }

    private void construirTela() {
        stackRoot = new StackPane();
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + config.getCorPrincipal() + ";");
        if (exibirCabecalho) {
            root.setTop(criarHeader());
        }

        HBox centro = new HBox(modoModal ? 14 : (modoCompacto ? 8 : 30));
        centro.setPadding(new Insets(modoModal ? 14 : (modoCompacto ? 8 : 20)));
        
        VBox esquerdo = criarLadoEsquerdo();
        // Em modo compacto (paralelo): permitir divisão 50/50 real
        if (modoModal) {
            esquerdo.setMinWidth(0);
            esquerdo.setPrefWidth(0);
            esquerdo.setMaxWidth(Double.MAX_VALUE);
        } else if (modoCompacto) {
            esquerdo.setMinWidth(0);
            esquerdo.setPrefWidth(0);
            esquerdo.setMaxWidth(Double.MAX_VALUE);
        } else {
            esquerdo.setMinWidth(600);
        }
        HBox.setHgrow(esquerdo, Priority.ALWAYS);

        VBox direito = criarLadoDireito();
        // Em modo compacto (paralelo): permitir divisão 50/50 real
        if (modoModal) {
            direito.setMinWidth(0);
            direito.setPrefWidth(300);
            direito.setMaxWidth(340);
        } else if (modoCompacto) {
            direito.setMinWidth(0);
            direito.setPrefWidth(0);
            direito.setMaxWidth(Double.MAX_VALUE);
        } else {
            direito.setMinWidth(450);
            direito.setPrefWidth(450);
            direito.setMaxWidth(450);
        }
        HBox.setHgrow(direito, Priority.ALWAYS);

        centro.getChildren().addAll(esquerdo, direito);
        root.setCenter(centro);

        stackRoot.getChildren().add(root);

        // As imagens decorativas têm dimensões fixas e não devem influenciar
        // a largura mínima de cada caixa no modo dividido.
        if (!modoCompacto && !modoModal) {
            try {
                ImageView imgTopo = new ImageView(new Image(new File("hanging-vines-creepers-leaves-forest-shrubs-ai-generated.png").toURI().toString()));
                imgTopo.setPreserveRatio(true);
                imgTopo.setFitWidth(1200);
                imgTopo.setMouseTransparent(true);
                StackPane.setAlignment(imgTopo, Pos.TOP_CENTER);
                stackRoot.getChildren().add(imgTopo);

                ImageView imgCanto = new ImageView(new Image(new File("green-ivy-vine-isolated-on-a-transparent-background-showcasing-lush-leaves-forest-shrubs-ai-generated.png").toURI().toString()));
                imgCanto.setPreserveRatio(true);
                imgCanto.setFitHeight(500);
                imgCanto.setMouseTransparent(true);
                StackPane.setAlignment(imgCanto, Pos.BOTTOM_LEFT);
                stackRoot.getChildren().add(imgCanto);
            } catch (Exception e) {
                System.err.println("Imagens de folhas não encontradas: " + e.getMessage());
            }
        }
    }

    private HBox criarHeader() {
        lblVendaId = new Label("PDV - Caixa Aberto");
        lblVendaId.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblVendaId.setStyle("-fx-text-fill: #1B5E20;");
        
        btnVoltar = criarBotao("Voltar");
        btnVoltar.setPrefWidth(120);
        btnVoltar.setPrefHeight(40);
        
        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);
        
        HBox h = new HBox(15, btnVoltar, esp, lblVendaId);
        h.setAlignment(Pos.CENTER);
        h.setPadding(new Insets(15, 30, 15, 30));
        h.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -5%);");
        return h;
    }

    private VBox criarLadoEsquerdo() {
        itensObservavel = FXCollections.observableArrayList();
        listaItens = new ListView<>(itensObservavel);
        listaItens.setStyle("-fx-font-size: 18px; -fx-background-color: #FAFFF5; -fx-border-color: #A5D6A7; -fx-border-radius: 10; -fx-background-radius: 10;");
        
        listaItens.setCellFactory(param -> new ListCell<ItemVenda>() {
            private final Button btnRemove = new Button("x");
            private final Label lblText = new Label();
            private final HBox box = new HBox(10);
            private final ChangeListener<Boolean> selectionListener = (obs, wasSelected, isSelected) -> {
                btnRemove.setVisible(isSelected);
            };

            {
                // Botão começa invisível - só aparece quando linha está selecionada
                btnRemove.setVisible(false);
                btnRemove.setStyle(
                    "-fx-background-color: #E53935; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 12px; " +
                    "-fx-background-radius: 50%; " +
                    "-fx-min-width: 24px; -fx-min-height: 24px; " +
                    "-fx-max-width: 24px; -fx-max-height: 24px; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 0; " +
                    "-fx-alignment: center;"
                );
                btnRemove.setOnMouseEntered(e -> btnRemove.setStyle(
                    "-fx-background-color: #B71C1C; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 12px; " +
                    "-fx-background-radius: 50%; " +
                    "-fx-min-width: 24px; -fx-min-height: 24px; " +
                    "-fx-max-width: 24px; -fx-max-height: 24px; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 0; " +
                    "-fx-alignment: center;"
                ));
                btnRemove.setOnMouseExited(e -> btnRemove.setStyle(
                    "-fx-background-color: #E53935; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-font-size: 12px; " +
                    "-fx-background-radius: 50%; " +
                    "-fx-min-width: 24px; -fx-min-height: 24px; " +
                    "-fx-max-width: 24px; -fx-max-height: 24px; " +
                    "-fx-cursor: hand; " +
                    "-fx-padding: 0; " +
                    "-fx-alignment: center;"
                ));

                lblText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
                lblText.setStyle("-fx-text-fill: #1B5E20;");

                // lblText expande para empurrar botão para a direita
                HBox.setHgrow(lblText, Priority.ALWAYS);
                box.setAlignment(Pos.CENTER_LEFT);
                box.getChildren().addAll(lblText, btnRemove);

                btnRemove.setOnAction(e -> {
                    int idx = getIndex();
                    if (idx >= 0 && idx < getListView().getItems().size() && onItemRemoveHandler != null) {
                        onItemRemoveHandler.accept(idx);
                    }
                });
            }

            @Override
            protected void updateItem(ItemVenda item, boolean empty) {
                super.updateItem(item, empty);
                // Limpa listener anterior para evitar vazamentos
                selectedProperty().removeListener(selectionListener);
                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    btnRemove.setVisible(false);
                } else {
                    lblText.setText(String.format("%03d - %s | R$ %.2f", getIndex() + 1, item.getNomeProduto(), item.getValor()));
                    setGraphic(box);
                    setText(null);
                    // Sincroniza visibilidade do botão com seleção da linha
                    btnRemove.setVisible(isSelected());
                    selectedProperty().addListener(selectionListener);
                }
            }
        });

        VBox.setVgrow(listaItens, Priority.ALWAYS);
        
        lblStatus = new Label("");
        lblStatus.setFont(Font.font("Arial", 16));
        lblStatus.setStyle("-fx-text-fill: #555;");

        // Botão "Venda em paralelo (/)" - conforme tópico 9 e 10
        btnNovaVenda = criarBotaoVerde("Venda em paralelo (/)");
        btnNovaVenda.setMaxWidth(Double.MAX_VALUE);
        btnNovaVenda.setPrefWidth(250);
        btnNovaVenda.setPrefHeight(50);
        btnNovaVenda.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        btnFinalizarVenda = criarBotaoVerde("Finalizar Venda (F4 / -)");
        btnFinalizarVenda.setMaxWidth(Double.MAX_VALUE);
        btnFinalizarVenda.setPrefWidth(250);
        btnFinalizarVenda.setPrefHeight(60);
        btnFinalizarVenda.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        HBox botoesEsquerda = new HBox(15, btnNovaVenda, btnFinalizarVenda);
        HBox.setHgrow(btnNovaVenda, Priority.ALWAYS);
        HBox.setHgrow(btnFinalizarVenda, Priority.ALWAYS);

        VBox v = new VBox(10, listaItens, lblStatus, botoesEsquerda);
        return v;
    }

    private VBox criarLadoDireito() {
        Label lp = new Label("Produto:");
        lp.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        
        txtProduto = new TextField();
        txtProduto.setPromptText("Diversos");
        txtProduto.setFont(Font.font("Arial", 28));
        estilizarCampo(txtProduto);

        Label lv = new Label("Valor Unitário R$:");
        lv.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        
        txtValor = new TextField();
        txtValor.setPromptText("0,00");
        txtValor.setFont(Font.font("Arial", 28));
        estilizarCampo(txtValor);

        // Filtro: só permite dígitos, ponto e vírgula
        txtValor.addEventFilter(KeyEvent.KEY_TYPED, ev -> {
            String c = ev.getCharacter();
            if (!c.matches("[0-9.,]")) {
                ev.consume();
            }
        });

        // Intercepta * no campo de valor e redireciona foco para Valor Pago
        // (precisa de addEventFilter para capturar antes do campo consumir o evento)
        txtValor.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.MULTIPLY ||
                (ev.getText() != null && ev.getText().equals("*"))) {
                ev.consume();
                txtValorPago.requestFocus();
            }
        });

        btnAdicionarItem = criarBotaoVerde("Adicionar Item");
        btnAdicionarItem.setPrefWidth(Double.MAX_VALUE);
        btnAdicionarItem.setPrefHeight(60);
        btnAdicionarItem.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        Region esp = new Region();
        esp.setPrefHeight(30);

        lblTotal = new Label("Total: R$ 0,00");
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        lblTotal.setStyle("-fx-text-fill: #1B5E20;");

        Label lvp = new Label("Valor Pago R$:");
        lvp.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        
        txtValorPago = new TextField();
        txtValorPago.setPromptText("0,00");
        txtValorPago.setFont(Font.font("Arial", 28));
        estilizarCampo(txtValorPago);

        // Bloqueia o '*' que chega no campo após redirecionamento de foco do txtValor
        // (o KEY_TYPED dispara no campo que recebe o foco, não no original)
        txtValorPago.addEventFilter(KeyEvent.KEY_TYPED, ev -> {
            if ("*".equals(ev.getCharacter())) {
                ev.consume();
            }
        });

        lblTroco = new Label("Troco: R$ 0,00");
        lblTroco.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        lblTroco.setStyle("-fx-text-fill: #2E7D32;");

        VBox v = new VBox(15, lp, txtProduto, lv, txtValor, btnAdicionarItem, esp, lblTotal, lvp, txtValorPago, lblTroco);
        v.setAlignment(Pos.TOP_LEFT);
        v.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -2%); -fx-padding: 20; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #81C784; -fx-border-width: 2;");
        return v;
    }

    private Button criarBotao(String texto) {
        Button btn = new Button(texto);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        String raio = config.isBordaArredondada() ? String.valueOf(config.getRaioBorada()) : "0";
        String n = "-fx-background-color:" + config.getCorBotoes() + ";-fx-text-fill:" + config.getCorTextoBotoes()
                + ";-fx-background-radius:" + raio + ";-fx-border-radius:" + raio
                + ";-fx-cursor:hand;-fx-border-color:#A5D6A7;-fx-border-width:1;";
        String h = "-fx-background-color:derive(" + config.getCorBotoes() + ",-10%);-fx-text-fill:" + config.getCorTextoBotoes()
                + ";-fx-background-radius:" + raio + ";-fx-border-radius:" + raio
                + ";-fx-cursor:hand;-fx-border-color:#81C784;-fx-border-width:1.5;";
        btn.setStyle(n);
        btn.setOnMouseEntered(e -> btn.setStyle(h));
        btn.setOnMouseExited(e -> btn.setStyle(n));
        return btn;
    }

    private Button criarBotaoVerde(String texto) {
        Button btn = new Button(texto);
        String raio = config.isBordaArredondada() ? String.valueOf(config.getRaioBorada()) : "0";
        String n = "-fx-background-color:#2E7D32;-fx-text-fill:#FFF;-fx-background-radius:" + raio + ";-fx-border-radius:" + raio + ";-fx-cursor:hand;";
        String h = "-fx-background-color:#1B5E20;-fx-text-fill:#FFF;-fx-background-radius:" + raio + ";-fx-border-radius:" + raio + ";-fx-cursor:hand;";
        btn.setStyle(n);
        btn.setOnMouseEntered(e -> btn.setStyle(h));
        btn.setOnMouseExited(e -> btn.setStyle(n));
        return btn;
    }

    private void estilizarCampo(TextField campo) {
        String n = "-fx-background-color:#FAFFF5;-fx-border-color:#A5D6A7;-fx-border-radius:10;-fx-background-radius:10;-fx-padding:10;-fx-text-fill:#000;";
        String f = "-fx-background-color:#FAFFF5;-fx-border-color:#43A047;-fx-border-radius:10;-fx-background-radius:10;-fx-border-width:2;-fx-padding:9;-fx-text-fill:#000;";
        campo.setStyle(n);
        campo.focusedProperty().addListener((obs, old, nv) -> campo.setStyle(nv ? f : n));
    }

    public void setOnItemRemoveHandler(Consumer<Integer> handler) {
        this.onItemRemoveHandler = handler;
    }

    public void atualizarListaItens(java.util.List<ItemVenda> itens) {
        itensObservavel.setAll(itens);
    }

    public void atualizarTotal(double total) { 
        lblTotal.setText("Total: R$ " + String.format("%.2f", total)); 
    }

    public void atualizarTroco(double troco) {
        lblTroco.setText("Troco: R$ " + String.format("%.2f", troco));
        lblTroco.setStyle(troco < 0 ? "-fx-text-fill:#C62828;" : "-fx-text-fill:#2E7D32;");
    }

    public void setStatus(String msg) { lblStatus.setText(msg); }

    public void limparTudo() { 
        txtProduto.clear(); 
        txtValor.clear(); 
        txtValorPago.clear(); 
        itensObservavel.clear(); 
        atualizarTotal(0); 
        atualizarTroco(0); 
        setStatus(""); 
    }

    public void limparCamposEntrada() { 
        txtValor.clear(); 
        txtProduto.requestFocus(); 
    }

    public StackPane getRoot() { return stackRoot; }
    public TextField getTxtProduto() { return txtProduto; }
    public TextField getTxtValor() { return txtValor; }
    public TextField getTxtValorPago() { return txtValorPago; }
    public ListView<ItemVenda> getListaItens() { return listaItens; }
    public Button getBtnAdicionarItem() { return btnAdicionarItem; }
    public Button getBtnNovaVenda() { return btnNovaVenda; }
    public Button getBtnFinalizarVenda() { return btnFinalizarVenda; }
    public Button getBtnVoltar() { return btnVoltar; }
    public Label getLblVendaId() { return lblVendaId; }
    public Label getLblTotal() { return lblTotal; }
    public Label getLblTroco() { return lblTroco; }
    public Label getLblStatus() { return lblStatus; }
}
