package dao;

import db.DB;
import java.sql.*;

public class UserDAO {
    public boolean checkLogin(String username, String password) {
        String sql = "SELECT 1 FROM users WHERE username=? AND password=? LIMIT 1";
        try (Connection c = DB.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}