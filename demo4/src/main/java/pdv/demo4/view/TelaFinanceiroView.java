package pdv.demo4.view;

import javafx.collections.FXCollections;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.StringConverter;
import pdv.demo4.model.Configuracao;
import pdv.demo4.model.Empresa;
import pdv.demo4.model.FaturaFinanceira;
import pdv.demo4.model.MovimentacaoCaixa;
import pdv.demo4.model.TipoMovimentacao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Tela de Gestão Financeira (MVP) - Fluxo de Caixa.
 * - Filtros: Hoje / 7 dias / Mês Atual
 * - Cards: Entradas / Saídas / Saldo
 * - Gráficos: Barras Entradas x Saídas e Pizza por Fornecedor
 * - Form lançamento manual de SAIDA com Select empresa + atalho +Cadastrar
 */
public class TelaFinanceiroView {

    private BorderPane root;
    private Configuracao config;

    // header
    private Button btnVoltar;

    // cards
    private Label lblEntradas, lblSaidas, lblSaldo;

    // filtros
    private ComboBox<String> comboPeriodo;
    private DatePicker dpPeriodoInicio;
    private DatePicker dpPeriodoFim;
    private Label lblPeriodoInfo;
    private BarChart<String, Number> chartBar;
    private PieChart chartPizza;

    // form saída
    private TextField txtValorSaida;
    private ComboBox<Empresa> comboEmpresa;
    private Button btnNovaEmpresa;
    private DatePicker dpDataSaida;
    private TextField txtHoraSaida; // HH:mm
    private TextField txtDescricaoSaida;
    private Button btnSalvarSaida;

    // lista recentes
    private ListView<MovimentacaoCaixa> listaMovs;
    private TableView<FaturaFinanceira> tabelaFaturas;
    private Label lblStatus;
    private Consumer<MovimentacaoCaixa> onMovimentacaoRemoveHandler;
    private Consumer<FaturaFinanceira> onFaturaQuitarHandler;

    public TelaFinanceiroView(Configuracao config) {
        this.config = config;
        construirTela();
    }

