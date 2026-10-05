package pdv.demo4.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pdv.demo4.model.Configuracao;
import pdv.demo4.model.Empresa;

import java.util.List;

public class TelaEmpresasView {
    private final Configuracao config;
    private BorderPane root;
    private Button btnVoltar;
    private Button btnNova;
    private Button btnEditar;
    private Button btnExcluir;
    private ListView<Empresa> lista;
    private TextField txtNome;
    private TextField txtTelefone;
    private TextField txtCnpjCpf;
    private Button btnSalvarEdicao;

    public TelaEmpresasView(Configuracao config) {
        this.config = config;
        construirTela();
    }

    private void construirTela() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: " + config.getCorPrincipal() + ";");

        btnVoltar = new Button("Voltar");
        btnNova = new Button("Nova empresa");
        btnEditar = new Button("Editar");
        btnExcluir = new Button("Excluir");
        HBox acoes = new HBox(10, btnVoltar, btnNova, btnEditar, btnExcluir);
        acoes.setAlignment(Pos.CENTER_LEFT);
        acoes.setPadding(new Insets(15, 20, 10, 20));

        Label titulo = new Label("Empresas");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titulo.setStyle("-fx-text-fill: #1B5E20;");
        HBox cabecalho = new HBox(15, acoes, titulo);
        cabecalho.setAlignment(Pos.CENTER);
        root.setTop(cabecalho);

        lista = new ListView<>();
        lista.setPlaceholder(new Label("Nenhuma empresa cadastrada."));
        lista.setCellFactory(v -> new ListCell<>() {
            @Override
            protected void updateItem(Empresa empresa, boolean vazio) {
                super.updateItem(empresa, vazio);
                if (vazio || empresa == null) {
                    setText(null);
                    return;
                }
                setText(String.format("%s    |    Telefone: %s    |    CNPJ/CPF: %s",
                        empresa.getNomeRazao(),
                        empresa.getTelefone().isBlank() ? "não informado" : empresa.getTelefone(),
                        empresa.getCnpjCpf().isBlank() ? "não informado" : empresa.getCnpjCpf()));
            }
        });
        lista.setStyle("-fx-font-size: 18px; -fx-background-color:#FAFFF5;-fx-border-color:#A5D6A7;");
        lista.getSelectionModel().selectedItemProperty().addListener((obs, anterior, empresa) -> preencherFormulario(empresa));

        Label tituloFormulario = new Label("Dados da empresa selecionada");
        tituloFormulario.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tituloFormulario.setStyle("-fx-text-fill:#1B5E20;");
        txtNome = criarCampo("Nome / Razão social");
        txtTelefone = criarCampo("Telefone");
        txtCnpjCpf = criarCampo("CNPJ / CPF");
        btnSalvarEdicao = criarBotao("Salvar alterações");
        btnSalvarEdicao.setDisable(true);
        VBox formulario = new VBox(8, tituloFormulario,
                new Label("Nome / Razão social"), txtNome,
                new Label("Telefone"), txtTelefone,
                new Label("CNPJ / CPF"), txtCnpjCpf,
                btnSalvarEdicao);
        formulario.setPadding(new Insets(15));
        formulario.setPrefWidth(330);
        formulario.setStyle("-fx-background-color:rgba(255,255,255,0.88);-fx-background-radius:14;"
                + "-fx-border-color:#A5D6A7;-fx-border-radius:14;");

        HBox centro = new HBox(15, lista, formulario);
        centro.setPadding(new Insets(15, 20, 20, 20));
        VBox.setVgrow(lista, Priority.ALWAYS);
        HBox.setHgrow(lista, Priority.ALWAYS);
        root.setCenter(centro);
    }

    private TextField criarCampo(String prompt) {
        TextField campo = new TextField();
        campo.setPromptText(prompt);
        campo.setStyle("-fx-background-color:#FAFFF5;-fx-border-color:#A5D6A7;"
                + "-fx-border-radius:10;-fx-background-radius:10;-fx-padding:8;");
        return campo;
    }

    private Button criarBotao(String texto) {
        Button botao = new Button(texto);
        botao.setPrefHeight(40);
        botao.setStyle("-fx-background-color:#2E7D32;-fx-text-fill:#FFF;"
                + "-fx-background-radius:10;-fx-border-radius:10;-fx-cursor:hand;");
        return botao;
    }

    private void preencherFormulario(Empresa empresa) {
        boolean selecionada = empresa != null;
        txtNome.setDisable(!selecionada);
        txtTelefone.setDisable(!selecionada);
        txtCnpjCpf.setDisable(!selecionada);
        btnSalvarEdicao.setDisable(!selecionada);
        if (!selecionada) {
            txtNome.clear();
            txtTelefone.clear();
            txtCnpjCpf.clear();
            return;
        }
        txtNome.setText(empresa.getNomeRazao());
        txtTelefone.setText(empresa.getTelefone());
        txtCnpjCpf.setText(empresa.getCnpjCpf());
    }

    public void carregarEmpresas(List<Empresa> empresas) {
        lista.getItems().setAll(empresas);
    }

    public Empresa getEmpresaSelecionada() {
        return lista.getSelectionModel().getSelectedItem();
    }

    public BorderPane getRoot() { return root; }
    public Button getBtnVoltar() { return btnVoltar; }
    public Button getBtnNova() { return btnNova; }
    public Button getBtnEditar() { return btnEditar; }
    public Button getBtnExcluir() { return btnExcluir; }
    public TextField getTxtNome() { return txtNome; }
    public TextField getTxtTelefone() { return txtTelefone; }
    public TextField getTxtCnpjCpf() { return txtCnpjCpf; }
    public Button getBtnSalvarEdicao() { return btnSalvarEdicao; }
}
