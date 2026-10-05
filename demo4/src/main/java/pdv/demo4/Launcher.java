package pdv.demo4;

import javafx.application.Application;

/**
 * Classe lançadora do sistema PDV Elmix.
 * Necessária para contornar limitações do JavaFX com módulos.
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(ElmixApp.class, args);
    }
}