    private void construirTela() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + config.getCorPrincipal() + ";");
        root.setTop(criarHeader());

        ScrollPane scroll = new ScrollPane(criarCentro());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + config.getCorPrincipal() + "; -fx-background-color: " + config.getCorPrincipal() + ";");
        root.setCenter(scroll);
    }

    private HBox criarHeader() {
        Label t = new Label("Financeiro — Fluxo de Caixa");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        t.setStyle("-fx-text-fill: #1B5E20;");

        btnVoltar = criarBotao("Voltar", false);
        btnVoltar.setPrefWidth(120);
        btnVoltar.setPrefHeight(38);

        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);

        HBox h = new HBox(15, btnVoltar, esp, t);
        h.setAlignment(Pos.CENTER);
        h.setPadding(new Insets(15, 30, 15, 30));
        h.setStyle("-fx-background-color: derive(" + config.getCorPrincipal() + ", -5%);");
        return h;
    }

    private VBox criarCentro() {
        VBox centro = new VBox(20);
        centro.setPadding(new Insets(20));

        // Cards
        HBox cards = criarCards();

        // Filtros
        HBox filtros = criarFiltros();

        // Gráficos
        HBox graficos = criarGraficos();

        // Form + Lista
        HBox bottom = criarFormELista();

        lblStatus = new Label("");
        lblStatus.setFont(Font.font("Arial", 13));
        lblStatus.setStyle("-fx-text-fill: #2E7D32;");

        centro.getChildren().addAll(cards, filtros, graficos, new Separator(), bottom, lblStatus);
        return centro;
    }

    private HBox criarCards() {
        lblEntradas = criarCardLabel("R$ 0,00", "#2E7D32");
        lblSaidas = criarCardLabel("R$ 0,00", "#C62828");
        lblSaldo = criarCardLabel("R$ 0,00", "#1B5E20");

        VBox c1 = cardBox("Entradas (Ganhos)", lblEntradas, "#E8F5E9", "#43A047");
        VBox c2 = cardBox("Saídas (Gastos)", lblSaidas, "#FFEBEE", "#E53935");
        VBox c3 = cardBox("Saldo Bruto", lblSaldo, "#E3F2FD", "#1E88E5");

        HBox h = new HBox(15, c1, c2, c3);
        h.setAlignment(Pos.CENTER);
        return h;
    }

    private Label criarCardLabel(String txt, String cor) {
        Label l = new Label(txt);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        l.setStyle("-fx-text-fill: " + cor + ";");
        return l;
    }

    private VBox cardBox(String titulo, Label valor, String bg, String border) {
        Label lt = new Label(titulo);
        lt.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        lt.setStyle("-fx-text-fill: #555;");
        VBox v = new VBox(6, lt, valor);
        v.setAlignment(Pos.CENTER);
        v.setPadding(new Insets(16, 24, 16, 24));
        v.setMinWidth(220);
        v.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 14; -fx-border-color: " + border + "; -fx-border-radius: 14; -fx-border-width: 1.5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8,0,0,2);");
        HBox.setHgrow(v, Priority.ALWAYS);
        return v;
    }

    private HBox criarFiltros() {
        Label lf = new Label("Período:");
        lf.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        lf.setStyle("-fx-text-fill: #1B5E20;");

        comboPeriodo = new ComboBox<>(FXCollections.observableArrayList("Hoje", "Últimos 7 dias", "Mês Atual", "Personalizado", "Tudo"));
        comboPeriodo.getSelectionModel().select(2); // Mês Atual padrão
        comboPeriodo.setPrefWidth(180);
        estilizarCombo(comboPeriodo);

        dpPeriodoInicio = new DatePicker(LocalDate.now().withDayOfMonth(1));
        dpPeriodoFim = new DatePicker(LocalDate.now());
        dpPeriodoInicio.setPromptText("Data inicial");
        dpPeriodoFim.setPromptText("Data final");
        estilizarDatePicker(dpPeriodoInicio);
        estilizarDatePicker(dpPeriodoFim);
        dpPeriodoInicio.setVisible(false);
        dpPeriodoFim.setVisible(false);
        dpPeriodoInicio.setManaged(false);
        dpPeriodoFim.setManaged(false);

        lblPeriodoInfo = new Label("");
        lblPeriodoInfo.setFont(Font.font("Arial", 12));
        lblPeriodoInfo.setStyle("-fx-text-fill: #666;");

        HBox h = new HBox(12, lf, comboPeriodo, dpPeriodoInicio, dpPeriodoFim, lblPeriodoInfo);
        h.setAlignment(Pos.CENTER_LEFT);
        h.setPadding(new Insets(4, 0, 4, 0));
        return h;
    }

    private HBox criarGraficos() {
        // Bar Entradas x Saídas
        CategoryAxis xBar = new CategoryAxis();
        NumberAxis yBar = new NumberAxis();
        xBar.setLabel("Fluxo");
        yBar.setLabel("R$ (bruto)");
        chartBar = new BarChart<>(xBar, yBar);
        chartBar.setTitle("Ganhos vs Gastos");
        chartBar.setLegendVisible(false);
        chartBar.setAnimated(false);
        chartBar.setPrefHeight(280);
        VBox.setVgrow(chartBar, Priority.ALWAYS);
        VBox boxBar = new VBox(chartBar);
        boxBar.setPadding(new Insets(10));
        boxBar.setStyle(cardStyle());
        HBox.setHgrow(boxBar, Priority.ALWAYS);

        // Pizza por fornecedor
        chartPizza = new PieChart();
        chartPizza.setTitle("Gastos por Fornecedor");
        chartPizza.setLegendVisible(true);
        chartPizza.setPrefHeight(280);
        VBox boxPizza = new VBox(chartPizza);
        boxPizza.setPadding(new Insets(10));
        boxPizza.setStyle(cardStyle());
        HBox.setHgrow(boxPizza, Priority.ALWAYS);

        HBox h = new HBox(15, boxBar, boxPizza);
        return h;
    }

    private HBox criarFormELista() {
        // --- FORM SAÍDA ---
        Label titForm = new Label("Lançar Saída (Despesa)");
        titForm.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titForm.setStyle("-fx-text-fill: #C62828;");

        Label lValor = smallLabel("Valor (R$) *");
        txtValorSaida = new TextField();
        txtValorSaida.setPromptText("200,00");
        estilizarCampo(txtValorSaida);
        // só números
        txtValorSaida.textProperty().addListener((o, old, nv) -> {
            if (nv != null && !nv.matches("[0-9.,]*")) txtValorSaida.setText(old);
        });

        Label lEmp = smallLabel("Fornecedor / Empresa *");
        comboEmpresa = new ComboBox<>();
        comboEmpresa.setPromptText("Selecione o fornecedor");
        comboEmpresa.setPrefWidth(260);
        estilizarCombo(comboEmpresa);
        comboEmpresa.setConverter(new StringConverter<>() {
            @Override public String toString(Empresa e) { return e == null ? "" : e.getNomeRazao(); }
            @Override public Empresa fromString(String s) { return null; }
        });

        btnNovaEmpresa = criarBotao("+ Empresa", true);
        btnNovaEmpresa.setPrefWidth(120);
        btnNovaEmpresa.setTooltip(new Tooltip("Cadastrar novo fornecedor sem sair da tela"));

        HBox hEmpresa = new HBox(8, comboEmpresa, btnNovaEmpresa);
        hEmpresa.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(comboEmpresa, Priority.ALWAYS);

        Label lData = smallLabel("Data / Vencimento *");
        Label dicaData = new Label("Uma data futura cadastra uma fatura pendente.");
        dicaData.setStyle("-fx-text-fill: #666; -fx-font-size: 11px;");
        dpDataSaida = new DatePicker(LocalDate.now());
        dpDataSaida.setPrefWidth(170);
        estilizarDatePicker(dpDataSaida);

        txtHoraSaida = new TextField(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        txtHoraSaida.setPromptText("HH:mm");
        txtHoraSaida.setPrefWidth(90);
        estilizarCampo(txtHoraSaida);

        HBox hData = new HBox(8, dpDataSaida, txtHoraSaida);
        hData.setAlignment(Pos.CENTER_LEFT);

        Label lDesc = smallLabel("Descrição");
        txtDescricaoSaida = new TextField();
        txtDescricaoSaida.setPromptText("Ex: Compra de castanhas a granel");
        estilizarCampo(txtDescricaoSaida);

        btnSalvarSaida = criarBotaoVerde("Salvar Saída");
        btnSalvarSaida.setPrefWidth(Double.MAX_VALUE);
        btnSalvarSaida.setPrefHeight(46);
        btnSalvarSaida.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        dpDataSaida.valueProperty().addListener((obs, antiga, novaData) ->
                btnSalvarSaida.setText(novaData != null && novaData.isAfter(LocalDate.now())
                        ? "Salvar Fatura"
                        : "Salvar Saída"));

        VBox form = new VBox(8, titForm, lValor, txtValorSaida, lEmp, hEmpresa, lData, dicaData, hData, lDesc, txtDescricaoSaida, btnSalvarSaida);
        form.setPadding(new Insets(16));
        form.setPrefWidth(420);
        form.setMinWidth(360);
        form.setStyle(cardStyle());

        // --- LISTA MOVIMENTAÇÕES ---
        Label titLista = new Label("Movimentações e faturas");
        titLista.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        titLista.setStyle("-fx-text-fill: #1B5E20;");

        listaMovs = new ListView<>();
        listaMovs.setPlaceholder(new Label("Nenhuma movimentação no período"));
        listaMovs.setStyle("-fx-background-color: #FAFFF5; -fx-border-color: #A5D6A7; -fx-border-radius: 10; -fx-background-radius: 10;");
        listaMovs.setCellFactory(lv -> new ListCell<>() {
            private final Button btnRemove = new Button("x");
            private final Label lblTexto = new Label();
            private final HBox linha = new HBox(10, lblTexto, btnRemove);

            {
                btnRemove.setVisible(false);
                btnRemove.setManaged(false);
                btnRemove.setStyle("-fx-background-color:#E53935;-fx-text-fill:#FFF;-fx-font-weight:bold;"
                        + "-fx-background-radius:50%;-fx-min-width:24px;-fx-min-height:24px;"
                        + "-fx-max-width:24px;-fx-max-height:24px;-fx-padding:0;-fx-cursor:hand;");
                btnRemove.setOnMouseEntered(e -> btnRemove.setStyle(
                        "-fx-background-color:#B71C1C;-fx-text-fill:#FFF;-fx-font-weight:bold;"
                        + "-fx-background-radius:50%;-fx-min-width:24px;-fx-min-height:24px;"
                        + "-fx-max-width:24px;-fx-max-height:24px;-fx-padding:0;-fx-cursor:hand;"));
                btnRemove.setOnMouseExited(e -> btnRemove.setStyle(
                        "-fx-background-color:#E53935;-fx-text-fill:#FFF;-fx-font-weight:bold;"
                        + "-fx-background-radius:50%;-fx-min-width:24px;-fx-min-height:24px;"
                        + "-fx-max-width:24px;-fx-max-height:24px;-fx-padding:0;-fx-cursor:hand;"));
                HBox.setHgrow(lblTexto, Priority.ALWAYS);
                linha.setAlignment(Pos.CENTER_LEFT);
                btnRemove.setOnAction(e -> {
                    MovimentacaoCaixa movimentacao = getItem();
                    if (movimentacao != null && onMovimentacaoRemoveHandler != null) {
                        onMovimentacaoRemoveHandler.accept(movimentacao);
                    }
                });
                selectedProperty().addListener((obs, antigo, selecionado) -> {
                    btnRemove.setVisible(selecionado);
                    btnRemove.setManaged(selecionado);
                });
            }

            @Override protected void updateItem(MovimentacaoCaixa m, boolean empty) {
                super.updateItem(m, empty);
                if (empty || m == null) {
                    btnRemove.setVisible(false);
                    btnRemove.setManaged(false);
                    setText(null);
                    setGraphic(null);
                    return;
                }
                String fornecedor = m.getEmpresa() != null ? " • " + m.getEmpresa().getNomeRazao() : "";
                String cor = m.getTipo() == TipoMovimentacao.ENTRADA ? "#2E7D32" : "#C62828";
                String txt = String.format("%s%s | %s | R$ %.2f [%s]",
                        m.getDescricao().isBlank() ? (m.getTipo()== TipoMovimentacao.ENTRADA?"Entrada PDV":"Saída") : m.getDescricao(),
                        fornecedor, m.getDataFormatada(), m.getValor(), m.getOrigem());
                lblTexto.setText(txt);
                lblTexto.setStyle("-fx-text-fill: " + cor + "; -fx-font-size: 13px; -fx-font-weight: 600;");
                setText(null);
                setGraphic(linha);
                setStyle("-fx-font-size: 13px; -fx-font-weight: 600;");
            }
        });
        VBox.setVgrow(listaMovs, Priority.ALWAYS);

        tabelaFaturas = criarTabelaFaturas();
        Tab abaMovimentacoes = new Tab("Movimentações", listaMovs);
        Tab abaFaturas = new Tab("Faturas", tabelaFaturas);
        abaMovimentacoes.setClosable(false);
        abaFaturas.setClosable(false);
        TabPane abas = new TabPane(abaMovimentacoes, abaFaturas);
        VBox.setVgrow(abas, Priority.ALWAYS);

        VBox listaBox = new VBox(10, titLista, abas);
        listaBox.setPadding(new Insets(16));
        listaBox.setStyle(cardStyle());
        HBox.setHgrow(listaBox, Priority.ALWAYS);
        listaBox.setPrefHeight(380);

        HBox h = new HBox(15, form, listaBox);
        HBox.setHgrow(listaBox, Priority.ALWAYS);
        return h;
    }

    private TableView<FaturaFinanceira> criarTabelaFaturas() {
        TableView<FaturaFinanceira> tabela = new TableView<>();
        tabela.setPlaceholder(new Label("Nenhuma fatura cadastrada"));
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<FaturaFinanceira, String> descricao = new TableColumn<>("Descrição");
        descricao.setCellValueFactory(dado -> new ReadOnlyStringWrapper(
                dado.getValue().getDescricao().isBlank() ? "Fatura" : dado.getValue().getDescricao()));

        TableColumn<FaturaFinanceira, String> empresa = new TableColumn<>("Fornecedor");
        empresa.setCellValueFactory(dado -> new ReadOnlyStringWrapper(
                dado.getValue().getEmpresa().getNomeRazao()));

        TableColumn<FaturaFinanceira, String> vencimento = new TableColumn<>("Vencimento");
        vencimento.setCellValueFactory(dado -> new ReadOnlyStringWrapper(
                dado.getValue().getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        TableColumn<FaturaFinanceira, String> valor = new TableColumn<>("Valor");
        valor.setCellValueFactory(dado -> new ReadOnlyStringWrapper(
                "R$ " + String.format("%.2f", dado.getValue().getValor())));

        TableColumn<FaturaFinanceira, String> estado = new TableColumn<>("Estado");
        estado.setCellValueFactory(dado -> new ReadOnlyStringWrapper(
                dado.getValue().getEstado().getNome()));
        estado.setCellFactory(coluna -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item);
                setStyle(empty ? "" : switch (item) {
                    case "QUITADA" -> "-fx-text-fill: #2E7D32; -fx-font-weight: bold;";
                    case "ATRASADA" -> "-fx-text-fill: #C62828; -fx-font-weight: bold;";
                    default -> "-fx-text-fill: #EF6C00; -fx-font-weight: bold;";
                });
            }
        });

        TableColumn<FaturaFinanceira, Void> acoes = new TableColumn<>("Ação");
        acoes.setCellFactory(coluna -> new TableCell<>() {
            private final Button btnQuitar = new Button("Quitar");

            {
                btnQuitar.setOnAction(event -> {
                    FaturaFinanceira fatura = getTableRow().getItem();
                    if (onFaturaQuitarHandler != null) {
                        onFaturaQuitarHandler.accept(fatura);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                FaturaFinanceira fatura = empty ? null : getTableRow().getItem();
                btnQuitar.setDisable(fatura == null || fatura.estaQuitada());
                setGraphic(empty ? null : btnQuitar);
            }
        });

        tabela.getColumns().addAll(descricao, empresa, vencimento, valor, estado, acoes);
        return tabela;
    }

    // ---- helpers estilo ----
    private Label smallLabel(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        l.setStyle("-fx-text-fill: #33691E;");
        return l;
    }

    private String cardStyle() {
        return "-fx-background-color: rgba(255,255,255,0.88); -fx-background-radius: 14; -fx-border-color: #A5D6A7; -fx-border-radius: 14; -fx-border-width: 1.2;";
    }

    private void estilizarCampo(TextField tf) {
        String n = "-fx-background-color:#FAFFF5;-fx-border-color:#A5D6A7;-fx-border-radius:10;-fx-background-radius:10;-fx-padding:8;-fx-text-fill:#000;";
        String f = "-fx-background-color:#FAFFF5;-fx-border-color:#43A047;-fx-border-radius:10;-fx-background-radius:10;-fx-border-width:2;-fx-padding:7;-fx-text-fill:#000;";
        tf.setStyle(n);
        tf.focusedProperty().addListener((o,a,b)-> tf.setStyle(b?f:n));
    }

    private <T> void estilizarCombo(ComboBox<T> cb) {
        cb.setStyle("-fx-background-color:#FAFFF5;-fx-border-color:#A5D6A7;-fx-border-radius:10;-fx-background-radius:10;");
    }
    private void estilizarDatePicker(DatePicker dp) {
        dp.setStyle("-fx-background-color:#FAFFF5;-fx-border-color:#A5D6A7;-fx-border-radius:10;-fx-background-radius:10;");
    }

    private Button criarBotao(String txt, boolean pequeno) {
        Button b = new Button(txt);
        b.setFont(Font.font("Arial", FontWeight.BOLD, pequeno?12:14));
        String raio = config.isBordaArredondada()? String.valueOf(config.getRaioBorada()):"0";
        String n="-fx-background-color:"+config.getCorBotoes()+";-fx-text-fill:"+config.getCorTextoBotoes()+";-fx-background-radius:"+raio+";-fx-border-radius:"+raio+";-fx-cursor:hand;-fx-border-color:#A5D6A7;-fx-border-width:1;";
        String h="-fx-background-color:derive("+config.getCorBotoes()+",-10%);-fx-text-fill:"+config.getCorTextoBotoes()+";-fx-background-radius:"+raio+";-fx-border-radius:"+raio+";-fx-cursor:hand;-fx-border-color:#81C784;-fx-border-width:1.5;";
        b.setStyle(n); b.setOnMouseEntered(e->b.setStyle(h)); b.setOnMouseExited(e->b.setStyle(n));
        return b;
    }
    private Button criarBotaoVerde(String txt) {
        Button b=new Button(txt);
        String raio=config.isBordaArredondada()?String.valueOf(config.getRaioBorada()):"0";
        String n="-fx-background-color:#C62828;-fx-text-fill:#FFF;-fx-background-radius:"+raio+";-fx-border-radius:"+raio+";-fx-cursor:hand;";
        String h="-fx-background-color:#B71C1C;-fx-text-fill:#FFF;-fx-background-radius:"+raio+";-fx-border-radius:"+raio+";-fx-cursor:hand;";
        b.setStyle(n); b.setOnMouseEntered(e->b.setStyle(h)); b.setOnMouseExited(e->b.setStyle(n));
        return b;
    }

    // ---- API para Controller ----

    public void setEmpresas(List<Empresa> empresas) {
        Empresa sel = comboEmpresa.getSelectionModel().getSelectedItem();
        comboEmpresa.setItems(FXCollections.observableArrayList(empresas));
        if (sel != null) {
            // tenta manter seleção
            empresas.stream().filter(e->e.getId().equals(sel.getId())).findFirst().ifPresent(e-> comboEmpresa.getSelectionModel().select(e));
        }
    }

    public void popularDashboard(BigDecimal entradas, BigDecimal saidas, BigDecimal saldo,
                                 Map<Empresa, BigDecimal> gastosPorFornecedor,
                                 List<MovimentacaoCaixa> movsPeriodo,
                                 String periodoTexto) {
        lblEntradas.setText("R$ " + String.format("%.2f", entradas));
        lblSaidas.setText("R$ " + String.format("%.2f", saidas));
        lblSaldo.setText("R$ " + String.format("%.2f", saldo));
        lblSaldo.setStyle("-fx-text-fill: " + (saldo.compareTo(BigDecimal.ZERO) >=0 ? "#1B5E20" : "#C62828") + "; -fx-font-size: 22; -fx-font-weight: bold;");
        lblPeriodoInfo.setText(periodoTexto);

        // BarChart
        chartBar.getData().clear();
        XYChart.Series<String, Number> s = new XYChart.Series<>();
        s.getData().add(new XYChart.Data<>("Entradas", entradas));
        s.getData().add(new XYChart.Data<>("Saídas", saidas));
        chartBar.getData().add(s);
        // colore barras após render
        chartBar.lookupAll(".chart-bar").forEach(n -> {
            // será aplicado no próximo pulse; fallback simples
        });

        // PieChart
        chartPizza.getData().clear();
        if (gastosPorFornecedor == null || gastosPorFornecedor.isEmpty()) {
            chartPizza.setData(FXCollections.observableArrayList(new PieChart.Data("Sem gastos", 1)));
        } else {
            gastosPorFornecedor.forEach((emp, val) ->
                    chartPizza.getData().add(new PieChart.Data(emp.getNomeRazao() + " (R$ " + String.format("%.2f", val) + ")", val.doubleValue()))
            );
        }

        // lista
        listaMovs.setItems(FXCollections.observableArrayList(movsPeriodo));
    }

    public void setFaturas(List<FaturaFinanceira> faturas) {
        tabelaFaturas.setItems(FXCollections.observableArrayList(faturas));
        // O estado é mutável dentro da mesma fatura; forçamos a tabela a
        // recalcular texto, cor e botão após uma quitação confirmada.
        tabelaFaturas.refresh();
    }

    public LocalDateTime getDataHoraSaida() {
        LocalDate d = dpDataSaida.getValue();
        if (d == null) return LocalDateTime.now();
        String h = txtHoraSaida.getText()==null? "": txtHoraSaida.getText().trim();
        LocalTime t;
        try {
            t = h.isBlank() ? LocalTime.now().withSecond(0).withNano(0) : LocalTime.parse(h, DateTimeFormatter.ofPattern("H:mm"));
        } catch (Exception e) {
            t = LocalTime.now();
        }
        return LocalDateTime.of(d, t);
    }

    public void limparFormSaida() {
        txtValorSaida.clear();
        txtDescricaoSaida.clear();
        // mantém empresa e data
    }

    public void setStatus(String msg, boolean erro) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: " + (erro? "#C62828":"#2E7D32") + ";");
    }

    // getters
    public BorderPane getRoot() { return root; }
    public Button getBtnVoltar() { return btnVoltar; }
    public ComboBox<String> getComboPeriodo() { return comboPeriodo; }
    public DatePicker getDpPeriodoInicio() { return dpPeriodoInicio; }
    public DatePicker getDpPeriodoFim() { return dpPeriodoFim; }
    public void atualizarVisibilidadePeriodoPersonalizado() {
        boolean personalizado = "Personalizado".equals(comboPeriodo.getValue());
        dpPeriodoInicio.setVisible(personalizado);
        dpPeriodoFim.setVisible(personalizado);
        dpPeriodoInicio.setManaged(personalizado);
        dpPeriodoFim.setManaged(personalizado);
    }
    public TextField getTxtValorSaida() { return txtValorSaida; }
    public ComboBox<Empresa> getComboEmpresa() { return comboEmpresa; }
    public Button getBtnNovaEmpresa() { return btnNovaEmpresa; }
    public DatePicker getDpDataSaida() { return dpDataSaida; }
    public TextField getTxtHoraSaida() { return txtHoraSaida; }
    public TextField getTxtDescricaoSaida() { return txtDescricaoSaida; }
    public Button getBtnSalvarSaida() { return btnSalvarSaida; }
    public ListView<MovimentacaoCaixa> getListaMovs() { return listaMovs; }
    public void setOnMovimentacaoRemoveHandler(Consumer<MovimentacaoCaixa> handler) {
        this.onMovimentacaoRemoveHandler = handler;
    }
    public void setOnFaturaQuitarHandler(Consumer<FaturaFinanceira> handler) {
        this.onFaturaQuitarHandler = handler;
    }
}
