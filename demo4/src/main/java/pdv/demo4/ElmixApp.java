package pdv.demo4;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import pdv.demo4.controller.ConfiguracaoController;
import pdv.demo4.controller.FinanceiroController;
import pdv.demo4.controller.VendaController;
import pdv.demo4.model.Configuracao;
import pdv.demo4.model.Empresa;
import pdv.demo4.model.ItemVenda;
import pdv.demo4.model.MovimentacaoCaixa;
import pdv.demo4.model.Venda;
import pdv.demo4.model.BackupCompleto;
import pdv.demo4.view.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Classe principal do PDV Elmix.
 * Gerencia a navegação entre telas e conecta Views com Controllers.
 */
public class ElmixApp extends Application {

    private Stage stage;

    // Controllers
    private VendaController vendaController;
    private ConfiguracaoController configController;
    private FinanceiroController financeiroController;

    // Views
    private TelaInicialView telaInicial;
    private TelaPDVView telaPDV;
    private Stage modalVendaUm;
    private Stage modalVendaDois;
    private TelaPDVView telaModalVendaUm;
    private TelaPDVView telaModalVendaDois;
    private VendaController controllerModalVendaUm;
    private VendaController controllerModalVendaDois;
    private int modalVendaAtivo;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        // Inicializa os controllers
        vendaController = new VendaController();
        configController = new ConfiguracaoController();
        financeiroController = new FinanceiroController();

        // Mostra a tela inicial
        mostrarTelaInicial();

