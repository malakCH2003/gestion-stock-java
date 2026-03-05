package ui;

import app.MainFrame;
import dao.MovementDAO;
import dao.ProductDAO;
import model.Product;
import model.StockMovement;
import util.CardPanel;
import util.Theme;
import util.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class MovementsPanel extends JPanel {

    private final ProductDAO productDAO = new ProductDAO();
    private final MovementDAO movementDAO = new MovementDAO();

    private final JComboBox<String> cbProduct = new JComboBox<>();
    private final JComboBox<String> cbType = new JComboBox<>(new String[]{"ENTRÉE", "SORTIE"});
    private final JTextField tfQty = new JTextField();
    private final JTextField tfNote = new JTextField();

    private final DefaultTableModel model;
    private final JTable table;

    public MovementsPanel(MainFrame main) {
        setLayout(new BorderLayout(14, 14));
        setOpaque(false);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        // ===== Header =====
        JPanel header = new JPanel(new GridLayout(0, 1, 2, 2));
        header.setOpaque(false);

        JLabel title = new JLabel("Mouvements de stock");
        title.setFont(Theme.H1);
        title.setForeground(Theme.TEXT);

        JLabel sub = new JLabel("Enregistrez une entrée/sortie et consultez l’historique des mouvements.");
        sub.setFont(Theme.BODY.deriveFont(12f));
        sub.setForeground(Theme.MUTED);

        header.add(title);
        header.add(sub);

        add(header, BorderLayout.NORTH);

        // ===== Content 2 columns =====
        JPanel grid = new JPanel(new GridLayout(1, 2, 14, 14));
        grid.setOpaque(false);

        // LEFT CARD: Form
        CardPanel formCard = new CardPanel();
        formCard.setLayout(new BorderLayout(12, 12));

        JLabel formTitle = new JLabel("Nouveau mouvement");
        formTitle.setFont(Theme.H2);
        formTitle.setForeground(Theme.TEXT);

        JPanel form = new JPanel(new GridLayout(0, 1, 10, 10));
        form.setOpaque(false);

        refreshProducts();
        styleCombo(cbProduct);
        styleCombo(cbType);
        styleField(tfQty);
        styleField(tfNote);

        form.add(row("Produit", cbProduct));
        form.add(row("Type", cbType));
        form.add(row("Quantité", tfQty));
        form.add(row("Note (optionnel)", tfNote));

        JButton bAdd = primaryButton("Valider");
        bAdd.addActionListener(e -> addMovement());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(bAdd, BorderLayout.CENTER);

        formCard.add(formTitle, BorderLayout.NORTH);
        formCard.add(form, BorderLayout.CENTER);
        formCard.add(bottom, BorderLayout.SOUTH);

        // RIGHT CARD: Table
        CardPanel tableCard = new CardPanel();
        tableCard.setLayout(new BorderLayout(12, 12));

        JLabel tableTitle = new JLabel("Historique des mouvements");
        tableTitle.setFont(Theme.H2);
        tableTitle.setForeground(Theme.TEXT);

        model = new DefaultTableModel(new Object[]{"Date", "Produit", "Type", "Qté", "Note"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(26);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(new Color(226, 232, 240));
        table.setSelectionForeground(Theme.TEXT);

        table.getTableHeader().setFont(Theme.BODY.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(Theme.CARD_BG);
        table.getTableHeader().setForeground(Theme.MUTED);

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        // small refresh button
        JButton refreshBtn = softButton("Actualiser");
        refreshBtn.addActionListener(e -> {
            refreshProducts();
            refreshTable();
        });

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(tableTitle, BorderLayout.WEST);
        top.add(refreshBtn, BorderLayout.EAST);

        tableCard.add(top, BorderLayout.NORTH);
        tableCard.add(sp, BorderLayout.CENTER);

        grid.add(formCard);
        grid.add(tableCard);

        add(grid, BorderLayout.CENTER);

        refreshTable();
    }

    // ===== UI helpers =====
    private JPanel row(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(8, 6));
        p.setOpaque(false);

        JLabel l = new JLabel(label);
        l.setFont(Theme.BODY);
        l.setForeground(Theme.MUTED);

        field.setPreferredSize(new Dimension(0, 36));

        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private void styleField(JTextField f) {
        f.setFont(Theme.BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        f.setBackground(Color.WHITE);
        f.setForeground(Theme.TEXT);
    }

    private void styleCombo(JComboBox<?> cb) {
        cb.setFont(Theme.BODY);
        cb.setBackground(Color.WHITE);
        cb.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
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
        b.setPreferredSize(new Dimension(0, 42));

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

    private JButton softButton(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.BODY.deriveFont(Font.BOLD));
        b.setForeground(Theme.TEXT);
        b.setBackground(new Color(241, 245, 249));
        b.setOpaque(true);
        b.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ===== Data =====
    private void refreshProducts() {
        cbProduct.removeAllItems();
        List<Product> list = productDAO.findAll("");
        for (Product p : list) {
            cbProduct.addItem(p.getId() + " - " + p.getName());
        }
    }

    private void refreshTable() {
        model.setRowCount(0);

        List<StockMovement> list = movementDAO.findAll();
        for (StockMovement m : list) {
            Product p = productDAO.findById(m.getProductId());
            String pname = (p == null) ? m.getProductId() : (p.getId() + " - " + p.getName());
            String typeFr = (m.getType() == StockMovement.Type.IN) ? "ENTRÉE" : "SORTIE";

            model.addRow(new Object[]{
                    m.getDate(),
                    pname,
                    typeFr,
                    m.getQty(),
                    m.getNote()
            });
        }
    }

    private void addMovement() {
        if (cbProduct.getSelectedItem() == null) {
            UIHelper.msg(this, "Aucun produit.");
            return;
        }

        try {
            String selected = cbProduct.getSelectedItem().toString();
            String productId = selected.split(" - ")[0].trim();

            Product p = productDAO.findById(productId);
            if (p == null) { UIHelper.msg(this, "Produit introuvable."); return; }

            int qty = Integer.parseInt(tfQty.getText().trim());
            if (qty <= 0) { UIHelper.msg(this, "Quantité > 0."); return; }

            boolean isIn = cbType.getSelectedItem().toString().equals("ENTRÉE");
            if (!isIn && p.getQuantity() < qty) {
                UIHelper.msg(this, "Stock insuffisant (disponible: " + p.getQuantity() + ").");
                return;
            }

            StockMovement.Type type = isIn ? StockMovement.Type.IN : StockMovement.Type.OUT;
            String note = tfNote.getText().trim();

            // 1) update product quantity
            p.setQuantity(isIn ? p.getQuantity() + qty : p.getQuantity() - qty);
            productDAO.update(p);

            // 2) insert movement
            movementDAO.insert(productId, type, qty, LocalDateTime.now(), note);

            UIHelper.msg(this, "Mouvement enregistré.");
            tfQty.setText("");
            tfNote.setText("");

            refreshTable();

        } catch (Exception ex) {
            UIHelper.msg(this, "Quantité invalide.");
        }
    }
}