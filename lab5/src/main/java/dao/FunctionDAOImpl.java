package dao;

import dto.FunctionDTO;
import database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FunctionDAOImpl implements FunctionDAO {
    private static final Logger logger = LoggerFactory.getLogger(FunctionDAOImpl.class);

    public UUID insertFunction(FunctionDTO function) {
        String sql = "INSERT INTO functions (id, user_id, name, type, expression, left_bound, right_bound, points_count, points_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            UUID functionId = UUID.randomUUID();
            stmt.setObject(1, functionId);
            stmt.setObject(2, function.getUserId());
            stmt.setString(3, function.getName());
            stmt.setString(4, function.getType());
            stmt.setString(5, function.getExpression());
            stmt.setDouble(6, function.getLeftBound());
            stmt.setDouble(7, function.getRightBound());
            stmt.setInt(8, function.getPointsCount());
            stmt.setString(9, function.getPointsData());
            stmt.executeUpdate();
            logger.debug("Function inserted: {}", functionId);
            return functionId;
        } catch (SQLException e) {
            logger.error("Error inserting function", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public FunctionDTO findFunctionById(UUID id) {
        String sql = "SELECT * FROM functions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToFunction(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding function by id: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<FunctionDTO> findFunctionsByUserId(UUID userId) {
        String sql = "SELECT * FROM functions WHERE user_id = ?";
        List<FunctionDTO> functions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                functions.add(mapResultSetToFunction(rs));
            }
            return functions;
        } catch (SQLException e) {
            logger.error("Error finding functions by user: {}", userId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<FunctionDTO> findFunctionsByType(String type) {
        String sql = "SELECT * FROM functions WHERE type = ?";
        List<FunctionDTO> functions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                functions.add(mapResultSetToFunction(rs));
            }
            return functions;
        } catch (SQLException e) {
            logger.error("Error finding functions by type: {}", type, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<FunctionDTO> findAllFunctions() {
        String sql = "SELECT * FROM functions";
        List<FunctionDTO> functions = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                functions.add(mapResultSetToFunction(rs));
            }
            return functions;
        } catch (SQLException e) {
            logger.error("Error finding all functions", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public void updateFunction(FunctionDTO function) {
        String sql = "UPDATE functions SET name = ?, type = ?, expression = ?, left_bound = ?, right_bound = ?, points_count = ?, points_data = ?::jsonb WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, function.getName());
            stmt.setString(2, function.getType());
            stmt.setString(3, function.getExpression());
            stmt.setDouble(4, function.getLeftBound());
            stmt.setDouble(5, function.getRightBound());
            stmt.setInt(6, function.getPointsCount());
            stmt.setString(7, function.getPointsData());
            stmt.setObject(8, function.getId());
            stmt.executeUpdate();
            logger.debug("Function updated: {}", function.getId());
        } catch (SQLException e) {
            logger.error("Error updating function: {}", function.getId(), e);
            throw new RuntimeException("Database error", e);
        }
    }

    public void deleteFunction(UUID id) {
        String sql = "DELETE FROM functions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
            logger.debug("Function deleted: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting function: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    private FunctionDTO mapResultSetToFunction(ResultSet rs) throws SQLException {
        FunctionDTO function = new FunctionDTO();
        function.setId((UUID) rs.getObject("id"));
        function.setUserId((UUID) rs.getObject("user_id"));
        function.setName(rs.getString("name"));
        function.setType(rs.getString("type"));
        function.setExpression(rs.getString("expression"));
        function.setLeftBound(rs.getDouble("left_bound"));
        function.setRightBound(rs.getDouble("right_bound"));
        function.setPointsCount(rs.getInt("points_count"));
        function.setPointsData(rs.getString("points_data"));
        function.setCreatedAt(rs.getTimestamp("created_at"));
        return function;
    }
}