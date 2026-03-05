package ui;

import dao.ProductDAO;
import util.CardPanel;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ReportsPanel extends JPanel {

    private static final int THRESHOLD = 5;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nom", "Quantité", "Statut"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JLabel kRupture = new JLabel("0");
    private final JLabel kFaible = new JLabel("0");
    private final JLabel kTotal = new JLabel("0");

    private final JTable table = new JTable(model);

    private final ProductDAO productDAO = new ProductDAO();

    public ReportsPanel() {
        setLayout(new BorderLayout(14, 14));
        setOpaque(false);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        // ===== Header =====
        JPanel header = new JPanel(new GridLayout(0, 1, 2, 2));
        header.setOpaque(false);

        JLabel title = new JLabel("Rapports");
        title.setFont(Theme.H1);
        title.setForeground(Theme.TEXT);

        JLabel sub = new JLabel("Liste des produits en rupture ou en stock faible (seuil ≤ " + THRESHOLD + ").");
        sub.setFont(Theme.BODY.deriveFont(12f));
        sub.setForeground(Theme.MUTED);

        header.add(title);
        header.add(sub);

        add(header, BorderLayout.NORTH);

        // ===== KPI Row =====
        JPanel kpis = new JPanel(new GridLayout(1, 3, 12, 12));
        kpis.setOpaque(false);

        kpis.add(kpi("Rupture", kRupture, new Color(239, 68, 68)));
        kpis.add(kpi("Stock faible", kFaible, new Color(249, 115, 22)));
        kpis.add(kpi("Total alertes", kTotal, Theme.BLUE));

        // ===== Table Card =====
        CardPanel tableCard = new CardPanel();
        tableCard.setLayout(new BorderLayout(12, 12));

        JLabel t = new JLabel("Produits à surveiller");
        t.setFont(Theme.H2);
        t.setForeground(Theme.TEXT);

        table.setRowHeight(26);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.BORDER);
        table.getTableHeader().setFont(Theme.BODY.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(Theme.CARD_BG);
        table.getTableHeader().setForeground(Theme.MUTED);

        // Renderer statut coloré
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JButton refresh = primaryButton("Actualiser");
        refresh.addActionListener(e -> load());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(t, BorderLayout.WEST);
        top.add(refresh, BorderLayout.EAST);

        tableCard.add(top, BorderLayout.NORTH);
        tableCard.add(sp, BorderLayout.CENTER);

        // ===== Layout Center =====
        JPanel center = new JPanel(new BorderLayout(14, 14));
        center.setOpaque(false);
        center.add(kpis, BorderLayout.NORTH);
        center.add(tableCard, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        load();
    }

    private JComponent kpi(String label, JLabel value, Color accent) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(6, 6));

        JLabel l = new JLabel(label);
        l.setFont(Theme.BODY);
        l.setForeground(Theme.MUTED);

        value.setFont(new Font("Segoe UI", Font.BOLD, 26));
        value.setForeground(Theme.TEXT);

        JPanel bar = new JPanel();
        bar.setPreferredSize(new Dimension(10, 6));
        bar.setBackground(accent);
        bar.setOpaque(true);

        card.add(l, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(bar, BorderLayout.SOUTH);
        return card;
    }

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.BODY.deriveFont(Font.BOLD));
        b.setForeground(Color.WHITE);
        b.setBackground(Theme.SIDEBAR_ACTIVE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(120, 38));

        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                b.setBackground(new Color(220, 38, 38));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                b.setBackground(Theme.SIDEBAR_ACTIVE);
            }
        });
        return b;
    }

    private void load() {
        model.setRowCount(0);

        try {
            int rupture = productDAO.countRupture();
            int faible  = productDAO.countLowStock(THRESHOLD);

            var alerts = productDAO.findAlerts(THRESHOLD);
            for (var a : alerts) {
                model.addRow(new Object[]{a.id, a.name, a.quantity, a.status});
            }

            kRupture.setText(String.valueOf(rupture));
            kFaible.setText(String.valueOf(faible));
            kTotal.setText(String.valueOf(rupture + faible));

        } catch (Exception ex) {
            // si DB down / mauvais user/pass
            kRupture.setText("0");
            kFaible.setText("0");
            kTotal.setText("0");
            JOptionPane.showMessageDialog(this,
                    "Erreur DB: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===== Renderer pour colorer Statut =====
    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {

            JLabel c = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            String s = String.valueOf(value);

            c.setFont(new Font("Segoe UI", Font.BOLD, 12));
            c.setHorizontalAlignment(SwingConstants.CENTER);

            if ("Rupture".equalsIgnoreCase(s)) {
                c.setForeground(new Color(239, 68, 68));
            } else if ("Faible".equalsIgnoreCase(s)) {
                c.setForeground(new Color(249, 115, 22));
            } else {
                c.setForeground(new Color(34, 197, 94));
            }

            return c;
        }
    }
}