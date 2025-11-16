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

    private OperationDTO mapResultSetToOperation(ResultSet rs) throws SQLException {
        return mapper.OperationMapper.toDTO(rs);
    }
}