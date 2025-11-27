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

    public List<FunctionDTO> findFunctionsByMultipleCriteria(UUID userId, String namePattern, String type,
                                                             Double minLeftBound, Double maxRightBound,
                                                             String sortBy, String sortOrder) {
        logger.info("Starting multiple criteria search for functions - user: {}, name: {}, type: {}",
                userId, namePattern, type);

        List<FunctionDTO> functions = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM functions WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append(" AND user_id = ?");
            params.add(userId);
        }

        if (namePattern != null && !namePattern.trim().isEmpty()) {
            sql.append(" AND name ILIKE ?");
            params.add("%" + namePattern + "%");
        }

        if (type != null && !type.trim().isEmpty()) {
            sql.append(" AND type = ?");
            params.add(type);
        }

        if (minLeftBound != null) {
            sql.append(" AND left_bound >= ?");
            params.add(minLeftBound);
        }

        if (maxRightBound != null) {
            sql.append(" AND right_bound <= ?");
            params.add(maxRightBound);
        }

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String validSortBy = getValidSortField(sortBy, "created_at");
            String validOrder = "DESC".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
            sql.append(" ORDER BY ").append(validSortBy).append(" ").append(validOrder);
        } else {
            sql.append(" ORDER BY created_at DESC");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                functions.add(mapResultSetToFunction(rs));
            }

            logger.debug("Found {} functions with multiple criteria search", functions.size());
            return functions;

        } catch (SQLException e) {
            logger.error("Error in multiple criteria function search", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<FunctionDTO> findFunctionDerivatives(UUID rootFunctionId) {
        logger.info("Starting depth-first search for function derivatives - root: {}", rootFunctionId);

        List<FunctionDTO> derivatives = new ArrayList<>();
        String sql = "WITH RECURSIVE function_tree AS (" +
                "    SELECT id, user_id, name, type, expression, left_bound, right_bound, " +
                "           points_count, points_data, created_at, 1 as level " +
                "    FROM functions " +
                "    WHERE id = ? " +
                "    " +
                "    UNION ALL " +
                "    " +
                "    SELECT f.id, f.user_id, f.name, f.type, f.expression, f.left_bound, " +
                "           f.right_bound, f.points_count, f.points_data, f.created_at, ft.level + 1 " +
                "    FROM functions f " +
                "    INNER JOIN operations o ON f.id = o.result_function_id " +
                "    INNER JOIN function_tree ft ON o.function1_id = ft.id OR o.function2_id = ft.id " +
                "    WHERE ft.level < 10 " +
                ") " +
                "SELECT * FROM function_tree WHERE id != ? ORDER BY level, created_at";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, rootFunctionId);
            stmt.setObject(2, rootFunctionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                derivatives.add(mapResultSetToFunction(rs));
            }

            logger.debug("Found {} function derivatives for root {}", derivatives.size(), rootFunctionId);
            return derivatives;

        } catch (SQLException e) {
            logger.error("Error in depth-first function search", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<FunctionDTO> findFunctionsByLevel(UUID userId, String period) {
        logger.info("Starting breadth-first search for functions - user: {}, period: {}", userId, period);

        List<FunctionDTO> functions = new ArrayList<>();
        String sql = "SELECT * FROM functions WHERE user_id = ? AND created_at >= CURRENT_DATE - INTERVAL ? ORDER BY created_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setString(2, period);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                functions.add(mapResultSetToFunction(rs));
            }

            logger.debug("Found {} functions for user {} in period {}", functions.size(), userId, period);
            return functions;

        } catch (SQLException e) {
            logger.error("Error in breadth-first function search", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<FunctionDTO> findFunctionsWithHighestPointCount(int limit) {
        logger.info("Searching for functions with highest point count - limit: {}", limit);

        List<FunctionDTO> functions = new ArrayList<>();
        String sql = "SELECT * FROM functions WHERE points_count IS NOT NULL ORDER BY points_count DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                functions.add(mapResultSetToFunction(rs));
            }

            logger.debug("Found {} functions with highest point count", functions.size());
            return functions;

        } catch (SQLException e) {
            logger.error("Error finding functions with highest point count", e);
            throw new RuntimeException("Database error", e);
        }
    }

    private String getValidSortField(String requestedField, String defaultField) {
        String[] allowedFields = {"name", "type", "created_at", "updated_at", "points_count", "left_bound", "right_bound"};
        for (String field : allowedFields) {
            if (field.equalsIgnoreCase(requestedField)) {
                return field;
            }
        }
        return defaultField;
    }

    private FunctionDTO mapResultSetToFunction(ResultSet rs) throws SQLException {
        return mapper.FunctionMapper.toDTO(rs);
    }
}