        stage.setTitle("PDV Elmix - Produtos Naturais");
        stage.setWidth(1024);
        stage.setHeight(700);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.setMaximized(true);
        stage.show();
    }

    /**
     * Troca a tela atual reaproveitando a Scene para evitar bugs de maximização.
     */
    private void trocarTela(javafx.scene.Parent root, javafx.event.EventHandler<javafx.scene.input.KeyEvent> handler) {
        if (stage.getScene() == null) {
            Scene s = new Scene(root);
            s.setOnKeyPressed(handler);
            stage.setScene(s);
        } else {
            stage.getScene().setRoot(root);
            stage.getScene().setOnKeyPressed(handler);
        }
    }

    // ===================== TELA INICIAL =====================

    private void tentarAbrirPDV() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja abrir o caixa e iniciar as vendas?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Abrir Caixa");
        alert.setHeaderText(null);
        alert.showAndWait().ifPresent(res -> {
            if (res == ButtonType.YES) {
                mostrarTelaPDV();
            }
        });
    }

    /**
     * Mostra a tela inicial e conecta os botões.
     */
    private void mostrarTelaInicial() {
        Configuracao cfg = configController.getConfiguracao();
        telaInicial = new TelaInicialView(cfg);

        // Conecta os botões da barra direita
        telaInicial.getBtnIniciarVenda().setOnAction(e -> tentarAbrirPDV());
        telaInicial.getBtnHistorico().setOnAction(e -> mostrarTelaHistorico());
        telaInicial.getBtnFecharCaixa().setOnAction(e -> fecharCaixa());

        // Conecta os botões da barra esquerda
        telaInicial.getBtnConta().setOnAction(e -> mostrarTelaConta());
        telaInicial.getBtnMoney().setOnAction(e -> mostrarTelaMoney());
        telaInicial.getBtnGestao().setOnAction(e -> mostrarTelaFinanceiro());
        telaInicial.getBtnEmpresas().setOnAction(e -> mostrarTelaEmpresas());
        telaInicial.getBtnSalvarBackup().setOnAction(e -> mostrarTelaBackup());
        telaInicial.getBtnConfiguracoes().setOnAction(e -> mostrarTelaConfig());

        // Atalho F1 para iniciar venda
        trocarTela(telaInicial.getRoot(), ev -> {
            if (ev.getCode() == KeyCode.F1) tentarAbrirPDV();
        });
    }

    private void mostrarTelaEmpresas() {
        TelaEmpresasView tela = new TelaEmpresasView(configController.getConfiguracao());
        Runnable recarregar = () -> tela.carregarEmpresas(financeiroController.getEmpresasAtivas());

        tela.getBtnVoltar().setOnAction(e -> mostrarTelaInicial());
        tela.getBtnNova().setOnAction(e -> abrirCadastroEmpresa(tela, recarregar));
        tela.getBtnEditar().setOnAction(e -> {
            Empresa empresa = tela.getEmpresaSelecionada();
            if (empresa == null) {
                mostrarAlerta("Seleção", "Selecione uma empresa para editar.");
                return;
            }
            tela.getTxtNome().requestFocus();
        });
        tela.getBtnSalvarEdicao().setOnAction(e -> {
            Empresa empresa = tela.getEmpresaSelecionada();
            String nome = tela.getTxtNome().getText().trim();
            if (empresa == null) {
                mostrarAlerta("Seleção", "Selecione uma empresa para editar.");
                return;
            }
            if (nome.isBlank()) {
                tela.getTxtNome().requestFocus();
                mostrarAlerta("Dados inválidos", "O nome da empresa é obrigatório.");
                return;
            }
            empresa.setNomeRazao(nome);
            empresa.setTelefone(tela.getTxtTelefone().getText());
            empresa.setCnpjCpf(tela.getTxtCnpjCpf().getText());
            financeiroController.atualizarEmpresa(empresa);
            recarregar.run();
            mostrarAlerta("Sucesso", "Empresa atualizada com sucesso.");
        });
        tela.getBtnExcluir().setOnAction(e -> {
            Empresa empresa = tela.getEmpresaSelecionada();
            if (empresa == null) {
                mostrarAlerta("Seleção", "Selecione uma empresa para excluir.");
                return;
            }
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    "Deseja realmente excluir a empresa '" + empresa.getNomeRazao() + "'?",
                    ButtonType.OK, ButtonType.CANCEL);
            confirmacao.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK && financeiroController.excluirEmpresa(empresa)) {
                    recarregar.run();
                    mostrarAlerta("Sucesso", "Empresa excluída.");
                }
            });
        });

        recarregar.run();
        trocarTela(tela.getRoot(), ev -> { if (ev.getCode() == KeyCode.ESCAPE) mostrarTelaInicial(); });
    }

    private void abrirCadastroEmpresa(TelaEmpresasView tela, Runnable recarregar) {
        TextInputDialog nome = new TextInputDialog();
        nome.setTitle("Nova empresa");
        nome.setHeaderText("Cadastrar empresa");
        nome.setContentText("Nome / Razão social:");
        nome.showAndWait().ifPresent(valor -> {
            if (valor.isBlank()) return;
            TextInputDialog telefone = new TextInputDialog();
            telefone.setTitle("Telefone");
            telefone.setContentText("Telefone da empresa (opcional):");
            String telefoneValor = telefone.showAndWait().orElse("");
            financeiroController.cadastrarEmpresa(valor, "", telefoneValor);
            recarregar.run();
        });
    }

    // ===================== TELA MONEY (original) =====================

    private void mostrarTelaMoney() {
        Configuracao cfg = configController.getConfiguracao();
        TelaMoneyView telaMoney = new TelaMoneyView(cfg);

        // Popular os dados - faturamento semanal/mensal (vendas)
        double[] semana = vendaController.getTotaisSemanaAtual();
        double[] mes = vendaController.getTotaisSemanasMesAtual();
        String resumo = vendaController.gerarResumoFinanceiro();
        telaMoney.popularDados(semana, mes, resumo);

        // Ações dos botões
        telaMoney.getBtnVoltar().setOnAction(e -> mostrarTelaInicial());
        telaMoney.getBtnBaixarTxt().setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Salvar Resumo Financeiro");
            fc.setInitialFileName("resumo_financeiro_" + LocalDate.now() + ".txt");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo de Texto", "*.txt"));
            File arquivo = fc.showSaveDialog(stage);

            if (arquivo != null) {
                boolean ok = vendaController.salvarResumoFinanceiro(arquivo.getAbsolutePath());
                if (ok) {
                    mostrarAlerta("Sucesso", "Resumo financeiro salvo com sucesso!");
                } else {
                    mostrarAlerta("Erro", "Erro ao salvar resumo financeiro.");
                }
            }
        });

        trocarTela(telaMoney.getRoot(), ev -> {
            if (ev.getCode() == KeyCode.ESCAPE) mostrarTelaInicial();
        });
    }

    // ===================== TELA GESTÃO FINANCEIRA (MVP) =====================

    private void mostrarTelaFinanceiro() {
        Configuracao cfg = configController.getConfiguracao();
        TelaFinanceiroView tela = new TelaFinanceiroView(cfg);

        // carregar empresas no combo
        tela.setEmpresas(financeiroController.getEmpresasAtivas());

        // helper para aplicar filtro e popular
        Runnable aplicarFiltro = () -> {
            // A lista de faturas não depende do período das movimentações.
            // Atualize-a antes de validar o filtro, que pode interromper o fluxo.
            tela.setFaturas(financeiroController.getFaturas());

            String sel = tela.getComboPeriodo().getSelectionModel().getSelectedItem();
            LocalDate inicio, fim;
            String labelPeriodo;
            if ("Hoje".equals(sel)) {
                LocalDate[] r = financeiroController.intervaloHoje();
                inicio = r[0]; fim = r[1];
                labelPeriodo = "Hoje • " + inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else if ("Últimos 7 dias".equals(sel)) {
                LocalDate[] r = financeiroController.intervaloUltimos7Dias();
                inicio = r[0]; fim = r[1];
                labelPeriodo = r[0].format(DateTimeFormatter.ofPattern("dd/MM")) + " a " + r[1].format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else if ("Tudo".equals(sel)) {
                inicio = null; fim = null;
                labelPeriodo = "Todo o período";
            } else if ("Personalizado".equals(sel)) {
                inicio = tela.getDpPeriodoInicio().getValue();
                fim = tela.getDpPeriodoFim().getValue();
                if (inicio == null || fim == null) {
                    tela.setStatus("Informe a data inicial e a data final.", true);
                    return;
                }
                if (inicio.isAfter(fim)) {
                    tela.setStatus("A data inicial não pode ser posterior à data final.", true);
                    return;
                }
                labelPeriodo = inicio.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        + " a " + fim.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } else { // Mês Atual
                LocalDate[] r = financeiroController.intervaloMesAtual();
                inicio = r[0]; fim = r[1];
                labelPeriodo = r[0].format(DateTimeFormatter.ofPattern("MM/yyyy"));
            }
            List<MovimentacaoCaixa> movs = financeiroController.filtrarPorPeriodo(inicio, fim);
            BigDecimal[] totais = financeiroController.calcularTotais(movs);
            Map<Empresa, BigDecimal> porFornecedor = financeiroController.agruparGastosPorFornecedor(movs);
            tela.popularDashboard(totais[0], totais[1], totais[2], porFornecedor, movs, labelPeriodo);
        };

        aplicarFiltro.run();
        tela.getComboPeriodo().setOnAction(e -> {
            tela.atualizarVisibilidadePeriodoPersonalizado();
            aplicarFiltro.run();
        });
        tela.getDpPeriodoInicio().setOnAction(e -> aplicarFiltro.run());
        tela.getDpPeriodoFim().setOnAction(e -> aplicarFiltro.run());
        tela.atualizarVisibilidadePeriodoPersonalizado();

        tela.getBtnVoltar().setOnAction(e -> mostrarTelaInicial());
        tela.setOnMovimentacaoRemoveHandler(movimentacao -> {
            if (movimentacao.getTipo() != pdv.demo4.model.TipoMovimentacao.SAIDA) {
                tela.setStatus("Somente despesas podem ser excluídas por esta lista.", true);
                return;
            }
            String fornecedor = movimentacao.getEmpresa() == null
                    ? ""
                    : " para " + movimentacao.getEmpresa().getNomeRazao();
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    "Deseja excluir a despesa de R$ "
                            + String.format("%.2f", movimentacao.getValor())
                            + fornecedor + "?", ButtonType.YES, ButtonType.NO);
            confirmacao.setTitle("Excluir despesa");
            confirmacao.setHeaderText(null);
            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES
                        && financeiroController.removerMovimentacao(movimentacao.getId())) {
                    aplicarFiltro.run();
                    tela.setStatus("Despesa excluída com sucesso.", false);
                }
            });
        });
        tela.setOnFaturaQuitarHandler(fatura -> {
            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    "Deseja quitar a fatura de R$ "
                            + String.format("%.2f", fatura.getValor())
                            + " para " + fatura.getEmpresa().getNomeRazao() + "?",
                    ButtonType.YES, ButtonType.NO);
            confirmacao.setTitle("Quitar fatura");
            confirmacao.setHeaderText(null);
            confirmacao.showAndWait().ifPresent(resposta -> {
                if (resposta == ButtonType.YES) {
                    try {
                        financeiroController.quitarFatura(fatura.getId());
                        aplicarFiltro.run();
                        tela.setStatus("Fatura quitada e saída registrada no fluxo de caixa.", false);
                    } catch (Exception ex) {
                        tela.setStatus("Erro ao quitar fatura: " + ex.getMessage(), true);
                    }
                }
            });
        });

        // + Empresa (cadastro rápido sem sair da tela - RF-02)
        tela.getBtnNovaEmpresa().setOnAction(e -> {
            javafx.scene.control.TextInputDialog d = new javafx.scene.control.TextInputDialog();
            d.setTitle("Nova Empresa / Fornecedor");
            d.setHeaderText("Cadastrar novo fornecedor");
            d.setContentText("Nome / Razão social:");
            Optional<String> res = d.showAndWait();
            res.ifPresent(nome -> {
                if (nome.trim().isEmpty()) {
                    tela.setStatus("Nome da empresa é obrigatório", true);
                    return;
                }

                javafx.scene.control.TextInputDialog d2 = new javafx.scene.control.TextInputDialog();
                d2.setTitle("CNPJ / CPF (opcional)");
                d2.setHeaderText(null);
                d2.setContentText("CNPJ/CPF (deixe vazio se não tiver):");
                String cnpj = d2.showAndWait().orElse("");

                javafx.scene.control.TextInputDialog d3 = new javafx.scene.control.TextInputDialog();
                d3.setTitle("Telefone da empresa");
                d3.setHeaderText(null);
                d3.setContentText("Telefone (opcional):");
                String telefone = d3.showAndWait().orElse("");

                try {
                    Empresa nova = financeiroController.cadastrarEmpresa(nome, cnpj, telefone);
                    tela.setEmpresas(financeiroController.getEmpresasAtivas());
                    tela.getComboEmpresa().getSelectionModel().select(nova);
                    tela.setStatus("Fornecedor '" + nova.getNomeRazao() + "' cadastrado!", false);
                    aplicarFiltro.run();
                } catch (Exception ex) {
                    tela.setStatus(ex.getMessage(), true);
                }
            });
        });

        // Salvar Saída (RF-03)
        tela.getBtnSalvarSaida().setOnAction(e -> {
            String valorTxt = tela.getTxtValorSaida().getText().replace(",", ".").trim();
            Empresa emp = tela.getComboEmpresa().getSelectionModel().getSelectedItem();
            String desc = tela.getTxtDescricaoSaida().getText().trim();
            if (valorTxt.isEmpty()) { tela.setStatus("Digite o valor da saída", true); return; }
            if (emp == null) { tela.setStatus("Selecione o fornecedor/empresa", true); return; }
            try {
                BigDecimal valor = new BigDecimal(valorTxt);
                if (valor.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
                LocalDateTime dataHora = tela.getDataHoraSaida();
                String descricao = desc.isEmpty() ? "Despesa" : desc;
                if (dataHora.toLocalDate().isAfter(LocalDate.now())) {
                    financeiroController.registrarFatura(valor, emp, dataHora.toLocalDate(), descricao);
                    tela.limparFormSaida();
                    tela.setEmpresas(financeiroController.getEmpresasAtivas());
                    aplicarFiltro.run();
                    tela.setStatus("Fatura de R$ " + String.format("%.2f", valor)
                            + " cadastrada para " + dataHora.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), false);
                    return;
                }
                financeiroController.registrarSaida(valor, emp, dataHora, descricao);
                tela.limparFormSaida();
                tela.setEmpresas(financeiroController.getEmpresasAtivas());
                aplicarFiltro.run();
                tela.setStatus("Saída de R$ " + String.format("%.2f", valor) + " lançada para " + emp.getNomeRazao(), false);
            } catch (NumberFormatException ex) {
                tela.setStatus("Valor inválido! Use números (ex: 200,00)", true);
            } catch (Exception ex) {
                tela.setStatus("Erro: " + ex.getMessage(), true);
            }
        });

        trocarTela(tela.getRoot(), ev -> { if (ev.getCode() == KeyCode.ESCAPE) mostrarTelaInicial(); });
    }

    // ===================== TELA PDV =====================

    /**
     * Mostra a tela do PDV e conecta eventos.
     */
    private void mostrarTelaPDV() {
        Configuracao cfg = configController.getConfiguracao();
        telaPDV = new TelaPDVView(cfg);

        // Handler para remoção de item da lista (bolinha vermelha com x)
        telaPDV.setOnItemRemoveHandler(this::confirmarRemoverItem);

        // Botão voltar
        telaPDV.getBtnVoltar().setOnAction(e -> mostrarTelaInicial());

        // Venda em paralelo abre o modal sem substituir a venda principal.
        telaPDV.getBtnNovaVenda().setOnAction(e -> abrirVendaParalela());

        // Enter no campo de produto vai para o valor
        telaPDV.getTxtProduto().setOnAction(e -> telaPDV.getTxtValor().requestFocus());

        // Botão adicionar item
        telaPDV.getBtnAdicionarItem().setOnAction(e -> adicionarItem());

        // Botão finalizar venda
        telaPDV.getBtnFinalizarVenda().setOnAction(e -> finalizarVenda());

        // Enter no campo de valor adiciona o item
        telaPDV.getTxtValor().setOnAction(e -> adicionarItem());

        // Enter no valor pago calcula o troco
        telaPDV.getTxtValorPago().setOnAction(e -> calcularTrocoAtual());

        // O atalho + retorna o foco ao campo de valor do item.
        telaPDV.getTxtValorPago().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.SUBTRACT) {
                ev.consume();
                finalizarVenda();
            } else if (ev.getCode() == KeyCode.ADD || ev.getCode() == KeyCode.PLUS || "+".equals(ev.getText())) {
                ev.consume();
                focarCampoValor();
            }
        });
        // Bloqueia os caracteres '-' e '+' de serem digitados no campo
        telaPDV.getTxtValorPago().addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, ev -> {
            String c = ev.getCharacter();
            if ("-".equals(c) || "+".equals(c)) {
                ev.consume();
            }
        });
        configurarAtalhoVendaParalela(telaPDV.getTxtProduto());
        configurarAtalhoVendaParalela(telaPDV.getTxtValor());
        configurarAtalhoVendaParalela(telaPDV.getTxtValorPago());

        // Atalhos de teclado no PDV:
        // * -> vai para valor pago
        // - (numérico) -> finaliza a venda
        // + (numérico) -> retorna ao campo de valor
        trocarTela(telaPDV.getRoot(), ev -> {
            if (ev.getCode() == KeyCode.F1) {
                confirmarNovaVenda();
                ev.consume();
            } else if (ev.getCode() == KeyCode.ADD || ev.getCode() == KeyCode.PLUS || "+".equals(ev.getText())) {
                focarCampoValor();
                ev.consume();
            } else if (ev.getCode() == KeyCode.SLASH || "/".equals(ev.getText())) {
                abrirVendaParalela();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F3 || ev.getCode() == KeyCode.MULTIPLY || ev.getCode() == KeyCode.ASTERISK || "*".equals(ev.getText())) {
                telaPDV.getTxtValorPago().requestFocus();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F4 || ev.getCode() == KeyCode.SUBTRACT) {
                finalizarVenda();
                ev.consume();
            } else if (ev.getCode() == KeyCode.ESCAPE) {
                mostrarTelaInicial();
                ev.consume();
            }
        });

        // Inicia uma venda automaticamente
        iniciarNovaVenda();
    }

    /**
     * Confirma se deseja iniciar nova venda.
     */
    private void confirmarNovaVenda() {
        if (vendaController.getVendaAtual() != null && !vendaController.getVendaAtual().getItens().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja iniciar uma nova venda e descartar a atual?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(res -> {
                if (res == ButtonType.YES) iniciarNovaVenda();
            });
        } else {
            iniciarNovaVenda();
        }
    }

    /**
     * Inicia uma nova venda no PDV.
     */
    private void iniciarNovaVenda() {
        vendaController.novaVenda();
        telaPDV.limparTudo();
        telaPDV.getLblVendaId().setText("PDV - Caixa Aberto");
        telaPDV.setStatus("Nova venda iniciada!");
        telaPDV.getTxtValor().requestFocus();
    }

    private void focarCampoValor() {
        if (telaPDV != null) {
            telaPDV.getTxtValor().requestFocus();
            telaPDV.getTxtValor().selectAll();
        }
    }

    private void configurarAtalhoVendaParalela(TextInputControl campo) {
        campo.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.SLASH || "/".equals(ev.getText())) {
                ev.consume();
                abrirVendaParalela();
            }
        });
    }

    private void abrirVendaParalela() {
        if (telaPDV == null || vendaController.getVendaAtual() == null) {
            return;
        }
        if (modalVendaUm != null || modalVendaDois != null) {
            alternarVendaParalela();
            return;
        }

        Venda vendaPrincipal = vendaController.getVendaAtual();
        boolean copiarVendaPrincipal = vendaPrincipal != null && !vendaPrincipal.getItens().isEmpty();
        controllerModalVendaUm = criarControllerModal(copiarVendaPrincipal ? vendaPrincipal : null);
        controllerModalVendaDois = criarControllerModal();
        telaModalVendaUm = criarTelaModal(controllerModalVendaUm, "Venda paralela 1");
        telaModalVendaDois = criarTelaModal(controllerModalVendaDois, "Venda paralela 2");
        if (copiarVendaPrincipal) {
            Venda copia = controllerModalVendaUm.getVendaAtual();
            telaModalVendaUm.atualizarListaItens(copia.getItens());
            telaModalVendaUm.atualizarTotal(copia.getTotalVenda());
            telaModalVendaUm.setStatus("Cópia independente da venda principal. Use / para alternar.");
        }
        modalVendaUm = criarModal(telaModalVendaUm, "Venda Paralela 1");
        modalVendaDois = criarModal(telaModalVendaDois, "Venda Paralela 2");
        posicionarModaisLadoALado();
        modalVendaAtivo = 1;
        modalVendaUm.show();
        modalVendaDois.show();
        Platform.runLater(this::posicionarModaisLadoALado);
        modalVendaUm.toFront();
        telaModalVendaUm.getTxtValor().requestFocus();
    }

    private VendaController criarControllerModal() {
        return criarControllerModal(null);
    }

    private VendaController criarControllerModal(Venda prototipo) {
        VendaController controller = new VendaController();
        controller.novaVenda(prototipo);
        return controller;
    }

    private TelaPDVView criarTelaModal(VendaController controller, String nome) {
        TelaPDVView tela = new TelaPDVView(configController.getConfiguracao(), false, true, false);
        tela.setOnItemRemoveHandler(indice -> removerItemVendaParalela(tela, controller, indice));
        tela.getBtnAdicionarItem().setOnAction(e -> adicionarItemVenda(tela, controller));
        tela.getTxtProduto().setOnAction(e -> tela.getTxtValor().requestFocus());
        tela.getTxtValor().setOnAction(e -> adicionarItemVenda(tela, controller));
        tela.getTxtValorPago().setOnAction(e -> atualizarTrocoVenda(tela, controller));
        tela.getBtnFinalizarVenda().setOnAction(e -> finalizarVendaParalela(tela, controller));

        configurarAtalhoModal(tela.getTxtProduto());
        configurarAtalhoModal(tela.getTxtValor());
        configurarAtalhoModal(tela.getTxtValorPago());
        tela.getTxtValorPago().addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.SUBTRACT) {
                ev.consume();
                finalizarVendaParalela(tela, controller);
            } else if (ev.getCode() == KeyCode.ADD || ev.getCode() == KeyCode.PLUS || "+".equals(ev.getText())) {
                ev.consume();
                tela.getTxtValor().requestFocus();
                tela.getTxtValor().selectAll();
            }
        });
        tela.getTxtValorPago().addEventFilter(KeyEvent.KEY_TYPED, ev -> {
            if ("+".equals(ev.getCharacter()) || "-".equals(ev.getCharacter())) {
                ev.consume();
            }
        });
        tela.setStatus(nome + " ativa. Use / para alternar.");
        return tela;
    }

    private Stage criarModal(TelaPDVView tela, String titulo) {
        Stage modal = new Stage();
        modal.initOwner(stage);
        modal.initModality(javafx.stage.Modality.NONE);
        modal.setTitle(titulo);
        modal.setResizable(false);
        javafx.geometry.Rectangle2D area = Screen.getPrimary().getVisualBounds();
        double largura = Math.min(820, Math.max(520, (area.getWidth() - 36) / 2));
        double altura = Math.min(820, Math.max(620, area.getHeight() - 40));
        modal.setWidth(largura);
        modal.setHeight(altura);
        modal.setMinWidth(largura);
        modal.setMaxWidth(largura);
        modal.setMinHeight(altura);
        modal.setMaxHeight(altura);
        Scene cena = new Scene(tela.getRoot());
        cena.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.SLASH || "/".equals(ev.getText())) {
                ev.consume();
                alternarVendaParalela();
            } else if (ev.getCode() == KeyCode.ESCAPE) {
                ev.consume();
                fecharModalVenda(tela);
            }
        });
        modal.setScene(cena);
        modal.setOnShown(e -> tela.getTxtValor().requestFocus());
        modal.setOnHidden(e -> limparReferenciaModal(tela));
        return modal;
    }

    private void posicionarModaisLadoALado() {
        if (modalVendaUm == null || modalVendaDois == null) {
            return;
        }
        javafx.geometry.Rectangle2D area = Screen.getPrimary().getVisualBounds();
        double espacamento = 12;
        double largura = Math.min(820, Math.max(520, (area.getWidth() - 36) / 2));
        double altura = Math.min(820, Math.max(620, area.getHeight() - 40));
        double grupoLargura = (largura * 2) + espacamento;
        double inicioX = area.getMinX() + Math.max(0, (area.getWidth() - grupoLargura) / 2);
        double inicioY = area.getMinY() + Math.max(0, (area.getHeight() - altura) / 2);

        modalVendaUm.setWidth(largura);
        modalVendaUm.setHeight(altura);
        modalVendaDois.setWidth(largura);
        modalVendaDois.setHeight(altura);
        modalVendaUm.setX(inicioX);
        modalVendaUm.setY(inicioY);
        modalVendaDois.setX(inicioX + largura + espacamento);
        modalVendaDois.setY(inicioY);
    }

    private void configurarAtalhoModal(TextInputControl campo) {
        campo.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.SLASH || "/".equals(ev.getText())) {
                ev.consume();
                alternarVendaParalela();
            }
        });
    }

    private void alternarVendaParalela() {
        if (modalVendaUm == null && modalVendaDois == null) {
            return;
        }
        if (modalVendaAtivo == 1 && modalVendaDois != null) {
            modalVendaAtivo = 2;
            modalVendaDois.toFront();
            telaModalVendaDois.getTxtValor().requestFocus();
            telaModalVendaDois.getTxtValor().selectAll();
        } else if (modalVendaUm != null) {
            modalVendaAtivo = 1;
            modalVendaUm.toFront();
            telaModalVendaUm.getTxtValor().requestFocus();
            telaModalVendaUm.getTxtValor().selectAll();
        }
    }

    private void adicionarItemVenda(TelaPDVView tela, VendaController controller) {
        String texto = tela.getTxtValor().getText().replace(",", ".").trim();
        if (texto.isBlank()) {
            tela.setStatus("Digite o valor do item.");
            tela.getTxtValor().requestFocus();
            return;
        }
        try {
            double valor = Double.parseDouble(texto);
            if (valor <= 0) {
                throw new NumberFormatException();
            }
            controller.adicionarItem(
                    tela.getTxtProduto().getText().isBlank() ? "Diversos" : tela.getTxtProduto().getText().trim(),
                    valor);
            tela.atualizarListaItens(controller.getVendaAtual().getItens());
            tela.atualizarTotal(controller.getVendaAtual().getTotalVenda());
            tela.getTxtValor().clear();
            tela.getTxtProduto().clear();
            tela.getTxtValor().requestFocus();
        } catch (NumberFormatException ex) {
            tela.setStatus("Valor inválido.");
        }
    }

    private void atualizarTrocoVenda(TelaPDVView tela, VendaController controller) {
        if (controller.getVendaAtual() == null) {
            return;
        }
        try {
            double pago = Double.parseDouble(tela.getTxtValorPago().getText().replace(",", ".").trim());
            tela.atualizarTroco(pago - controller.getVendaAtual().getTotalVenda());
        } catch (NumberFormatException ex) {
            tela.setStatus("Valor pago inválido.");
        }
    }

    private void removerItemVendaParalela(TelaPDVView tela, VendaController controller, int indice) {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja remover este item?", ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Remover item");
        confirmacao.setHeaderText(null);
        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.YES) {
                controller.removerItem(indice);
                tela.atualizarListaItens(controller.getVendaAtual().getItens());
                tela.atualizarTotal(controller.getVendaAtual().getTotalVenda());
            }
        });
    }

    private void finalizarVendaParalela(TelaPDVView tela, VendaController controller) {
        Venda vendaAtual = controller.getVendaAtual();
        if (vendaAtual == null || vendaAtual.getItens().isEmpty()) {
            tela.setStatus("Não há itens nesta venda.");
            return;
        }
        String texto = tela.getTxtValorPago().getText().replace(",", ".").trim();
        if (texto.isBlank()) {
            tela.setStatus("Digite o valor pago.");
            tela.getTxtValorPago().requestFocus();
            return;
        }
        try {
            double pago = Double.parseDouble(texto);
            if (pago < vendaAtual.getTotalVenda()) {
                tela.atualizarTroco(pago - vendaAtual.getTotalVenda());
                tela.setStatus("Valor pago insuficiente.");
                return;
            }
            Venda finalizada = controller.finalizarVenda(pago);
            financeiroController.registrarEntradaVenda(finalizada);
            vendaController.recarregar();
            fecharModalVenda(tela);
            mostrarAlertaGrande("Venda Finalizada",
                    "Total: R$ " + String.format("%.2f", finalizada.getTotalVenda())
                            + "\nTroco: R$ " + String.format("%.2f", finalizada.getTroco()));
        } catch (NumberFormatException ex) {
            tela.setStatus("Valor pago inválido.");
        }
    }

    private void fecharModalVenda(TelaPDVView tela) {
        if (tela == telaModalVendaUm && modalVendaUm != null) {
            modalVendaUm.close();
        } else if (tela == telaModalVendaDois && modalVendaDois != null) {
            modalVendaDois.close();
        }
    }

    private void limparReferenciaModal(TelaPDVView tela) {
        if (tela == telaModalVendaUm) {
            modalVendaUm = null;
        } else if (tela == telaModalVendaDois) {
            modalVendaDois = null;
        }
        if (modalVendaUm == null && modalVendaDois == null) {
            telaModalVendaUm = null;
            telaModalVendaDois = null;
            controllerModalVendaUm = null;
            controllerModalVendaDois = null;
        }
    }

    private void abrirVendaParalelaModalLegado() {
        if (telaPDV == null || vendaController.getVendaAtual() == null) return;

        // Cria um novo controller para a venda paralela
        VendaController vendaParalelaController = new VendaController();
        vendaParalelaController.novaVenda();

        // Cria o modal com estilo visual igual ao PDV
        Stage modalStage = new Stage();
        modalStage.initOwner(stage);
        modalStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        modalStage.setTitle("Venda Paralela");
        modalStage.setResizable(false);

        Configuracao cfg = configController.getConfiguracao();
        String corFundo = cfg.getCorPrincipal();
        String corPainel = "derive(" + corFundo + ", -2%)";
        String corBorda = "#81C784";
        String corTexto = "#1B5E20";
        String corBotao = "#2E7D32";
        String corBotaoHover = "#1B5E20";
        String corCampoFundo = "#FAFFF5";
        String corCampoBorda = "#A5D6A7";
        String raio = cfg.isBordaArredondada() ? String.valueOf(cfg.getRaioBorada()) : "10";

        // Componentes do modal
        Label lblTitulo = new Label("VENDA PARALELA");
        lblTitulo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTitulo.setStyle("-fx-text-fill: " + corTexto + ";");

        Label lblValor = new Label("Valor do Item R$:");
        lblValor.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblValor.setStyle("-fx-text-fill: " + corTexto + ";");

        TextField txtValorItem = new TextField();
        txtValorItem.setPromptText("0,00");
        txtValorItem.setFont(Font.font("Arial", 24));
        String estiloCampoNormal = "-fx-background-color:" + corCampoFundo + ";-fx-border-color:" + corCampoBorda + ";-fx-border-radius:" + raio + ";-fx-background-radius:" + raio + ";-fx-padding:8;-fx-text-fill:#000;";
        String estiloCampoFoco = "-fx-background-color:" + corCampoFundo + ";-fx-border-color:#43A047;-fx-border-radius:" + raio + ";-fx-background-radius:" + raio + ";-fx-border-width:2;-fx-padding:7;-fx-text-fill:#000;";
        txtValorItem.setStyle(estiloCampoNormal);
        txtValorItem.focusedProperty().addListener((obs, old, nv) -> txtValorItem.setStyle(nv ? estiloCampoFoco : estiloCampoNormal));
        txtValorItem.addEventFilter(KeyEvent.KEY_TYPED, ev -> {
            String c = ev.getCharacter();
            if (!c.matches("[0-9.,]")) ev.consume();
        });

        Button btnAdicionar = new Button("Adicionar Item");
        btnAdicionar.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        btnAdicionar.setPrefHeight(50);
        btnAdicionar.setMaxWidth(Double.MAX_VALUE);
        String estiloBotaoNormal = "-fx-background-color:" + corBotao + ";-fx-text-fill:#FFF;-fx-background-radius:" + raio + ";-fx-border-radius:" + raio + ";-fx-cursor:hand;";
        String estiloBotaoHover = "-fx-background-color:" + corBotaoHover + ";-fx-text-fill:#FFF;-fx-background-radius:" + raio + ";-fx-border-radius:" + raio + ";-fx-cursor:hand;";
        btnAdicionar.setStyle(estiloBotaoNormal);
        btnAdicionar.setOnMouseEntered(e -> btnAdicionar.setStyle(estiloBotaoHover));
        btnAdicionar.setOnMouseExited(e -> btnAdicionar.setStyle(estiloBotaoNormal));

        // Lista de itens
        ObservableList<ItemVenda> itensObs = FXCollections.observableArrayList();
        ListView<ItemVenda> listaItens = new ListView<>(itensObs);
        listaItens.setStyle("-fx-font-size: 16px; -fx-background-color: " + corCampoFundo + "; -fx-border-color: " + corCampoBorda + "; -fx-border-radius: " + raio + "; -fx-background-radius: " + raio + ";");
        listaItens.setCellFactory(param -> new ListCell<ItemVenda>() {
            @Override
            protected void updateItem(ItemVenda item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%03d - %s | R$ %.2f", getIndex() + 1, item.getNomeProduto(), item.getValor()));
                    setStyle("-fx-text-fill: " + corTexto + ";");
                }
            }
        });
        VBox.setVgrow(listaItens, Priority.ALWAYS);
        listaItens.setPrefHeight(150);
        listaItens.setMaxHeight(180);

        Label lblTotal = new Label("Total: R$ 0,00");
        lblTotal.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTotal.setStyle("-fx-text-fill: " + corTexto + ";");

        Label lblValorPago = new Label("Valor Pago R$:");
        lblValorPago.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lblValorPago.setStyle("-fx-text-fill: " + corTexto + ";");

        TextField txtValorPago = new TextField();
        txtValorPago.setPromptText("0,00");
        txtValorPago.setFont(Font.font("Arial", 24));
        txtValorPago.setStyle(estiloCampoNormal);
        txtValorPago.focusedProperty().addListener((obs, old, nv) -> txtValorPago.setStyle(nv ? estiloCampoFoco : estiloCampoNormal));

        Label lblTroco = new Label("Troco: R$ 0,00");
        lblTroco.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        lblTroco.setStyle("-fx-text-fill: #2E7D32;");

        Button btnFinalizar = new Button("Finalizar Venda (F4)");
        btnFinalizar.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        btnFinalizar.setPrefHeight(60);
        btnFinalizar.setMaxWidth(Double.MAX_VALUE);
        btnFinalizar.setStyle(estiloBotaoNormal);
        btnFinalizar.setOnMouseEntered(e -> btnFinalizar.setStyle(estiloBotaoHover));
        btnFinalizar.setOnMouseExited(e -> btnFinalizar.setStyle(estiloBotaoNormal));

        Button btnFechar = new Button("Fechar (ESC)");
        btnFechar.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        btnFechar.setPrefHeight(45);
        btnFechar.setMaxWidth(Double.MAX_VALUE);
        btnFechar.setStyle("-fx-background-color:#FFF;-fx-text-fill:#2E7D32;-fx-background-radius:" + raio + ";-fx-border-radius:" + raio + ";-fx-cursor:hand;-fx-border-color:#A5D6A7;");

        // Ações
        Runnable atualizarTotais = () -> {
            Venda v = vendaParalelaController.getVendaAtual();
            if (v != null) {
                lblTotal.setText("Total: R$ " + String.format("%.2f", v.getTotalVenda()));
                String pagoTxt = txtValorPago.getText().replace(",", ".").trim();
                if (!pagoTxt.isEmpty()) {
                    try {
                        double pago = Double.parseDouble(pagoTxt);
                        double troco = pago - v.getTotalVenda();
                        lblTroco.setText("Troco: R$ " + String.format("%.2f", troco));
                        lblTroco.setStyle(troco < 0 ? "-fx-text-fill:#C62828;" : "-fx-text-fill:#2E7D32;");
                    } catch (NumberFormatException ignored) {}
                }
            }
        };

        Runnable adicionarItem = () -> {
            String valorTxt = txtValorItem.getText().replace(",", ".").trim();
            if (valorTxt.isEmpty()) return;
            try {
                double valor = Double.parseDouble(valorTxt);
                if (valor <= 0) return;
                vendaParalelaController.adicionarItem("Diversos", valor);
                itensObs.setAll(vendaParalelaController.getVendaAtual().getItens());
                txtValorItem.clear();
                txtValorItem.requestFocus();
                atualizarTotais.run();
            } catch (NumberFormatException ignored) {}
        };

        btnAdicionar.setOnAction(e -> adicionarItem.run());
        txtValorItem.setOnAction(e -> adicionarItem.run());

        txtValorPago.setOnAction(e -> atualizarTotais.run());

        Runnable finalizarVendaParalela = () -> {
            Venda v = vendaParalelaController.getVendaAtual();
            if (v == null || v.getItens().isEmpty()) return;
            String pagoTxt = txtValorPago.getText().replace(",", ".").trim();
            if (pagoTxt.isEmpty()) {
                txtValorPago.requestFocus();
                return;
            }
            try {
                double pago = Double.parseDouble(pagoTxt);
                if (pago < v.getTotalVenda()) {
                    lblTroco.setText("Troco: R$ -" + String.format("%.2f", v.getTotalVenda() - pago));
                    lblTroco.setStyle("-fx-text-fill:#C62828;");
                    txtValorPago.requestFocus();
                    return;
                }
                Venda finalizada = vendaParalelaController.finalizarVenda(pago);
                if (finalizada == null) return;
                financeiroController.registrarEntradaVenda(finalizada);
                modalStage.close();
                mostrarAlertaGrande("Venda Paralela Finalizada",
                    "Total: R$ " + String.format("%.2f", finalizada.getTotalVenda()) +
                    "\nTroco: R$ " + String.format("%.2f", finalizada.getTroco()));
            } catch (NumberFormatException ignored) {}
        };

        btnFinalizar.setOnAction(e -> finalizarVendaParalela.run());
        btnFechar.setOnAction(e -> modalStage.close());

        // Atalhos no modal
        txtValorItem.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.F4 || ev.getCode() == KeyCode.SUBTRACT) {
                ev.consume();
                finalizarVendaParalela.run();
            } else if (ev.getCode() == KeyCode.ESCAPE) {
                ev.consume();
                modalStage.close();
            }
        });
        txtValorPago.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.F4 || ev.getCode() == KeyCode.SUBTRACT) {
                ev.consume();
                finalizarVendaParalela.run();
            } else if (ev.getCode() == KeyCode.ESCAPE) {
                ev.consume();
                modalStage.close();
            }
        });
        listaItens.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ESCAPE) {
                ev.consume();
                modalStage.close();
            }
        });
        btnFinalizar.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ESCAPE) {
                ev.consume();
                modalStage.close();
            }
        });
        btnFechar.addEventFilter(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ESCAPE) {
                ev.consume();
                modalStage.close();
            }
        });

        // Layout do modal
        VBox conteudo = new VBox(12,
            lblTitulo,
            new Separator(),
            new HBox(10, lblValor, txtValorItem, btnAdicionar),
            listaItens,
            lblTotal,
            new HBox(10, lblValorPago, txtValorPago),
            lblTroco,
            new Separator(),
            btnFinalizar,
            btnFechar
        );
        HBox.setHgrow(txtValorItem, Priority.ALWAYS);
        HBox.setHgrow(txtValorPago, Priority.ALWAYS);
        HBox.setHgrow(btnAdicionar, Priority.ALWAYS);

        conteudo.setPadding(new Insets(16));
        conteudo.setStyle("-fx-background-color: " + corFundo + "; -fx-background-radius: 15; -fx-border-radius: 15; -fx-border-color: " + corBorda + "; -fx-border-width: 2;");
        double larguraTela = Screen.getPrimary().getVisualBounds().getWidth();
        double larguraModal = Math.max(480, Math.min(720, larguraTela * 0.40));
        conteudo.setPrefWidth(larguraModal);
        conteudo.setMinWidth(larguraModal);
        conteudo.setMaxWidth(larguraModal);

        StackPane rootModal = new StackPane(conteudo);
        rootModal.setStyle("-fx-background-color: transparent;");
        rootModal.setPadding(new Insets(20));

        Scene scene = new Scene(rootModal);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        modalStage.setScene(scene);
        modalStage.setWidth(larguraModal + 32);
        modalStage.sizeToScene();
        modalStage.centerOnScreen();

        // Foco inicial
        modalStage.setOnShown(e -> txtValorItem.requestFocus());

        modalStage.showAndWait();
    }

    /**
     * Adiciona um item à venda atual.
     */
    private void adicionarItem() {
        if (vendaController.getVendaAtual() == null) {
            telaPDV.setStatus("Inicie uma nova venda primeiro! (F1)");
            return;
        }

        String valorTexto = telaPDV.getTxtValor().getText().replace(",", ".").trim();
        if (valorTexto.isEmpty()) {
            telaPDV.setStatus("Digite o valor do item!");
            return;
        }

        try {
            double valor = Double.parseDouble(valorTexto);
            if (valor <= 0) {
                telaPDV.setStatus("O valor deve ser maior que zero!");
                return;
            }
            String produto = telaPDV.getTxtProduto().getText();
            vendaController.adicionarItem(produto, valor);

            // Atualiza a tela
            Venda atual = vendaController.getVendaAtual();
            telaPDV.atualizarListaItens(atual.getItens());
            telaPDV.atualizarTotal(atual.getTotalVenda());
            telaPDV.limparCamposEntrada();
            telaPDV.getTxtValor().requestFocus();
            telaPDV.setStatus("Item adicionado!");

        } catch (NumberFormatException ex) {
            telaPDV.setStatus("Valor inválido! Use números (ex: 15,50)");
        }
    }

    // Remover item da venda com confirmação na tela
    private void confirmarRemoverItem(int indice) {
        Venda atual = vendaController.getVendaAtual();
        if (atual == null || indice < 0 || indice >= atual.getItens().size()) {
            return;
        }
        pdv.demo4.model.ItemVenda item = atual.getItens().get(indice);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Deseja realmente remover o item '" + item.getNomeProduto() + " - R$ " + String.format("%.2f", item.getValor()) + "' da venda?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirmar Exclusão de Item");
        alert.setHeaderText(null);
        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.YES) {
            vendaController.removerItem(indice);
            Venda vAtualizada = vendaController.getVendaAtual();
            telaPDV.atualizarListaItens(vAtualizada.getItens());
            telaPDV.atualizarTotal(vAtualizada.getTotalVenda());
            calcularTrocoAtual();
            telaPDV.setStatus("Item '" + item.getNomeProduto() + "' removido da soma!");
        }
    }

    /**
     * Apenas calcula e mostra o troco na tela sem finalizar a venda (acionado pelo Enter no valor pago).
     */
    private void calcularTrocoAtual() {
        Venda atual = vendaController.getVendaAtual();
        if (atual == null) return;
        
        String pagoTexto = telaPDV.getTxtValorPago().getText().replace(",", ".").trim();
        if (pagoTexto.isEmpty()) return;
        
        try {
            double valorPago = Double.parseDouble(pagoTexto);
            double troco = valorPago - atual.getTotalVenda();
            telaPDV.atualizarTroco(troco);
            if (troco >= 0) {
                telaPDV.setStatus("Troco calculado! Pressione F4 para finalizar.");
            } else {
                telaPDV.setStatus("ATENÇÃO: Valor pago insuficiente! Faltam R$ " + String.format("%.2f", Math.abs(troco)));
            }
        } catch (NumberFormatException ex) {
            telaPDV.setStatus("Valor pago inválido!");
        }
    }

    /**
     * Finaliza a venda, calcula troco e limpa para nova venda.
     */
    private void finalizarVenda() {
        Venda atual = vendaController.getVendaAtual();
        if (atual == null || atual.getItens().isEmpty()) {
            telaPDV.setStatus("Não há itens na venda!");
            return;
        }

        String pagoTexto = telaPDV.getTxtValorPago().getText().replace(",", ".").trim();

        // Se o campo de valor pago estiver vazio, foca nele (F3)
        if (pagoTexto.isEmpty()) {
            telaPDV.setStatus("Digite o valor pago pelo cliente! (F3)");
            telaPDV.getTxtValorPago().requestFocus();
            return;
        }

        try {
            double valorPago = Double.parseDouble(pagoTexto);
            Venda finalizada = vendaController.finalizarVenda(valorPago);

            if (finalizada != null) {
                // RF-01: entrada automática no fluxo de caixa
                financeiroController.registrarEntradaVenda(finalizada);
                telaPDV.atualizarTroco(finalizada.getTroco());

                if (finalizada.getTroco() >= 0) {
                    mostrarAlertaGrande("Venda Finalizada", 
                        "Total: R$ " + String.format("%.2f", finalizada.getTotalVenda()) + 
                        "\nTroco: R$ " + String.format("%.2f", finalizada.getTroco()));
                    iniciarNovaVenda();
                } else {
                    telaPDV.setStatus("ATENÇÃO: Valor pago insuficiente! Faltam R$ "
                            + String.format("%.2f", Math.abs(finalizada.getTroco())));
                }
            }
        } catch (NumberFormatException ex) {
            telaPDV.setStatus("Valor pago inválido!");
        }
    }

    // ===================== TELA HISTÓRICO =====================

    private void mostrarTelaHistorico() {
        Configuracao cfg = configController.getConfiguracao();
        TelaHistoricoView telaHist = new TelaHistoricoView(cfg);
        telaHist.getBtnVoltar().setOnAction(e -> mostrarTelaInicial());

        Runnable recarregarHistorico = () -> {
            vendaController.recarregar();
            telaHist.carregarVendas(vendaController.getTodasVendas());
        };
        recarregarHistorico.run();

        telaHist.getBtnEditar().setOnAction(e -> {
            Venda vendaSelecionada = telaHist.getListaVendas().getSelectionModel().getSelectedItem();
            if (vendaSelecionada == null) {
                mostrarAlerta("Seleção", "Selecione uma venda para editar.");
                return;
            }
            editarVenda(vendaSelecionada, recarregarHistorico);
        });

        telaHist.getBtnExcluir().setOnAction(e -> {
            Venda vendaSelecionada = telaHist.getListaVendas().getSelectionModel().getSelectedItem();
            if (vendaSelecionada == null) {
                mostrarAlerta("Seleção", "Selecione uma venda para excluir.");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Excluir venda");
            confirmacao.setHeaderText("Deseja realmente excluir esta venda?");
            confirmacao.setContentText("Venda #" + vendaSelecionada.getId() + " - Total: R$ " + String.format("%.2f", vendaSelecionada.getTotalVenda()));

            confirmacao.showAndWait().ifPresent(res -> {
                if (res == ButtonType.OK && vendaController.removerVenda(vendaSelecionada)) {
                    financeiroController.removerEntradaVenda(vendaSelecionada.getId());
                    financeiroController.recarregar();
                    recarregarHistorico.run();
                    mostrarAlerta("Sucesso", "Venda excluída com sucesso.");
                }
            });
        });

        recarregarHistorico.run();
        trocarTela(telaHist.getRoot(), ev -> { if (ev.getCode() == KeyCode.ESCAPE) mostrarTelaInicial(); });
    }

    /**
     * Abre o PDV para editar uma venda existente.
     */
    private void editarVenda(Venda vendaParaEditar, Runnable recarregarHistorico) {
        Configuracao cfg = configController.getConfiguracao();
        telaPDV = new TelaPDVView(cfg);

        // Handler para remoção de item da lista
        telaPDV.setOnItemRemoveHandler(this::confirmarRemoverItem);

        // Botão voltar
        telaPDV.getBtnVoltar().setOnAction(e -> mostrarTelaHistorico());

        // Venda em paralelo também permanece independente durante a edição.
        telaPDV.getBtnNovaVenda().setOnAction(e -> abrirVendaParalela());

        // Enter no campo de produto vai para o valor
        telaPDV.getTxtProduto().setOnAction(e -> telaPDV.getTxtValor().requestFocus());

        // Botão adicionar item
        telaPDV.getBtnAdicionarItem().setOnAction(e -> adicionarItem());

        // Botão finalizar venda - atualiza a venda existente
        telaPDV.getBtnFinalizarVenda().setOnAction(e -> finalizarEdicaoVenda(vendaParaEditar, recarregarHistorico));

        // Enter no campo de valor adiciona o item
        telaPDV.getTxtValor().setOnAction(e -> adicionarItem());

        // Enter no valor pago calcula o troco
        telaPDV.getTxtValorPago().setOnAction(e -> calcularTrocoAtual());

        // Atalhos de teclado
        telaPDV.getTxtValorPago().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.SUBTRACT) {
                ev.consume();
                finalizarEdicaoVenda(vendaParaEditar, recarregarHistorico);
            } else if (ev.getCode() == KeyCode.ADD || ev.getCode() == KeyCode.PLUS || "+".equals(ev.getText())) {
                ev.consume();
                focarCampoValor();
            }
        });
        telaPDV.getTxtValorPago().addEventFilter(javafx.scene.input.KeyEvent.KEY_TYPED, ev -> {
            String c = ev.getCharacter();
            if ("-".equals(c) || "+".equals(c)) {
                ev.consume();
            }
        });
        configurarAtalhoVendaParalela(telaPDV.getTxtProduto());
        configurarAtalhoVendaParalela(telaPDV.getTxtValor());
        configurarAtalhoVendaParalela(telaPDV.getTxtValorPago());

        trocarTela(telaPDV.getRoot(), ev -> {
            if (ev.getCode() == KeyCode.F1) {
                if (vendaController.getVendaAtual() != null && !vendaController.getVendaAtual().getItens().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja iniciar uma nova venda e descartar a edição atual?", ButtonType.YES, ButtonType.NO);
                    alert.showAndWait().ifPresent(res -> {
                        if (res == ButtonType.YES) iniciarNovaVenda();
                    });
                } else {
                    iniciarNovaVenda();
                }
                ev.consume();
            } else if (ev.getCode() == KeyCode.ADD || ev.getCode() == KeyCode.PLUS || "+".equals(ev.getText())) {
                focarCampoValor();
                ev.consume();
            } else if (ev.getCode() == KeyCode.SLASH || "/".equals(ev.getText())) {
                abrirVendaParalela();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F3 || ev.getCode() == KeyCode.MULTIPLY || ev.getCode() == KeyCode.ASTERISK || "*".equals(ev.getText())) {
                telaPDV.getTxtValorPago().requestFocus();
                ev.consume();
            } else if (ev.getCode() == KeyCode.F4 || ev.getCode() == KeyCode.SUBTRACT) {
                finalizarEdicaoVenda(vendaParaEditar, recarregarHistorico);
                ev.consume();
            } else if (ev.getCode() == KeyCode.ESCAPE) {
                mostrarTelaHistorico();
                ev.consume();
            }
        });

        // Carrega os dados da venda para edição
        vendaController.novaVenda(); // Cria uma venda temporária
        Venda tempVenda = vendaController.getVendaAtual();
        // Copia os itens da venda original
        for (pdv.demo4.model.ItemVenda item : vendaParaEditar.getItens()) {
            tempVenda.adicionarItem(new pdv.demo4.model.ItemVenda(item.getNomeProduto(), item.getValor()));
        }
        // Atualiza a tela
        telaPDV.atualizarListaItens(tempVenda.getItens());
        telaPDV.atualizarTotal(tempVenda.getTotalVenda());
        telaPDV.getLblVendaId().setText("Editando Venda #" + vendaParaEditar.getId());
        telaPDV.setStatus("Editando venda #" + vendaParaEditar.getId() + " - F4 para salvar alterações");
        telaPDV.getTxtValor().requestFocus();

        // Guarda o ID da venda original para atualizar depois
        telaPDV.getRoot().getProperties().put("vendaEditandoId", vendaParaEditar.getId());
    }

    /**
     * Finaliza a edição de uma venda, atualizando a venda original.
     */
    private void finalizarEdicaoVenda(Venda vendaOriginal, Runnable recarregarHistorico) {
        Venda vendaEditando = vendaController.getVendaAtual();
        if (vendaEditando == null || vendaEditando.getItens().isEmpty()) {
            telaPDV.setStatus("Não há itens na venda!");
            return;
        }

        String pagoTexto = telaPDV.getTxtValorPago().getText().replace(",", ".").trim();

        if (pagoTexto.isEmpty()) {
            telaPDV.setStatus("Digite o valor pago pelo cliente! (F3)");
            telaPDV.getTxtValorPago().requestFocus();
            return;
        }

        try {
            double valorPago = Double.parseDouble(pagoTexto);
            vendaEditando.finalizarVenda(valorPago);

            if (vendaEditando.getTroco() < 0) {
                telaPDV.setStatus("ATENÇÃO: Valor pago insuficiente! Faltam R$ " + String.format("%.2f", Math.abs(vendaEditando.getTroco())));
                return;
            }

            // Atualiza a venda original com os novos dados
            vendaOriginal.getItens().clear();
            for (pdv.demo4.model.ItemVenda item : vendaEditando.getItens()) {
                vendaOriginal.adicionarItem(new pdv.demo4.model.ItemVenda(item.getNomeProduto(), item.getValor()));
            }
            vendaOriginal.setValorPago(vendaEditando.getValorPago());
            vendaOriginal.atualizarPagamento(vendaEditando.getValorPago());

            // Salva as alterações
            boolean ok = vendaController.atualizarVenda(vendaOriginal);
            if (ok) {
                financeiroController.recarregar();
                recarregarHistorico.run();
                mostrarAlertaGrande("Venda Atualizada",
                    "Venda #" + vendaOriginal.getId() + " atualizada!\n" +
                    "Total: R$ " + String.format("%.2f", vendaOriginal.getTotalVenda()) +
                    "\nTroco: R$ " + String.format("%.2f", vendaOriginal.getTroco()));
                mostrarTelaHistorico();
            } else {
                telaPDV.setStatus("Erro ao atualizar a venda!");
            }

        } catch (NumberFormatException ex) {
            telaPDV.setStatus("Valor pago inválido!");
        }
    }

    // ===================== TELA CONTA =====================

    private void mostrarTelaConta() {
        Configuracao cfg = configController.getConfiguracao();
        TelaContaView telaConta = new TelaContaView(cfg);
        telaConta.getBtnVoltar().setOnAction(e -> mostrarTelaInicial());

        trocarTela(telaConta.getRoot(), ev -> { if (ev.getCode() == KeyCode.ESCAPE) mostrarTelaInicial(); });
    }

    // ===================== TELA CONFIGURAÇÕES =====================

    private void mostrarTelaConfig() {
        Configuracao cfg = configController.getConfiguracao();
        TelaConfigView telaConfig = new TelaConfigView(cfg);
        telaConfig.getBtnVoltar().setOnAction(e -> mostrarTelaInicial());

        // Botão salvar configurações
        telaConfig.getBtnSalvar().setOnAction(e -> {
            cfg.setNomeLoja(telaConfig.getTxtNomeLoja().getText());
            cfg.setCorPrincipal(telaConfig.getTxtCorPrincipal().getText());
            cfg.setCorBotoes(telaConfig.getTxtCorBotoes().getText());
            cfg.setCorTextoBotoes(telaConfig.getTxtCorTextoBotoes().getText());
            cfg.setLinkSite(telaConfig.getTxtLinkSite().getText());
            cfg.setBordaArredondada(telaConfig.getChkBordaArredondada().isSelected());
            cfg.setAnimacoesAtivas(telaConfig.getChkAnimacoes().isSelected());
            try {
                cfg.setRaioBorada(Double.parseDouble(telaConfig.getTxtRaioBorda().getText()));
            } catch (NumberFormatException ex) { /* mantém o atual */ }
            configController.salvar();
            mostrarAlerta("Sucesso", "Configurações salvas!");
            mostrarTelaInicial();
        });

        // Botão restaurar padrão
        telaConfig.getBtnRestaurar().setOnAction(e -> {
            configController.restaurarPadrao();
            mostrarAlerta("Sucesso", "Configurações restauradas!");
            mostrarTelaInicial();
        });

        trocarTela(telaConfig.getRoot(), ev -> { if (ev.getCode() == KeyCode.ESCAPE) mostrarTelaInicial(); });
    }

    // ===================== TELA BACKUP =====================

    private void mostrarTelaBackup() {
        Configuracao cfg = configController.getConfiguracao();
        TelaBackupView telaBackup = new TelaBackupView(cfg);
        telaBackup.getBtnVoltar().setOnAction(e -> mostrarTelaInicial());

        telaBackup.getBtnCarregarBackup().setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Selecionar backup do sistema");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup Elmix", "*.elmix"));
            File arquivo = fc.showOpenDialog(stage);
            if (arquivo == null) return;
            try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(arquivo))) {
                Object objeto = entrada.readObject();
                if (!(objeto instanceof BackupCompleto backup)) {
                    throw new IOException("Arquivo não é um backup Elmix válido.");
                }
                vendaController.restaurarVendas(backup.getVendas());
                if (backup.getDadosFinanceiros() != null) {
                    financeiroController.restaurarDados(backup.getDadosFinanceiros());
                }
                if (backup.getConfiguracao() != null) {
                    configController.restaurar(backup.getConfiguracao());
                }
                telaBackup.getLblStatus().setText("Backup restaurado com sucesso.");
                mostrarAlerta("Sucesso", "Vendas, despesas, empresas e demais dados foram restaurados.");
            } catch (IOException | ClassNotFoundException | RuntimeException ex) {
                telaBackup.getLblStatus().setText("Erro ao restaurar o backup.");
                mostrarAlerta("Erro", "Não foi possível restaurar o backup:\n" + ex.getMessage());
            }
        });

        // Botão gerar backup
        telaBackup.getBtnGerarBackup().setOnAction(e -> {
            LocalDate inicio = telaBackup.getDpInicio().getValue();
            LocalDate fim = telaBackup.getDpFim().getValue();

            if (inicio == null || fim == null) {
                telaBackup.getLblStatus().setText("Selecione as datas!");
                return;
            }

            // Mostra preview
            String relatorio = gerarRelatorioBackupCompleto(inicio, fim);
            telaBackup.getTxtPreview().setText(relatorio);

            // Salva em arquivo TXT
            FileChooser fc = new FileChooser();
            fc.setTitle("Salvar Backup");
            fc.setInitialFileName("backup_elmix_" + inicio + "_" + fim + ".txt");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo de Texto", "*.txt"));
            File arquivo = fc.showSaveDialog(stage);

            if (arquivo != null) {
                boolean ok = salvarRelatorioBackupCompleto(arquivo, relatorio);
                telaBackup.getLblStatus().setText(ok ? "Backup salvo em: " + arquivo.getName() : "Erro ao salvar!");
            }

            FileChooser pacoteChooser = new FileChooser();
            pacoteChooser.setTitle("Salvar backup completo");
            pacoteChooser.setInitialFileName("backup_elmix_" + LocalDate.now() + ".elmix");
            pacoteChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup Elmix", "*.elmix"));
            File pacote = pacoteChooser.showSaveDialog(stage);
            if (pacote != null) {
                try (ObjectOutputStream saida = new ObjectOutputStream(new FileOutputStream(pacote))) {
                    BackupCompleto backup = new BackupCompleto();
                    backup.setVendas(vendaController.getTodasVendas());
                    backup.setDadosFinanceiros(financeiroController.getDadosParaBackup());
                    backup.setConfiguracao(configController.getConfiguracao());
                    saida.writeObject(backup);
                    telaBackup.getLblStatus().setText("Backup completo salvo em: " + pacote.getName());
                } catch (IOException ex) {
                    telaBackup.getLblStatus().setText("Erro ao salvar backup completo.");
                    mostrarAlerta("Erro", "Não foi possível salvar o backup completo:\n" + ex.getMessage());
                }
            }
        });

        trocarTela(telaBackup.getRoot(), ev -> { if (ev.getCode() == KeyCode.ESCAPE) mostrarTelaInicial(); });
    }

    private String gerarRelatorioBackupCompleto(LocalDate inicio, LocalDate fim) {
        StringBuilder relatorio = new StringBuilder(vendaController.gerarRelatorioBackup(inicio, fim));
        relatorio.append("\n\n=== EMPRESAS / FORNECEDORES ===\n");
        financeiroController.getEmpresas().forEach(empresa ->
                relatorio.append("- ").append(empresa.getNomeRazao())
                        .append(" | CNPJ/CPF: ").append(empresa.getCnpjCpf())
                        .append(" | Telefone: ").append(empresa.getTelefone())
                        .append(" | Ativa: ").append(empresa.isAtivo()).append('\n'));
        relatorio.append("\n=== MOVIMENTAÇÕES FINANCEIRAS ===\n");
        financeiroController.filtrarPorPeriodo(inicio, fim).forEach(movimentacao ->
                relatorio.append("- ").append(movimentacao.getTipo())
                        .append(" | ").append(movimentacao.getDataFormatada())
                        .append(" | R$ ").append(movimentacao.getValor())
                        .append(" | ").append(movimentacao.getDescricao())
                        .append(movimentacao.getEmpresa() == null ? "" : " | Empresa: " + movimentacao.getEmpresa().getNomeRazao())
                        .append('\n'));
        return relatorio.toString();
    }

    private boolean salvarRelatorioBackupCompleto(File arquivo, String relatorio) {
        try {
            Files.writeString(arquivo.toPath(), relatorio, StandardCharsets.UTF_8);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    // ===================== FECHAR CAIXA =====================

    /**
     * Fecha o caixa do dia gerando um relatório TXT.
     */
    private void fecharCaixa() {
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Fechar Caixa");
        confirmacao.setHeaderText("Deseja fechar o caixa do dia?");
        confirmacao.setContentText("Um relatório TXT será gerado com as vendas de hoje.");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            FileChooser fc = new FileChooser();
            fc.setTitle("Salvar Fechamento de Caixa");
            fc.setInitialFileName("fechamento_" + LocalDate.now() + ".txt");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Arquivo de Texto", "*.txt"));
            File arquivo = fc.showSaveDialog(stage);

            if (arquivo != null) {
                boolean ok = vendaController.salvarRelatorioFechamento(arquivo.getAbsolutePath());
                if (ok) {
                    mostrarAlerta("Sucesso", "Caixa fechado!\nRelatório salvo em: " + arquivo.getName());
                } else {
                    mostrarAlerta("Erro", "Não foi possível salvar o relatório.");
                }
            }
        }
    }

    // ===================== UTILITÁRIOS =====================

    /**
     * Mostra um alerta simples.
     */
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void mostrarAlertaGrande(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.getDialogPane().setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");
        alerta.showAndWait();
    }
}
