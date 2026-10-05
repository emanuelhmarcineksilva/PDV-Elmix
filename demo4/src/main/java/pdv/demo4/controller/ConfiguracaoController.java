package pdv.demo4.controller;

import pdv.demo4.model.Configuracao;
import pdv.demo4.util.PersistenciaUtil;

/**
 * Controlador de configurações.
 * Carrega e salva as preferências do sistema.
 */
public class ConfiguracaoController {

    private static final String ARQUIVO_CONFIG = "configuracao.ser"; // Arquivo de persistência
    private Configuracao configuracao; // Configuração atual

    public ConfiguracaoController() {
        // Tenta carregar configuração salva, senão cria uma nova com valores padrão
        Object obj = PersistenciaUtil.carregar(ARQUIVO_CONFIG);
        if (obj instanceof Configuracao) {
            this.configuracao = (Configuracao) obj;
        } else {
            this.configuracao = new Configuracao();
            salvar();
        }
    }

    /**
     * Retorna a configuração atual.
     */
    public Configuracao getConfiguracao() {
        return configuracao;
    }

    /**
     * Salva a configuração no arquivo .ser
     */
    public void salvar() {
        PersistenciaUtil.salvar(configuracao, ARQUIVO_CONFIG);
    }

    /**
     * Restaura as configurações para o padrão.
     */
    public void restaurarPadrao() {
        this.configuracao = new Configuracao();
        salvar();
    }

    public void restaurar(Configuracao novaConfiguracao) {
        if (novaConfiguracao == null) {
            throw new IllegalArgumentException("Configuração ausente no backup");
        }
        this.configuracao = novaConfiguracao;
        salvar();
    }
}
