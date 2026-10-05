package pdv.demo4.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para salvar e carregar objetos em arquivos .ser
 * Funciona como o "banco de dados" do sistema via serialização Java.
 */
public class PersistenciaUtil {

    // Pasta onde ficam os dados salvos
    private static final String PASTA_DADOS = "dados_elmix";

    /**
     * Garante que a pasta de dados exista.
     */
    private static void garantirPasta() {
        File pasta = new File(PASTA_DADOS);
        if (!pasta.exists()) {
            pasta.mkdirs();
        }
    }

    /**
     * Salva um objeto qualquer em um arquivo .ser
     *
     * @param objeto    O objeto a ser salvo
     * @param nomeArquivo Nome do arquivo (ex: "configuracao.ser")
     */
    public static void salvar(Object objeto, String nomeArquivo) {
        garantirPasta();
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(PASTA_DADOS + File.separator + nomeArquivo))) {
            oos.writeObject(objeto);
        } catch (IOException e) {
            System.err.println("Erro ao salvar " + nomeArquivo + ": " + e.getMessage());
        }
    }

    /**
     * Carrega um objeto de um arquivo .ser
     *
     * @param nomeArquivo Nome do arquivo
     * @return O objeto carregado, ou null se não existir
     */
    public static Object carregar(String nomeArquivo) {
        garantirPasta();
        File arquivo = new File(PASTA_DADOS + File.separator + nomeArquivo);
        if (!arquivo.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(arquivo))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar " + nomeArquivo + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Salva uma lista de objetos.
     */
    @SuppressWarnings("unchecked")
    public static <T> void salvarLista(List<T> lista, String nomeArquivo) {
        salvar(new ArrayList<>(lista), nomeArquivo);
    }

    /**
     * Carrega uma lista de objetos. Retorna lista vazia se não existir.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> carregarLista(String nomeArquivo) {
        Object obj = carregar(nomeArquivo);
        if (obj instanceof List<?>) {
            return (List<T>) obj;
        }
        return new ArrayList<>();
    }

    /**
     * Retorna o caminho completo da pasta de dados.
     */
    public static String getCaminhoPasta() {
        return new File(PASTA_DADOS).getAbsolutePath();
    }

    /**
     * Verifica se um arquivo de dados existe.
     */
    public static boolean arquivoExiste(String nomeArquivo) {
        return new File(PASTA_DADOS + File.separator + nomeArquivo).exists();
    }
}
