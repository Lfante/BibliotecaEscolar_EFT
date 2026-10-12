package vista;

import javax.swing.*;
import java.awt.*;

final class UiUtils {

    private UiUtils() {
    }

    static void error(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(
                parent,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    static void info(Component parent, String mensaje) {
        JOptionPane.showMessageDialog(
                parent,
                mensaje,
                "Información",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    static boolean confirmar(Component parent, String mensaje) {
        return JOptionPane.showConfirmDialog(
                parent,
                mensaje,
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }
}
