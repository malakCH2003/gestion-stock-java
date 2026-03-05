package util;

import javax.swing.*;

public class UIHelper {
    public static void setNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception ignored) {}
    }

    public static void msg(java.awt.Component parent, String text) {
        JOptionPane.showMessageDialog(parent, text);
    }

    public static boolean confirm(java.awt.Component parent, String text) {
        return JOptionPane.showConfirmDialog(parent, text, "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
}