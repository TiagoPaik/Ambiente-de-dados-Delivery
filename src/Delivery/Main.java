package Delivery;

import Delivery.config.Conexao;
import Delivery.ui.LoginFrame;

import javax.swing.*;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
//        try(Connection comn = Conexao.getConnection()){
//            System.out.println("Conectado com Sucesso");
//        }
//        catch (Exception e){
//            System.out.println("Erro ao conectar com o banco"+e.getMessage());
//        }
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
