package app;

import util.UIHelper;

public class Main {
    public static void main(String[] args) {
        UIHelper.setNimbus();

        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}