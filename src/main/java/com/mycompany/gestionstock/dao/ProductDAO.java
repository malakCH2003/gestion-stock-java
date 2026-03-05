package dao;

import db.DB;
import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ===================== EXISTING =====================

    public List<Product> findAll(String q) {
        String sql = "SELECT id, name, category, price, quantity FROM products";
        boolean hasQ = q != null && !q.trim().isEmpty();
        if (hasQ) sql += " WHERE LOWER(id) LIKE ? OR LOWER(name) LIKE ? OR LOWER(category) LIKE ?";
        sql += " ORDER BY id";

        List<Product> out = new ArrayList<>();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (hasQ) {
                String like = "%" + q.toLowerCase() + "%";
                ps.setString(1, like);
                ps.setString(2, like);
                ps.setString(3, like);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new Product(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error findAll products", e);
        }
        return out;
    }

    public Product findById(String id) {
        String sql = "SELECT id, name, category, price, quantity FROM products WHERE id = ?";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Product(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error findById product", e);
        }
    }

    public void insert(Product p) {
        String sql = "INSERT INTO products(id, name, category, price, quantity) VALUES(?,?,?,?,?)";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getCategory());
            ps.setDouble(4, p.getPrice());
            ps.setInt(5, p.getQuantity());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error insert product", e);
        }
    }

    public void update(Product p) {
        String sql = "UPDATE products SET name=?, category=?, price=?, quantity=? WHERE id=?";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setString(2, p.getCategory());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getQuantity());
            ps.setString(5, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error update product", e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM products WHERE id=?";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error delete product", e);
        }
    }

    public int totalStock() {
        String sql = "SELECT COALESCE(SUM(quantity),0) AS s FROM products";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt("s");
        } catch (SQLException e) {
            throw new RuntimeException("DB error totalStock", e);
        }
    }

    public int countProducts() {
        String sql = "SELECT COUNT(*) AS n FROM products";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt("n");
        } catch (SQLException e) {
            throw new RuntimeException("DB error countProducts", e);
        }
    }

    // ===================== ADDED FOR REPORTS =====================

    /** Ligne pour l'écran Rapports (rupture / faible) */
    public static class AlertRow {
        public final String id;
        public final String name;
        public final int quantity;
        public final String status; // "Rupture" ou "Faible"

        public AlertRow(String id, String name, int quantity, String status) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.status = status;
        }
    }

    /** Liste des produits en alerte: quantité = 0 (Rupture) ou <= threshold (Faible) */
    public List<AlertRow> findAlerts(int threshold) {
        String sql =
                "SELECT id, name, quantity, " +
                "CASE WHEN quantity = 0 THEN 'Rupture' ELSE 'Faible' END AS status " +
                "FROM products " +
                "WHERE quantity = 0 OR quantity <= ? " +
                "ORDER BY quantity ASC, id ASC";

        List<AlertRow> out = new ArrayList<>();

        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, threshold);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new AlertRow(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getString("status")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error findAlerts", e);
        }

        return out;
    }

    /** KPI: nombre de produits en rupture (quantity = 0) */
    public int countRupture() {
        String sql = "SELECT COUNT(*) AS n FROM products WHERE quantity = 0";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt("n");
        } catch (SQLException e) {
            throw new RuntimeException("DB error countRupture", e);
        }
    }

    /** KPI: nombre de produits en stock faible (0 < quantity <= threshold) */
    public int countLowStock(int threshold) {
        String sql = "SELECT COUNT(*) AS n FROM products WHERE quantity > 0 AND quantity <= ?";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("n");
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error countLowStock", e);
        }
    }
}