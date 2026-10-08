package pdv.demo4.model;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConfiguracaoTest {

    @Test
    void usaDoisDiasDeAntecedenciaComoPadrao() {
        assertEquals(2, new Configuracao().getDiasAlertaFatura());
    }

    @Test
    void permiteDesativarSomenteOLembreteAntecipado() {
        Configuracao configuracao = new Configuracao();
        configuracao.setDiasAlertaFatura(0);

        assertEquals(0, configuracao.getDiasAlertaFatura());
    }

    @Test
    void rejeitaAntecedenciaForaDoLimite() {
        assertThrows(IllegalArgumentException.class,
                () -> new Configuracao().setDiasAlertaFatura(366));
    }

    @Test
    void configuracaoAntigaSemCampoDeAntecedenciaUsaPadrao() throws Exception {
        Configuracao original = new Configuracao();
        var campoDias = Configuracao.class.getDeclaredField("diasAlertaFatura");
        campoDias.setAccessible(true);
        campoDias.set(original, null);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream saida = new ObjectOutputStream(bytes)) {
            saida.writeObject(original);
        }

        try (ObjectInputStream entrada = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            Configuracao restaurada = (Configuracao) entrada.readObject();
            assertEquals(2, restaurada.getDiasAlertaFatura());
        }
    }
}
