package pdv.demo4.model;

import java.io.Serializable;

/**
 * Guarda as configurações do sistema (cores, nome da loja, etc).
 * Persistido em arquivo .ser para manter entre sessões.
 */
public class Configuracao implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nomeLoja;           // Nome da loja exibido no header
    private String corPrincipal;       // Cor principal do sistema (hex)
    private String corBotoes;          // Cor dos botões (hex)
    private String corTextoBotoes;     // Cor do texto dos botões (hex)
    private boolean temaEscuro;        // Se o tema escuro está ativo
    private boolean bordaArredondada;  // Se os botões têm borda arredondada
    private boolean animacoesAtivas;   // Se as animações estão ativas
    private String linkSite;           // Link para o site da loja
    private double raioBorada;         // Raio da borda arredondada
    // Wrapper para detectar configuração antiga desserializada sem este campo.
    private Integer diasAlertaFatura = 2;

    // Construtor com valores padrão
    public Configuracao() {
        this.nomeLoja = "ELMIX";
        this.corPrincipal = "#C8E6C9";       // Verde bem suave
        this.corBotoes = "#FFFFFF";           // Branco
        this.corTextoBotoes = "#2E7D32";      // Verde escuro
        this.temaEscuro = false;
        this.bordaArredondada = true;
        this.animacoesAtivas = true;
        this.linkSite = "";
        this.raioBorada = 12.0;
    }

    // --- Getters e Setters ---

    public String getNomeLoja() { return nomeLoja; }
    public void setNomeLoja(String nomeLoja) { this.nomeLoja = nomeLoja; }

    public String getCorPrincipal() { return corPrincipal; }
    public void setCorPrincipal(String corPrincipal) { this.corPrincipal = corPrincipal; }

    public String getCorBotoes() { return corBotoes; }
    public void setCorBotoes(String corBotoes) { this.corBotoes = corBotoes; }

    public String getCorTextoBotoes() { return corTextoBotoes; }
    public void setCorTextoBotoes(String corTextoBotoes) { this.corTextoBotoes = corTextoBotoes; }

    public boolean isTemaEscuro() { return temaEscuro; }
    public void setTemaEscuro(boolean temaEscuro) { this.temaEscuro = temaEscuro; }

    public boolean isBordaArredondada() { return bordaArredondada; }
    public void setBordaArredondada(boolean bordaArredondada) { this.bordaArredondada = bordaArredondada; }

    public boolean isAnimacoesAtivas() { return animacoesAtivas; }
    public void setAnimacoesAtivas(boolean animacoesAtivas) { this.animacoesAtivas = animacoesAtivas; }

    public String getLinkSite() { return linkSite; }
    public void setLinkSite(String linkSite) { this.linkSite = linkSite; }

    public double getRaioBorada() { return raioBorada; }
    public void setRaioBorada(double raioBorada) { this.raioBorada = raioBorada; }

    /** Número de dias de antecedência; zero desativa apenas o lembrete pré-vencimento. */
    public int getDiasAlertaFatura() {
        return diasAlertaFatura == null ? 2 : diasAlertaFatura;
    }

    public void setDiasAlertaFatura(int diasAlertaFatura) {
        if (diasAlertaFatura < 0 || diasAlertaFatura > 365) {
            throw new IllegalArgumentException(
                    "Os dias de antecedência devem estar entre 0 e 365");
        }
        this.diasAlertaFatura = diasAlertaFatura;
    }
}
