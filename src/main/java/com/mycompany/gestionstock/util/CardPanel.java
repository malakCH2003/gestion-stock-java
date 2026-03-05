package util;

import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel {
    private final int radius = 18;

    public CardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // shadow
        g2.setColor(new Color(0,0,0,20));
        g2.fillRoundRect(6, 6, getWidth()-12, getHeight()-12, radius, radius);

        // card
        g2.setColor(Theme.CARD_BG);
        g2.fillRoundRect(0, 0, getWidth()-12, getHeight()-12, radius, radius);

        // border
        g2.setColor(Theme.BORDER);
        g2.drawRoundRect(0, 0, getWidth()-12, getHeight()-12, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    public Insets getInsets() {
        return new Insets(16,16,16,16);
    }
}