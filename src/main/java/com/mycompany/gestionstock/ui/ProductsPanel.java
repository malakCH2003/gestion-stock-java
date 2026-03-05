package ui;

import app.MainFrame;
import dao.ProductDAO;
import model.Product;
import util.UIHelper;
import util.Theme;
import util.CardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProductsPanel extends JPanel {

    private final ProductDAO productDAO = new ProductDAO();

    private final DefaultTableModel model;
    private final JTable table;

    private final JTextField tfId = new JTextField();
    private final JTextField tfName = new JTextField();
    private final JTextField tfCat = new JTextField();
    private final JTextField tfPrice = new JTextField();
    private final JTextField tfQty = new JTextField();
    private final JTextField tfSearch = new JTextField();

    public ProductsPanel(MainFrame main) {
        setLayout(new BorderLayout(14, 14));
        setOpaque(false);
        setBorder(new EmptyBorder(18, 18, 18, 18));

        // ===== TOP BAR =====
        JPanel top = new JPanel(new BorderLayout(12, 12));
        top.setOpaque(false);

        JLabel title = new JLabel("Gestion des produits");
        title.setFont(Theme.H1);
        title.setForeground(Theme.TEXT);
        top.add(title, BorderLayout.WEST);

        JPanel searchBar = new JPanel(new BorderLayout(8, 8));
        searchBar.setOpaque(false);

        JLabel lb = new JLabel("Rechercher");
        lb.setFont(Theme.BODY);
        lb.setForeground(Theme.MUTED);

        styleField(tfSearch);
        tfSearch.setPreferredSize(new Dimension(220, 36));

        JButton bFind = softButton("OK");
        bFind.setPreferredSize(new Dimension(70, 36));
        bFind.addActionListener(e -> refresh(tfSearch.getText().trim()));
        tfSearch.addActionListener(e -> refresh(tfSearch.getText().trim()));

        searchBar.add(lb, BorderLayout.WEST);
        searchBar.add(tfSearch, BorderLayout.CENTER);
        searchBar.add(bFind, BorderLayout.EAST);

        top.add(searchBar, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // ===== CENTER (TABLE CARD) =====
        CardPanel tableCard = new CardPanel();
        tableCard.setLayout(new BorderLayout(10, 10));

        JLabel tableTitle = new JLabel("Liste des produits");
        tableTitle.setFont(Theme.H2);
        tableTitle.setForeground(Theme.TEXT);

        model = new DefaultTableModel(new Object[]{"ID","Nom","Catégorie","Prix","Quantité"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setShowVerticalLines(false);
        table.setGridColor(Theme.BORDER);
        table.setSelectionBackground(new Color(226, 232, 240));
        table.setSelectionForeground(Theme.TEXT);

        table.getTableHeader().setFont(Theme.BODY.deriveFont(Font.BOLD));
        table.getTableHeader().setBackground(Theme.CARD_BG);
        table.getTableHeader().setForeground(Theme.MUTED);

        table.setDefaultRenderer(Object.class, new ZebraRenderer());
        table.getSelectionModel().addListSelectionListener(e -> fillFromSelected());

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER));

        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(sp, BorderLayout.CENTER);

        // ===== RIGHT (FORM CARD) =====
        CardPanel formCard = new CardPanel();
        formCard.setPreferredSize(new Dimension(360, 0));
        formCard.setLayout(new BorderLayout(12, 12));

        JLabel formTitle = new JLabel("Formulaire produit");
        formTitle.setFont(Theme.H2);
        formTitle.setForeground(Theme.TEXT);

        JPanel form = new JPanel(new GridLayout(0, 1, 8, 8));
        form.setOpaque(false);

        tfId.setEnabled(false);
        styleField(tfId);
        styleField(tfName);
        styleField(tfCat);
        styleField(tfPrice);
        styleField(tfQty);

        form.add(formRow("ID", tfId));
        form.add(formRow("Nom", tfName));
        form.add(formRow("Catégorie", tfCat));
        form.add(formRow("Prix", tfPrice));
        form.add(formRow("Quantité", tfQty));

        JPanel actions = new JPanel(new GridLayout(1, 0, 10, 10));
        actions.setOpaque(false);

        JButton bNew = softButton("Nouveau");
        JButton bAdd = primaryButton("Ajouter");
        JButton bUpd = softButton("Modifier");
        JButton bDel = dangerButton("Supprimer");

        bNew.addActionListener(e -> clear());
        bAdd.addActionListener(e -> add());
        bUpd.addActionListener(e -> update());
        bDel.addActionListener(e -> delete());

        actions.add(bNew);
        actions.add(bAdd);
        actions.add(bUpd);
        actions.add(bDel);

        formCard.add(formTitle, BorderLayout.NORTH);
        formCard.add(form, BorderLayout.CENTER);
        formCard.add(actions, BorderLayout.SOUTH);

        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.setOpaque(false);
        center.add(tableCard, BorderLayout.CENTER);
        center.add(formCard, BorderLayout.EAST);

        add(center, BorderLayout.CENTER);

        refresh("");
    }

    // -------- UI helpers ----------
    private JPanel formRow(String label, JComponent field) {
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

    private JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.BODY.deriveFont(Font.BOLD));
        b.setForeground(Color.WHITE);
        b.setBackground(Theme.SIDEBAR_ACTIVE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.setFont(Theme.BODY.deriveFont(Font.BOLD));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(239, 68, 68));
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private static class ZebraRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                c.setForeground(new Color(15, 23, 42));
            }
            setBorder(noFocusBorder);
            return c;
        }
    }

    // --------- logic ----------
    private void refresh(String q) {
        model.setRowCount(0);
        List<Product> list = productDAO.findAll(q);

        for (Product p : list) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    p.getPrice(),
                    p.getQuantity()
            });
        }
    }

    private void fillFromSelected() {
        int r = table.getSelectedRow();
        if (r < 0) return;

        tfId.setText(model.getValueAt(r, 0).toString());
        tfName.setText(model.getValueAt(r, 1).toString());
        tfCat.setText(model.getValueAt(r, 2).toString());
        tfPrice.setText(model.getValueAt(r, 3).toString());
        tfQty.setText(model.getValueAt(r, 4).toString());
    }

    private void clear() {
        tfId.setText("");
        tfName.setText("");
        tfCat.setText("");
        tfPrice.setText("");
        tfQty.setText("");
        table.clearSelection();
    }

    // id auto: même logique que ton DataStore.newId()
    private String newId() {
        return "P-" + java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase();
    }

    private void add() {
        try {
            String id = newId();
            String name = tfName.getText().trim();
            String cat = tfCat.getText().trim();
            double price = Double.parseDouble(tfPrice.getText().trim());
            int qty = Integer.parseInt(tfQty.getText().trim());

            if (name.isEmpty()) { UIHelper.msg(this, "Nom obligatoire."); return; }

            productDAO.insert(new Product(id, name, cat, price, qty));
            UIHelper.msg(this, "Produit ajouté: " + id);

            refresh(tfSearch.getText().trim());
            clear();

        } catch (Exception ex) {
            UIHelper.msg(this, "Vérifie les valeurs (prix/quantité).");
        }
    }

    private void update() {
        String id = tfId.getText().trim();
        if (id.isEmpty()) { UIHelper.msg(this, "Sélectionne un produit."); return; }

        Product p = productDAO.findById(id);
        if (p == null) { UIHelper.msg(this, "Produit introuvable."); return; }

        try {
            p.setName(tfName.getText().trim());
            p.setCategory(tfCat.getText().trim());
            p.setPrice(Double.parseDouble(tfPrice.getText().trim()));
            p.setQuantity(Integer.parseInt(tfQty.getText().trim()));

            productDAO.update(p);
            UIHelper.msg(this, "Produit modifié.");
            refresh(tfSearch.getText().trim());

        } catch (Exception ex) {
            UIHelper.msg(this, "Valeurs invalides.");
        }
    }

    private void delete() {
        String id = tfId.getText().trim();
        if (id.isEmpty()) { UIHelper.msg(this, "Sélectionne un produit."); return; }
        if (!UIHelper.confirm(this, "Supprimer le produit " + id + " ?")) return;

        productDAO.delete(id);
        UIHelper.msg(this, "Produit supprimé.");
        refresh(tfSearch.getText().trim());
        clear();
    }
}