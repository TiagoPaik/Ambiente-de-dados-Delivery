package Delivery;

import Delivery.ui.LoginFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // tenta aplicar FlatLaf (se JAR estiver no classpath)
            } catch (Exception ignored) {
                // fallback automático para look-and-feel do sistema
                try { javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName()); }
                catch (Exception ignored2) {}
            }
            new LoginFrame().setVisible(true);
        });
    }
}
