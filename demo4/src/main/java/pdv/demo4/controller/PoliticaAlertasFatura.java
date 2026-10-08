package pdv.demo4.controller;

import pdv.demo4.model.FaturaFinanceira;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/** Decide qual lembrete diário, se houver, corresponde à data de uma fatura. */
public final class PoliticaAlertasFatura {
    private PoliticaAlertasFatura() {
    }

    public static Optional<EventoFatura> avaliar(
            FaturaFinanceira fatura, LocalDate hoje, int diasDeAntecedencia) {
        if (fatura == null || hoje == null) {
            throw new IllegalArgumentException("A fatura e a data atual são obrigatórias");
        }
        if (diasDeAntecedencia < 0 || diasDeAntecedencia > 365) {
            throw new IllegalArgumentException(
                    "A antecedência do alerta deve estar entre 0 e 365 dias");
        }
        if (fatura.estaQuitada()) {
            return Optional.empty();
        }

        long diasAteVencimento = ChronoUnit.DAYS.between(hoje, fatura.getDataVencimento());
        if (diasAteVencimento < 0) {
            return Optional.of(new EventoFatura(
                    TipoEventoFatura.FATURA_ATRASADA, fatura, -diasAteVencimento));
        }
        if (diasAteVencimento == 0) {
            return Optional.of(new EventoFatura(
                    TipoEventoFatura.VENCIMENTO_HOJE, fatura, 0));
        }
        if (diasAteVencimento <= diasDeAntecedencia) {
            return Optional.of(new EventoFatura(
                    TipoEventoFatura.LEMBRETE_VENCIMENTO, fatura, diasAteVencimento));
        }
        return Optional.empty();
    }
}
