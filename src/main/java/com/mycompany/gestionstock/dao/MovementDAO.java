package dao;

import db.DB;
import model.StockMovement;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovementDAO {

    public void insert(String productId, StockMovement.Type type, int qty, LocalDateTime date, String note) {
        String sql = "INSERT INTO movements(product_id, type, qty, date, note) VALUES(?,?,?,?,?)";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, productId);
            ps.setString(2, type.name());
            ps.setInt(3, qty);
            ps.setTimestamp(4, Timestamp.valueOf(date));
            ps.setString(5, note);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB error insert movement", e);
        }
    }

    public int countMovements() {
        String sql = "SELECT COUNT(*) AS n FROM movements";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getInt("n");
        } catch (SQLException e) {
            throw new RuntimeException("DB error countMovements", e);
        }
    }

    public List<StockMovement> findRecent(int limit) {
        String sql = "SELECT product_id, type, qty, date, note FROM movements ORDER BY date DESC LIMIT ?";
        List<StockMovement> out = new ArrayList<>();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new StockMovement(
                            rs.getString("product_id"),
                            StockMovement.Type.valueOf(rs.getString("type")),
                            rs.getInt("qty"),
                            rs.getTimestamp("date").toLocalDateTime(),
                            rs.getString("note")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error findRecent movements", e);
        }
        return out;
    }

    public List<StockMovement> findAll() {
        String sql = "SELECT product_id, type, qty, date, note FROM movements ORDER BY date DESC";
        List<StockMovement> out = new ArrayList<>();
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new StockMovement(
                        rs.getString("product_id"),
                        StockMovement.Type.valueOf(rs.getString("type")),
                        rs.getInt("qty"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("note")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error findAll movements", e);
        }
        return out;
    }
}