package dao;

import dto.UserDTO;
import database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(UserDAOImpl.class);

    public UUID insertUser(UserDTO user) {
        String sql = "INSERT INTO users (id, username, email, password_hash) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            UUID userId = UUID.randomUUID();
            stmt.setObject(1, userId);
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.executeUpdate();
            logger.debug("User inserted: {}", userId);
            return userId;
        } catch (SQLException e) {
            logger.error("Error inserting user", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public UserDTO findUserById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding user by id: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public UserDTO findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding user by username: {}", username, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<UserDTO> findAllUsers() {
        String sql = "SELECT * FROM users";
        List<UserDTO> users = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            return users;
        } catch (SQLException e) {
            logger.error("Error finding all users", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public void updateUser(UserDTO user) {
        String sql = "UPDATE users SET username = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setObject(3, user.getId());
            stmt.executeUpdate();
            logger.debug("User updated: {}", user.getId());
        } catch (SQLException e) {
            logger.error("Error updating user: {}", user.getId(), e);
            throw new RuntimeException("Database error", e);
        }
    }

    public void deleteUser(UUID id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
            logger.debug("User deleted: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting user: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    private UserDTO mapResultSetToUser(ResultSet rs) throws SQLException {
        return mapper.UserMapper.toDTO(rs);
    }
}