package pdv.demo4.controller;

/** Receptor interessado em mudanças ou lembretes publicados para faturas. */
@FunctionalInterface
public interface ObservadorFatura {
    void atualizar(EventoFatura evento);
}
