package app;

import ui.*;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);

    private JButton active;

    public MainFrame() {
        setTitle("Gestion de Stock");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1250, 750);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.APP_BG);

        // Sidebar
        JPanel sidebar = buildSidebar();
        root.add(sidebar, BorderLayout.WEST);

        // Center = Topbar + Content
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.APP_BG);
        center.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel topbar = buildTopbar();
        center.add(topbar, BorderLayout.NORTH);

        content.setBackground(Theme.APP_BG);
        content.setBorder(new EmptyBorder(16, 0, 0, 0));

        content.add(new DashboardPanel(), "DASH");
        content.add(new ProductsPanel(this), "PROD");
        content.add(new MovementsPanel(this), "MOVE");
        content.add(new ReportsPanel(), "REP");

        center.add(content, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        setContentPane(root);

        showCard("DASH", active);
    }

    private JPanel buildTopbar() {
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        JButton settings = new JButton("⚙");
        styleIconBtn(settings);
        right.add(settings);

        JButton notif = new JButton("🔔");
        styleIconBtn(notif);
        right.add(notif);

        top.add(right, BorderLayout.EAST);
        return top;
    }

    private void styleIconBtn(JButton b) {
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        b.setForeground(Theme.MUTED);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private JPanel buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(Theme.SIDEBAR_BG);
        side.setPreferredSize(new Dimension(240, 0));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(new EmptyBorder(18, 14, 18, 14));

        JLabel logo = new JLabel("Gestion de Stock");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(Theme.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        side.add(logo);
        side.add(Box.createVerticalStrut(18));

        // ✅ Sidebar en FR
        JButton bDash = nav("Tableau de bord", "DASH");
        JButton bProd = nav("Produits", "PROD");
        JButton bMov  = nav("Mouvements", "MOVE");
        JButton bRep  = nav("Rapports", "REP");

        side.add(bDash);
        side.add(Box.createVerticalStrut(8));
        side.add(bProd);
        side.add(Box.createVerticalStrut(8));
        side.add(bMov);
        side.add(Box.createVerticalStrut(8));
        side.add(bRep);

        side.add(Box.createVerticalGlue());

        // Default active
        setActive(bDash);
        active = bDash;

        return side;
    }

    private JButton nav(String text, String cardKey) {
        JButton b = new JButton(text);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        b.setHorizontalAlignment(SwingConstants.LEFT);

        b.setFont(Theme.BODY);
        b.setForeground(new Color(226, 232, 240));
        b.setBackground(Theme.SIDEBAR_BG);
        b.setOpaque(true);
        b.setFocusPainted(false);

        b.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addActionListener(e -> {
            setActive(b);
            showCard(cardKey, b);
        });

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (b != active) b.setBackground(Theme.SIDEBAR_HOVER);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (b != active) b.setBackground(Theme.SIDEBAR_BG);
            }
        });

        return b;
    }

    private void setActive(JButton b) {
        if (active != null) {
            active.setBackground(Theme.SIDEBAR_BG);
            active.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));
        }
        active = b;
        b.setBackground(Theme.SIDEBAR_HOVER);
        b.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.SIDEBAR_ACTIVE));
    }

    public void showCard(String key, JButton btn) {
        cards.show(content, key);
        content.revalidate();
        content.repaint();
    }
}