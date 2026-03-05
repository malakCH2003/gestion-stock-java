package util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {
    private Color bg;
    private Color hover;
    private Color pressed;
    private int radius = 14;

    public RoundedButton(String text, Color bg, Color hover, Color pressed) {
        super(text);
        this.bg = bg;
        this.hover = hover;
        this.pressed = pressed;

        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setFont(Theme.BODY);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { repaint(); }
            @Override public void mouseExited(MouseEvent e) { repaint(); }
            @Override public void mousePressed(MouseEvent e) { repaint(); }
            @Override public void mouseReleased(MouseEvent e) { repaint(); }
        });
    }

    public void setRadius(int r) { this.radius = r; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color c = bg;
        if (getModel().isPressed()) c = pressed;
        else if (getModel().isRollover()) c = hover;

        g2.setColor(c);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}