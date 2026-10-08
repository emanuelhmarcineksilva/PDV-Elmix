package pdv.demo4.controller;

/** Assinatura que permite remover explicitamente um observador. */
public final class AssinaturaEventoFatura implements AutoCloseable {
    private final Runnable cancelamento;
    private boolean cancelada;

    public AssinaturaEventoFatura(Runnable cancelamento) {
        this.cancelamento = cancelamento;
    }

    @Override
    public void close() {
        if (!cancelada) {
            cancelamento.run();
            cancelada = true;
        }
    }
}
