package pdv.demo4.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import pdv.demo4.model.Configuracao;

import java.time.LocalDate;

/**
 * Tela de backup - permite selecionar período e gerar/carregar backup.
 */
public class TelaBackupView {

    private BorderPane root;
    private Button btnVoltar, btnGerarBackup, btnCarregarBackup;
    private DatePicker dpInicio, dpFim;
    private TextArea txtPreview;
    private Label lblStatus;
    private Configuracao config;

    public TelaBackupView(Configuracao config) {
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
        Label titulo = new Label("Backup");
        titulo.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titulo.setStyle("-fx-text-fill: #1B5E20;");
        Region esp = new Region();
        HBox.setHgrow(esp, Priority.ALWAYS);
        HBox header = new HBox(15, btnVoltar, esp, titulo);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15, 20, 15, 20));
        root.setTop(header);

        // Centro
        Label lblInicio = new Label("Data Inicial:");
        lblInicio.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblInicio.setStyle("-fx-text-fill:#000;");
        dpInicio = new DatePicker(LocalDate.now().minusMonths(1));
        dpInicio.setStyle("-fx-font-size: 16px;");

        Label lblFim = new Label("Data Final:");
        lblFim.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lblFim.setStyle("-fx-text-fill:#000;");
        dpFim = new DatePicker(LocalDate.now());
        dpFim.setStyle("-fx-font-size: 16px;");

        btnGerarBackup = new Button("Gerar Backup TXT");
        btnGerarBackup.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btnGerarBackup.setPrefHeight(50);
        String n = "-fx-background-color:#2E7D32;-fx-text-fill:#FFF;-fx-background-radius:12;-fx-cursor:hand;";
        String h = "-fx-background-color:#1B5E20;-fx-text-fill:#FFF;-fx-background-radius:12;-fx-cursor:hand;";
        btnGerarBackup.setStyle(n);
        btnGerarBackup.setOnMouseEntered(e -> btnGerarBackup.setStyle(h));
        btnGerarBackup.setOnMouseExited(e -> btnGerarBackup.setStyle(n));

        btnCarregarBackup = new Button("Carregar Backup");
        btnCarregarBackup.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btnCarregarBackup.setPrefHeight(50);
        btnCarregarBackup.setStyle(n);
        btnCarregarBackup.setOnMouseEntered(e -> btnCarregarBackup.setStyle(h));
        btnCarregarBackup.setOnMouseExited(e -> btnCarregarBackup.setStyle(n));

        lblStatus = new Label("");
        lblStatus.setFont(Font.font("Arial", 16));
        lblStatus.setStyle("-fx-text-fill:#555;");

        HBox botoesBackup = new HBox(15, btnGerarBackup, btnCarregarBackup);
        botoesBackup.setAlignment(Pos.CENTER_LEFT);
        HBox linhaDatas = new HBox(15, lblInicio, dpInicio, lblFim, dpFim, botoesBackup);
        linhaDatas.setAlignment(Pos.CENTER_LEFT);
        linhaDatas.setPadding(new Insets(15, 20, 10, 20));

        txtPreview = new TextArea("O relatório aparecerá aqui...");
        txtPreview.setEditable(false);
        txtPreview.setWrapText(true);
        txtPreview.setFont(Font.font("Courier New", 18));
        txtPreview.setStyle("-fx-control-inner-background:#FAFFF5;-fx-border-color:#A5D6A7;-fx-border-radius:8;");

        VBox centro = new VBox(10, linhaDatas, lblStatus, txtPreview);
        centro.setPadding(new Insets(10, 20, 15, 20));
        VBox.setVgrow(txtPreview, Priority.ALWAYS);
        root.setCenter(centro);
    }

    public BorderPane getRoot() { return root; }
    public Button getBtnVoltar() { return btnVoltar; }
    public Button getBtnGerarBackup() { return btnGerarBackup; }
    public Button getBtnCarregarBackup() { return btnCarregarBackup; }
    public DatePicker getDpInicio() { return dpInicio; }
    public DatePicker getDpFim() { return dpFim; }
    public TextArea getTxtPreview() { return txtPreview; }
    public Label getLblStatus() { return lblStatus; }
}
