package pdv.demo4.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pdv.demo4.controller.EventoFatura;
import pdv.demo4.controller.TipoEventoFatura;
import pdv.demo4.model.FaturaFinanceira;

import java.util.Objects;
import java.util.function.Consumer;

/** Apresenta lembretes observados em um painel não modal no topo da janela. */
public final class PopupAlertasFatura {
    private final Stage janela;
    private final VBox alertas = new VBox(8);
    private final Consumer<String> aoAbrirFatura;

    public PopupAlertasFatura(Stage janelaPrincipal, Consumer<String> aoAbrirFatura) {
        this.janela = new Stage(StageStyle.TRANSPARENT);
        this.janela.initOwner(Objects.requireNonNull(janelaPrincipal));
        this.aoAbrirFatura = Objects.requireNonNull(aoAbrirFatura);

        Label titulo = new Label("Lembretes de faturas");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1B5E20;");
        VBox conteudo = new VBox(10, titulo, alertas);
        conteudo.setPadding(new Insets(14));
        conteudo.setPrefWidth(460);
        conteudo.setStyle(
                "-fx-background-color: white; -fx-background-radius: 12;"
                        + "-fx-border-color: #A5D6A7; -fx-border-radius: 12;"
                        + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 12, 0, 0, 4);");

        Scene cena = new Scene(conteudo);
        cena.setFill(Color.TRANSPARENT);
        this.janela.setScene(cena);
        this.janela.setAlwaysOnTop(true);
    }

    public void mostrar(EventoFatura evento) {
        FaturaFinanceira fatura = evento.getFatura();

        Label mensagem = new Label(formatarMensagem(evento));
        mensagem.setWrapText(true);
        mensagem.setStyle("-fx-font-size: 13px; -fx-text-fill: #263238;");

        Label detalhes = new Label(
                (fatura.getDescricao().isBlank() ? "Fatura" : fatura.getDescricao())
                        + " — " + fatura.getEmpresa().getNomeRazao()
                        + " — R$ " + String.format("%.2f", fatura.getValor()));
        detalhes.setWrapText(true);
        detalhes.setStyle("-fx-font-size: 12px; -fx-text-fill: #546E7A;");

        Button verFatura = new Button("Ver fatura");
        verFatura.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white;");
        verFatura.setOnAction(acao -> {
            janela.hide();
            alertas.getChildren().clear();
            aoAbrirFatura.accept(fatura.getId());
        });

        Button fechar = new Button("X");
        fechar.setAccessibleText("Fechar este alerta");

        HBox cabecalho = new HBox(8, mensagem, fechar);
        cabecalho.setAlignment(Pos.TOP_RIGHT);
        HBox.setHgrow(mensagem, Priority.ALWAYS);

        VBox cartao = new VBox(7, cabecalho, detalhes, verFatura);
        fechar.setOnAction(acao -> removerAlerta(cartao));
        cartao.setPadding(new Insets(10));
        cartao.setStyle(
                "-fx-background-color: #F5FBF5; -fx-background-radius: 8;"
                        + "-fx-border-color: #C8E6C9; -fx-border-radius: 8;");
        alertas.getChildren().add(cartao);

        posicionarNoTopo();
        if (!janela.isShowing()) {
            janela.show();
        }
        janela.sizeToScene();
        posicionarNoTopo();
    }

    public void fechar() {
        janela.close();
    }

    private void removerAlerta(VBox cartao) {
        alertas.getChildren().remove(cartao);
        if (alertas.getChildren().isEmpty()) {
            janela.hide();
        }
    }

    private void posicionarNoTopo() {
        Stage proprietaria = (Stage) janela.getOwner();
        janela.setX(proprietaria.getX() + (proprietaria.getWidth() - 460) / 2);
        janela.setY(proprietaria.getY() + 24);
    }

    private String formatarMensagem(EventoFatura evento) {
        long dias = evento.getDiasEmRelacaoAoVencimento();
        return switch (evento.getTipo()) {
            case LEMBRETE_VENCIMENTO -> dias == 1
                    ? "A fatura vence amanhã."
                    : "A fatura vence em " + dias + " dias.";
            case VENCIMENTO_HOJE -> "A fatura vence hoje.";
            case FATURA_ATRASADA -> dias == 1
                    ? "A fatura está atrasada há 1 dia."
                    : "A fatura está atrasada há " + dias + " dias.";
            case FATURA_CRIADA, FATURA_QUITADA -> "";
        };
    }
}
