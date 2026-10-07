package pdv.demo4.model;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FaturaFinanceiraTest {

    private final Empresa empresa = new Empresa("Fornecedor", "");

    @Test
    void faturaNovaComecaAbertaENaoAtrasaNoDiaDoVencimento() {
        FaturaFinanceira fatura = novaFatura(LocalDate.now().plusDays(1));

        assertInstanceOf(EstadoFaturaAberta.class, fatura.getEstado(LocalDate.now()));
        assertInstanceOf(EstadoFaturaAberta.class, fatura.getEstado(LocalDate.now().plusDays(1)));
    }

    @Test
    void estadoAbertoTransicionaParaAtrasadoDepoisDoVencimento() {
        FaturaFinanceira fatura = novaFatura(LocalDate.of(2026, 10, 5));

        assertInstanceOf(EstadoFaturaAtrasada.class, fatura.getEstado(LocalDate.of(2026, 10, 6)));
        // Depois da transição, o estado atrasado não volta a aberto.
        assertInstanceOf(EstadoFaturaAtrasada.class, fatura.getEstado(LocalDate.of(2026, 10, 5)));
    }

    @Test
    void faturaAtrasadaPodeSerQuitadaEPermaneceQuitada() {
        FaturaFinanceira fatura = novaFatura(LocalDate.of(2026, 10, 5));
        fatura.getEstado(LocalDate.of(2026, 10, 6));
        fatura.marcarComoQuitada(LocalDateTime.of(2026, 10, 7, 12, 0));

        assertInstanceOf(EstadoFaturaQuitada.class, fatura.getEstado(LocalDate.of(2026, 10, 8)));
        assertTrue(fatura.estaQuitada());
        assertEquals(LocalDateTime.of(2026, 10, 7, 12, 0), fatura.getQuitadaEm());
        assertThrows(IllegalStateException.class,
                () -> fatura.marcarComoQuitada(LocalDateTime.now()));
    }

    @Test
    void pagamentoSemDataEhRejeitadoSemAlterarEstado() {
        FaturaFinanceira fatura = novaFatura(LocalDate.of(2026, 10, 5));

        assertThrows(IllegalArgumentException.class, () -> fatura.marcarComoQuitada(null));
        assertInstanceOf(EstadoFaturaAberta.class, fatura.getEstado(LocalDate.of(2026, 10, 5)));
        assertNull(fatura.getQuitadaEm());
    }

    @Test
    void estadoSerializadoContinuaLigadoAoContextoRestaurado() throws Exception {
        FaturaFinanceira original = novaFatura(LocalDate.of(2026, 10, 5));
        original.getEstado(LocalDate.of(2026, 10, 6));

        // O estado contém uma referência à fatura; a serialização deve preservar
        // o vínculo com a cópia restaurada, e não com o objeto original.
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream saida = new ObjectOutputStream(bytes)) {
            saida.writeObject(original);
        }

        FaturaFinanceira restaurada;
        try (ObjectInputStream entrada = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            restaurada = assertInstanceOf(FaturaFinanceira.class, entrada.readObject());
        }

        restaurada.marcarComoQuitada(LocalDateTime.of(2026, 10, 7, 12, 0));

        assertTrue(restaurada.estaQuitada());
        assertNull(original.getQuitadaEm());
    }

    @Test
    void faturaAntigaSemEstadoRecuperaSeuEstadoAoSerDesserializada() throws Exception {
        FaturaFinanceira original = novaFatura(LocalDate.now().plusDays(1));
        var campoEstado = FaturaFinanceira.class.getDeclaredField("estado");
        campoEstado.setAccessible(true);
        campoEstado.set(original, null);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream saida = new ObjectOutputStream(bytes)) {
            saida.writeObject(original);
        }

        FaturaFinanceira restaurada;
        try (ObjectInputStream entrada = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()))) {
            restaurada = assertInstanceOf(FaturaFinanceira.class, entrada.readObject());
        }

        assertInstanceOf(EstadoFaturaAberta.class, restaurada.getEstado());
    }

    private FaturaFinanceira novaFatura(LocalDate vencimento) {
        return new FaturaFinanceira(new BigDecimal("25.00"), vencimento, "Conta de teste", empresa);
    }
}
