package main;

import config.DatabaseConnection;
import vista.LoginFrame;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(
                new Thread(() ->
                        DatabaseConnection.getInstance().closeConnection()
                )
        );

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception ignored) {
                // Si no se puede aplicar, Swing usa su apariencia por defecto.
            }

            new LoginFrame().setVisible(true);
        });
    }
}
