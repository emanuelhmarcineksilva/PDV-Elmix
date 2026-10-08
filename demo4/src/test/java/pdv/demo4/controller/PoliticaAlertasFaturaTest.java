package pdv.demo4.controller;

import org.junit.jupiter.api.Test;
import pdv.demo4.model.Empresa;
import pdv.demo4.model.FaturaFinanceira;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PoliticaAlertasFaturaTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 10, 7);

    @Test
    void alertaVencimentoComAntecedenciaConfigurada() {
        FaturaFinanceira fatura = novaFatura(HOJE.plusDays(2));

        EventoFatura evento = PoliticaAlertasFatura.avaliar(fatura, HOJE, 2).orElseThrow();

        assertEquals(TipoEventoFatura.LEMBRETE_VENCIMENTO, evento.getTipo());
        assertEquals(2, evento.getDiasEmRelacaoAoVencimento());
    }

    @Test
    void naoAlertaAntesDoLimiteConfigurado() {
        assertFalse(PoliticaAlertasFatura.avaliar(novaFatura(HOJE.plusDays(3)), HOJE, 2).isPresent());
    }

    @Test
    void diaDoVencimentoTemEventoProprio() {
        EventoFatura evento = PoliticaAlertasFatura.avaliar(novaFatura(HOJE), HOJE, 2).orElseThrow();

        assertEquals(TipoEventoFatura.VENCIMENTO_HOJE, evento.getTipo());
    }

    @Test
    void vencimentoPassadoInformaDiasDeAtraso() {
        EventoFatura evento = PoliticaAlertasFatura.avaliar(
                novaFatura(HOJE.minusDays(3)), HOJE, 2).orElseThrow();

        assertEquals(TipoEventoFatura.FATURA_ATRASADA, evento.getTipo());
        assertEquals(3, evento.getDiasEmRelacaoAoVencimento());
    }

    @Test
    void faturaQuitadaNaoGeraLembrete() {
        FaturaFinanceira fatura = novaFatura(HOJE.plusDays(1));
        fatura.marcarComoQuitada(LocalDateTime.of(2026, 10, 7, 12, 0));

        assertFalse(PoliticaAlertasFatura.avaliar(fatura, HOJE, 2).isPresent());
    }

    @Test
    void rejeitaAntecedenciaForaDoIntervaloSuportado() {
        assertThrows(IllegalArgumentException.class,
                () -> PoliticaAlertasFatura.avaliar(novaFatura(HOJE), HOJE, 366));
    }

    private FaturaFinanceira novaFatura(LocalDate vencimento) {
        return new FaturaFinanceira(
                new BigDecimal("10.00"), vencimento, "Teste", new Empresa("Fornecedor", ""));
    }
}
