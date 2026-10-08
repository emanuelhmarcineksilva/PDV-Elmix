package pdv.demo4.controller;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Mantém as inscrições e encaminha cada evento apenas aos observadores
 * interessados naquele tipo.
 */
public final class GerenciadorEventosFatura {
    private final Map<TipoEventoFatura, List<ObservadorFatura>> observadores =
            new EnumMap<>(TipoEventoFatura.class);

    public synchronized AssinaturaEventoFatura inscrever(
            TipoEventoFatura tipo, ObservadorFatura observador) {
        Objects.requireNonNull(tipo, "O tipo do evento é obrigatório");
        Objects.requireNonNull(observador, "O observador é obrigatório");

        List<ObservadorFatura> inscritos =
                observadores.computeIfAbsent(tipo, chave -> new ArrayList<>());
        inscritos.add(observador);
        return new AssinaturaEventoFatura(() -> remover(tipo, observador));
    }

    public void publicar(EventoFatura evento) {
        Objects.requireNonNull(evento, "O evento é obrigatório");
        List<ObservadorFatura> inscritos;
        synchronized (this) {
            inscritos = List.copyOf(
                    observadores.getOrDefault(evento.getTipo(), List.of()));
        }

        // A cópia permite que um observador altere inscrições sem afetar
        // a notificação atualmente em andamento.
        for (ObservadorFatura observador : inscritos) {
            observador.atualizar(evento);
        }
    }

    private synchronized void remover(
            TipoEventoFatura tipo, ObservadorFatura observador) {
        List<ObservadorFatura> inscritos = observadores.get(tipo);
        if (inscritos != null) {
            inscritos.remove(observador);
            if (inscritos.isEmpty()) {
                observadores.remove(tipo);
            }
        }
    }
}
