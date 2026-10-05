package pdv.demo4.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pdv.demo4.model.Configuracao;

public class TelaMoneyView {

    private BorderPane root;
    private Configuracao config;

    private Button btnVoltar;
    private Button btnBaixarTxt;
    private TextArea txtResumo;

    private BarChart<String, Number> chartSemanal;
    private BarChart<String, Number> chartMensal;

    public TelaMoneyView(Configuracao config) {
        this.config = config;
        construirTela();
    }

    private void construirTela() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + config.getCorPrincipal() + ";");
        root.setTop(criarHeader());

        // HBox principal contendo o painel esquerdo (texto) e direito (gráficos)
        HBox centro = new HBox(30);
        centro.setPadding(new Insets(20));
        HBox.setHgrow(centro, Priority.ALWAYS);

        // Painel Esquerdo (Resumo)
        VBox painelEsquerdo = criarPainelEsquerdo();
        HBox.setHgrow(painelEsquerdo, Priority.ALWAYS);

        // Painel Direito (Gráficos)
        VBox painelDireito = criarPainelDireito();
        HBox.setHgrow(painelDireito, Priority.ALWAYS);

        centro.getChildren().addAll(painelEsquerdo, painelDireito);
        root.setCenter(centro);
    }

    private HBox criarHeader() {
        Label lblTitulo = new Label("Painel Financeiro - Money");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTitulo.setStyle("-fx-text-fill: #1B5E20;");

        btnVoltar = criarBotao("Voltar");
        btnVoltar.setPrefWidth(120);
        btnVoltar.setPrefHeight(40);

        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        HBox h = new HBox(15, btnVoltar, esp, lblTitulo);
        h.setAlignment(Pos.CENTER);
        h.setPadding(new Insets(15, 30, 15, 30));
        h.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -5%);");
        return h;
    }

    private VBox criarPainelEsquerdo() {
        Label lblSub = new Label("Resumo do Faturamento");
        lblSub.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblSub.setStyle("-fx-text-fill: #2E7D32;");

        txtResumo = new TextArea();
        txtResumo.setEditable(false);
        txtResumo.setFont(Font.font("Monospaced", 16));
        txtResumo.setStyle("-fx-control-inner-background: #FAFFF5; -fx-text-fill: #1B5E20; -fx-border-color: #A5D6A7; -fx-border-radius: 10; -fx-background-radius: 10;");
        VBox.setVgrow(txtResumo, Priority.ALWAYS);

        btnBaixarTxt = criarBotaoVerde("Baixar Resumo (TXT)");
        btnBaixarTxt.setPrefWidth(Double.MAX_VALUE);
        btnBaixarTxt.setPrefHeight(50);
        btnBaixarTxt.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox v = new VBox(15, lblSub, txtResumo, btnBaixarTxt);
        v.setMinWidth(400);
        v.setPrefWidth(450);
        v.setPadding(new Insets(15));
        v.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -2%); -fx-padding: 20; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #81C784; -fx-border-width: 2;");
        return v;
    }

    private VBox criarPainelDireito() {
        // Gráfico Semanal
        CategoryAxis xAxisSemanal = new CategoryAxis();
        NumberAxis yAxisSemanal = new NumberAxis();
        xAxisSemanal.setLabel("Dias da Semana");
        yAxisSemanal.setLabel("Valor Faturado R$");
        chartSemanal = new BarChart<>(xAxisSemanal, yAxisSemanal);
        chartSemanal.setTitle("Faturamento da Semana");
        chartSemanal.setLegendVisible(false);
        chartSemanal.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(chartSemanal, Priority.ALWAYS);

        // Gráfico Mensal
        CategoryAxis xAxisMensal = new CategoryAxis();
        NumberAxis yAxisMensal = new NumberAxis();
        xAxisMensal.setLabel("Período do Mês");
        yAxisMensal.setLabel("Valor Faturado R$");
        chartMensal = new BarChart<>(xAxisMensal, yAxisMensal);
        chartMensal.setTitle("Faturamento Mensal (Por Semanas)");
        chartMensal.setLegendVisible(false);
        chartMensal.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(chartMensal, Priority.ALWAYS);

        VBox v = new VBox(20, chartSemanal, chartMensal);
        v.setPadding(new Insets(15));
        v.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -2%); -fx-padding: 20; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: #81C784; -fx-border-width: 2;");
        return v;
    }

    public void popularDados(double[] semana, double[] mes, String resumo) {
        txtResumo.setText(resumo);

        // Limpa dados antigos
        chartSemanal.getData().clear();
        chartMensal.getData().clear();

        // Dados Semanal
        XYChart.Series<String, Number> seriesSemanal = new XYChart.Series<>();
        String[] dias = {"Seg", "Ter", "Qua", "Qui", "Sex", "Sab", "Dom"};
        for (int i = 0; i < 7; i++) {
            seriesSemanal.getData().add(new XYChart.Data<>(dias[i], semana[i]));
        }
        chartSemanal.getData().add(seriesSemanal);

        // Dados Mensal
        XYChart.Series<String, Number> seriesMensal = new XYChart.Series<>();
        String[] semanas = {"Semana 1", "Semana 2", "Semana 3", "Semana 4"};
        for (int i = 0; i < 4; i++) {
            seriesMensal.getData().add(new XYChart.Data<>(semanas[i], mes[i]));
        }
        chartMensal.getData().add(seriesMensal);
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

    public BorderPane getRoot() { return root; }
    public Button getBtnVoltar() { return btnVoltar; }
    public Button getBtnBaixarTxt() { return btnBaixarTxt; }
    public TextArea getTxtResumo() { return txtResumo; }
}
