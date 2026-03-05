package ui;

import dao.MovementDAO;
import dao.ProductDAO;
import db.DB;
import model.Product;
import model.StockMovement;
import util.CardPanel;
import util.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final ProductDAO productDAO = new ProductDAO();
    private final MovementDAO movementDAO = new MovementDAO();

    // KPIs (top)
    private JLabel kTotalQty;
    private JLabel kProducts;
    private JLabel kMoves;

    // Summary card (right on mid row)
    private JLabel sNbProd;
    private JLabel sTotal;

    // Recent movements table model
    private DefaultTableModel recentModel;

    public DashboardPanel() {
        setLayout(new BorderLayout(14, 14));
        setOpaque(false);

        JLabel title = new JLabel("Activité");
        title.setFont(Theme.H1);
        title.setForeground(Theme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(14, 14));
        body.setOpaque(false);

        // ================= KPI row =================
        JPanel kpis = new JPanel(new GridLayout(1, 3, 12, 12));
        kpis.setOpaque(false);

        kTotalQty = new JLabel("0");
        kProducts = new JLabel("0");
        kMoves    = new JLabel("0");

        kpis.add(kpi("Quantité totale", kTotalQty, Theme.BLUE));
        kpis.add(kpi("Produits", kProducts, Theme.GREEN));
        kpis.add(kpi("Mouvements", kMoves, Theme.BLUE));

        body.add(kpis, BorderLayout.NORTH);

        // ================= Mid row =================
        JPanel mid = new JPanel(new GridLayout(1, 2, 12, 12));
        mid.setOpaque(false);

        mid.add(productSummaryCard());
        mid.add(recentMovementsCard());

        body.add(mid, BorderLayout.CENTER);

        add(body, BorderLayout.CENTER);

        load(); // initial load
    }

    public void load() {

        // ================= DEBUG DB (à garder pendant test) =================
        // Si ça affiche 0 ici => ta base est vide OU mauvaise DB/port/user
        // Si ça affiche 200 ici mais KPI restent 0 => problème dans tes DAO
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT COUNT(*) AS n, COALESCE(SUM(quantity),0) AS s FROM products"
             );
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            System.out.println("DEBUG DB -> products=" + rs.getInt("n") + " stock=" + rs.getInt("s"));

        } catch (Exception ex) {
            ex.printStackTrace();
            return; // stop if DB error
        }

        // ================= NORMAL LOAD =================
        int totalStock = productDAO.totalStock();
        int countProd  = productDAO.countProducts();
        int countMov   = movementDAO.countMovements();

        // Top KPIs
        kTotalQty.setText(String.valueOf(totalStock));
        kProducts.setText(String.valueOf(countProd));
        kMoves.setText(String.valueOf(countMov));

        // Summary card values
        if (sNbProd != null) sNbProd.setText(String.valueOf(countProd));
        if (sTotal  != null) sTotal.setText(String.valueOf(totalStock));

        // Recent table
        if (recentModel != null) {
            recentModel.setRowCount(0);
            List<StockMovement> recent = movementDAO.findRecent(6);

            for (StockMovement mv : recent) {
                Product p = productDAO.findById(mv.getProductId());
                String name = (p == null) ? mv.getProductId() : p.getName();

                recentModel.addRow(new Object[]{
                        name,
                        (mv.getType() == StockMovement.Type.IN ? "ENTRÉE" : "SORTIE"),
                        mv.getQty()
                });
            }
        }
    }

    // ================= UI helpers =================

    private JComponent kpi(String label, JLabel value, Color accent) {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(6, 6));

        JLabel l = new JLabel(label);
        l.setFont(Theme.BODY);
        l.setForeground(Theme.MUTED);

        value.setFont(new Font("Segoe UI", Font.BOLD, 22));
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

    private JComponent productSummaryCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Détails des produits");
        t.setFont(Theme.H2);
        t.setForeground(Theme.TEXT);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(t, BorderLayout.WEST);

        JLabel tag = pill("Stock", Theme.BLUE);
        header.add(tag, BorderLayout.EAST);

        JPanel stats = new JPanel(new GridLayout(1, 2, 12, 12));
        stats.setOpaque(false);

        // IMPORTANT: champs, pas variables locales
        sNbProd = new JLabel("0");
        sTotal  = new JLabel("0");

        stats.add(statBox("Nombre de produits", sNbProd, Theme.BLUE));
        stats.add(statBox("Stock total", sTotal, Theme.GREEN));

        JLabel hint = new JLabel("Aperçu rapide de votre catalogue et du niveau de stock.");
        hint.setFont(Theme.BODY.deriveFont(12f));
        hint.setForeground(Theme.MUTED);

        JPanel top = new JPanel(new BorderLayout(0, 10));
        top.setOpaque(false);
        top.add(header, BorderLayout.NORTH);
        top.add(hint, BorderLayout.CENTER);

        card.add(top, BorderLayout.NORTH);
        card.add(stats, BorderLayout.CENTER);

        return card;
    }

    private JComponent statBox(String label, JLabel value, Color accent) {
        JPanel box = new JPanel(new BorderLayout(8, 6));
        box.setBackground(Theme.CARD_BG);
        box.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        top.setOpaque(false);

        JPanel dot = new JPanel();
        dot.setPreferredSize(new Dimension(10, 10));
        dot.setBackground(accent);

        JLabel l = new JLabel(label);
        l.setFont(Theme.BODY);
        l.setForeground(Theme.MUTED);

        top.add(dot);
        top.add(l);

        value.setFont(new Font("Segoe UI", Font.BOLD, 28));
        value.setForeground(Theme.TEXT);

        box.add(top, BorderLayout.NORTH);
        box.add(value, BorderLayout.CENTER);

        return box;
    }

    private JLabel pill(String text, Color accent) {
        JLabel p = new JLabel("  " + text + "  ");
        p.setFont(new Font("Segoe UI", Font.BOLD, 12));
        p.setForeground(accent);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80)),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return p;
    }

    private JComponent recentMovementsCard() {
        CardPanel card = new CardPanel();
        card.setLayout(new BorderLayout(10, 10));

        JLabel t = new JLabel("Mouvements récents");
        t.setFont(Theme.H2);
        t.setForeground(Theme.TEXT);

        recentModel = new DefaultTableModel(new Object[]{"Produit", "Type", "Qté"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(recentModel);
        table.setRowHeight(24);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.BORDER);
        table.getTableHeader().setFont(Theme.BODY.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(Theme.CARD_BG);
        table.getTableHeader().setForeground(Theme.MUTED);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        JButton refresh = new JButton("Actualiser");
        refresh.setFont(Theme.BODY.deriveFont(Font.BOLD));
        refresh.setForeground(Color.WHITE);
        refresh.setBackground(Theme.SIDEBAR_ACTIVE);
        refresh.setOpaque(true);
        refresh.setBorderPainted(false);
        refresh.setFocusPainted(false);
        refresh.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        refresh.addActionListener(e -> load());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(t, BorderLayout.WEST);
        top.add(refresh, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(sp, BorderLayout.CENTER);
        return card;
    }
}