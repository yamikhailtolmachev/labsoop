package dao;

import dto.UserDTO;
import mapper.UserMapper;
import java.sql.*;
import java.util.*;
import java.util.UUID;
import database.DatabaseConnection;
import util.PasswordUtil;

public class UserDAOImpl implements UserDAO {

    private Connection getConnection() throws SQLException {
        return DatabaseConnection.getConnection();
    }

    @Override
    public UserDTO findUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return UserMapper.mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by username: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<UserDTO> findAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(UserMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding all users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public UserDTO findUserById(UUID id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id, java.sql.Types.OTHER);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return UserMapper.mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public UUID insertUser(UserDTO user) {
        System.out.println("UserDAOImpl.insertUser called for user: " + user.getUsername());
        String sql = "INSERT INTO users (id, username, email, password_hash, roles, created_at, updated_at) VALUES (?, ?, ?, ?, ?::text[], ?, ?)";
        UUID id = user.getId() != null ? user.getId() : UUID.randomUUID();

        System.out.println("SQL: " + sql);
        System.out.println("Parameters:");
        System.out.println("  id: " + id);
        System.out.println("  username: " + user.getUsername());
        System.out.println("  email: " + user.getEmail());
        System.out.println("  password_hash: " + user.getPasswordHash());

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String rolesJson = UserMapper.serializeRoles(user.getRoles());
            System.out.println("  roles (serialized): " + rolesJson);

            stmt.setObject(1, id, java.sql.Types.OTHER);
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPasswordHash());
            stmt.setString(5, rolesJson);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            stmt.setTimestamp(6, now);
            stmt.setTimestamp(7, now);

            System.out.println("Executing update...");
            int rows = stmt.executeUpdate();
            System.out.println("Rows affected: " + rows);

            if (rows > 0) {
                System.out.println("SUCCESS: User inserted with ID: " + id);
                return id;
            } else {
                System.out.println("ERROR: No rows affected");
                return null;
            }

        } catch (SQLException e) {
            System.out.println("SQLException in insertUser:");
            e.printStackTrace(System.out);
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Message: " + e.getMessage());
            return null;
        }
    }

    @Override
    public boolean updateUser(UserDTO user) {
        String sql = "UPDATE users SET username = ?, email = ?, roles = ?::text[], updated_at = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());

            String rolesJson = UserMapper.serializeRoles(user.getRoles());
            stmt.setString(3, rolesJson);

            stmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            stmt.setString(5, user.getId().toString());

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteUser(UUID id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id, java.sql.Types.OTHER);
            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<UserDTO> findUsersByMultipleCriteria(String usernamePattern, String emailPattern,
                                                     String sortBy, String sortOrder) {
        List<UserDTO> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (usernamePattern != null && !usernamePattern.isEmpty()) {
            sql.append(" AND username ILIKE ?");
            params.add("%" + usernamePattern + "%");
        }

        if (emailPattern != null && !emailPattern.isEmpty()) {
            sql.append(" AND email ILIKE ?");
            params.add("%" + emailPattern + "%");
        }

        if (sortBy != null && !sortBy.isEmpty()) {
            sql.append(" ORDER BY ").append(sortBy);
            if (sortOrder != null && !sortOrder.isEmpty()) {
                sql.append(" ").append(sortOrder);
            }
        } else {
            sql.append(" ORDER BY created_at DESC");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(UserMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding users by criteria: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public List<UserDTO> findRecentUsers(int days) {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE created_at >= CURRENT_TIMESTAMP - INTERVAL ? DAY ORDER BY created_at DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, days);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(UserMapper.mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error finding recent users: " + e.getMessage());
            e.printStackTrace();
        }
        return users;
    }
}