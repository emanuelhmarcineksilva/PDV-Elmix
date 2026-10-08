package pdv.demo4.controller;

import org.junit.jupiter.api.Test;
import pdv.demo4.model.Empresa;
import pdv.demo4.model.FaturaFinanceira;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GerenciadorEventosFaturaTest {

    @Test
    void publicaSomenteParaInscritosNoTipoDoEvento() {
        GerenciadorEventosFatura eventos = new GerenciadorEventosFatura();
        AtomicInteger criadas = new AtomicInteger();
        AtomicInteger quitadas = new AtomicInteger();
        FaturaFinanceira fatura = novaFatura();

        eventos.inscrever(TipoEventoFatura.FATURA_CRIADA, evento -> criadas.incrementAndGet());
        eventos.inscrever(TipoEventoFatura.FATURA_QUITADA, evento -> quitadas.incrementAndGet());

        eventos.publicar(new EventoFatura(TipoEventoFatura.FATURA_CRIADA, fatura, 0));

        assertEquals(1, criadas.get());
        assertEquals(0, quitadas.get());
    }

    @Test
    void assinaturaFechadaParaDeReceberEventos() {
        GerenciadorEventosFatura eventos = new GerenciadorEventosFatura();
        AtomicInteger recebidos = new AtomicInteger();
        FaturaFinanceira fatura = novaFatura();
        AssinaturaEventoFatura assinatura = eventos.inscrever(
                TipoEventoFatura.FATURA_QUITADA, evento -> recebidos.incrementAndGet());

        assinatura.close();
        assinatura.close();
        eventos.publicar(new EventoFatura(TipoEventoFatura.FATURA_QUITADA, fatura, 0));

        assertEquals(0, recebidos.get());
    }

    private FaturaFinanceira novaFatura() {
        return new FaturaFinanceira(
                new BigDecimal("10.00"), LocalDate.now().plusDays(2), "Teste",
                new Empresa("Fornecedor", ""));
    }
}
