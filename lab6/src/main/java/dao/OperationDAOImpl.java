package dao;

import dto.OperationDTO;
import database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OperationDAOImpl implements OperationDAO {
    private static final Logger logger = LoggerFactory.getLogger(OperationDAOImpl.class);

    public UUID insertOperation(OperationDTO operation) {
        String sql = "INSERT INTO operations (id, user_id, function1_id, function2_id, result_function_id, operation_type, parameters) VALUES (?, ?, ?, ?, ?, ?, ?::jsonb)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            UUID operationId = UUID.randomUUID();
            stmt.setObject(1, operationId);
            stmt.setObject(2, operation.getUserId());
            stmt.setObject(3, operation.getFunction1Id());
            stmt.setObject(4, operation.getFunction2Id());
            stmt.setObject(5, operation.getResultFunctionId());
            stmt.setString(6, operation.getOperationType());
            stmt.setString(7, operation.getParameters());
            stmt.executeUpdate();
            logger.debug("Operation inserted: {}", operationId);
            return operationId;
        } catch (SQLException e) {
            logger.error("Error inserting operation", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public OperationDTO findOperationById(UUID id) {
        String sql = "SELECT * FROM operations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToOperation(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding operation by id: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<OperationDTO> findOperationsByUserId(UUID userId) {
        String sql = "SELECT * FROM operations WHERE user_id = ?";
        List<OperationDTO> operations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }
            return operations;
        } catch (SQLException e) {
            logger.error("Error finding operations by user: {}", userId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<OperationDTO> findOperationsByType(String operationType) {
        String sql = "SELECT * FROM operations WHERE operation_type = ?";
        List<OperationDTO> operations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, operationType);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }
            return operations;
        } catch (SQLException e) {
            logger.error("Error finding operations by type: {}", operationType, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<OperationDTO> findAllOperations() {
        String sql = "SELECT * FROM operations";
        List<OperationDTO> operations = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }
            return operations;
        } catch (SQLException e) {
            logger.error("Error finding all operations", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public void deleteOperation(UUID id) {
        String sql = "DELETE FROM operations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, id);
            stmt.executeUpdate();
            logger.debug("Operation deleted: {}", id);
        } catch (SQLException e) {
            logger.error("Error deleting operation: {}", id, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<OperationDTO> findOperationsByMultipleCriteria(UUID userId, String operationType,
                                                               UUID functionId, String sortBy, String sortOrder) {
        logger.info("Starting multiple criteria search for operations - user: {}, type: {}, function: {}",
                userId, operationType, functionId);

        List<OperationDTO> operations = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM operations WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append(" AND user_id = ?");
            params.add(userId);
        }

        if (operationType != null && !operationType.trim().isEmpty()) {
            sql.append(" AND operation_type = ?");
            params.add(operationType);
        }

        if (functionId != null) {
            sql.append(" AND (function1_id = ? OR function2_id = ? OR result_function_id = ?)");
            params.add(functionId);
            params.add(functionId);
            params.add(functionId);
        }

        if (sortBy != null && !sortBy.trim().isEmpty()) {
            String validSortBy = getValidSortField(sortBy, "computed_at");
            String validOrder = "DESC".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
            sql.append(" ORDER BY ").append(validSortBy).append(" ").append(validOrder);
        } else {
            sql.append(" ORDER BY computed_at DESC");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }

            logger.debug("Found {} operations with multiple criteria search", operations.size());
            return operations;

        } catch (SQLException e) {
            logger.error("Error in multiple criteria operation search", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<OperationDTO> findOperationsByFunctionHierarchy(UUID rootFunctionId) {
        logger.info("Searching operations by function hierarchy - root: {}", rootFunctionId);

        List<OperationDTO> operations = new ArrayList<>();
        String sql = "WITH RECURSIVE function_hierarchy AS (" +
                "    SELECT id FROM functions WHERE id = ? " +
                "    UNION " +
                "    SELECT o.result_function_id " +
                "    FROM operations o " +
                "    INNER JOIN function_hierarchy fh ON o.function1_id = fh.id OR o.function2_id = fh.id " +
                ") " +
                "SELECT o.* " +
                "FROM operations o " +
                "WHERE o.function1_id IN (SELECT id FROM function_hierarchy) " +
                "   OR o.function2_id IN (SELECT id FROM function_hierarchy) " +
                "   OR o.result_function_id IN (SELECT id FROM function_hierarchy) " +
                "ORDER BY o.computed_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, rootFunctionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }

            logger.debug("Found {} operations in function hierarchy for root {}", operations.size(), rootFunctionId);
            return operations;

        } catch (SQLException e) {
            logger.error("Error searching operations by function hierarchy", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<OperationDTO> findRecentOperations(UUID userId, int days) {
        logger.info("Starting breadth-first search for recent operations - user: {}, days: {}", userId, days);

        List<OperationDTO> operations = new ArrayList<>();
        String sql = "SELECT * FROM operations WHERE user_id = ? AND computed_at >= CURRENT_DATE - INTERVAL ? || ' days' ORDER BY computed_at ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setInt(2, days);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }

            logger.debug("Found {} recent operations for user {}", operations.size(), userId);
            return operations;

        } catch (SQLException e) {
            logger.error("Error in breadth-first operation search", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<OperationDTO> findOperationChainDepthFirst(UUID functionId) {
        logger.info("Starting depth-first search for operation chain - function: {}", functionId);

        List<OperationDTO> operations = new ArrayList<>();
        String sql = "WITH RECURSIVE operation_chain AS (" +
                "    SELECT o.*, 1 as level " +
                "    FROM operations o " +
                "    WHERE o.result_function_id = ? " +
                "    " +
                "    UNION ALL " +
                "    " +
                "    SELECT o.*, oc.level + 1 " +
                "    FROM operations o " +
                "    INNER JOIN operation_chain oc ON o.result_function_id = oc.function1_id " +
                "        OR o.result_function_id = oc.function2_id " +
                "    WHERE oc.level < 15 " +
                ") " +
                "SELECT * FROM operation_chain ORDER BY level DESC, computed_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, functionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                operations.add(mapResultSetToOperation(rs));
            }

            logger.debug("Found {} operations in chain for function {}", operations.size(), functionId);
            return operations;

        } catch (SQLException e) {
            logger.error("Error in depth-first operation search", e);
            throw new RuntimeException("Database error", e);
        }
    }

    private String getValidSortField(String requestedField, String defaultField) {
        String[] allowedFields = {"operation_type", "computed_at", "updated_at"};
        for (String field : allowedFields) {
            if (field.equalsIgnoreCase(requestedField)) {
                return field;
            }
        }
        return defaultField;
    }

    private OperationDTO mapResultSetToOperation(ResultSet rs) throws SQLException {
        return mapper.OperationMapper.toDTO(rs);
    }
}