package dao;

import dto.CacheDTO;
import database.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CacheDAOImpl implements CacheDAO {
    private static final Logger logger = LoggerFactory.getLogger(CacheDAOImpl.class);

    public void insertCache(CacheDTO cache) {
        String sql = "INSERT INTO computation_cache (cache_key, user_id, function_expression, left_bound, right_bound, points_count, result_function_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cache.getCacheKey());
            stmt.setObject(2, cache.getUserId());
            stmt.setString(3, cache.getFunctionExpression());
            stmt.setDouble(4, cache.getLeftBound());
            stmt.setDouble(5, cache.getRightBound());
            stmt.setInt(6, cache.getPointsCount());
            stmt.setObject(7, cache.getResultFunctionId());
            stmt.executeUpdate();
            logger.debug("Cache inserted: {}", cache.getCacheKey());
        } catch (SQLException e) {
            logger.error("Error inserting cache", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public CacheDTO findCacheByKey(String cacheKey) {
        String sql = "SELECT * FROM computation_cache WHERE cache_key = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cacheKey);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCache(rs);
            }
            return null;
        } catch (SQLException e) {
            logger.error("Error finding cache by key: {}", cacheKey, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<CacheDTO> findCacheByUserId(UUID userId) {
        String sql = "SELECT * FROM computation_cache WHERE user_id = ?";
        List<CacheDTO> cacheEntries = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cacheEntries.add(mapResultSetToCache(rs));
            }
            return cacheEntries;
        } catch (SQLException e) {
            logger.error("Error finding cache by user: {}", userId, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<CacheDTO> findAllCache() {
        String sql = "SELECT * FROM computation_cache";
        List<CacheDTO> cacheEntries = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                cacheEntries.add(mapResultSetToCache(rs));
            }
            return cacheEntries;
        } catch (SQLException e) {
            logger.error("Error finding all cache", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public void updateCacheAccess(String cacheKey) {
        String sql = "UPDATE computation_cache SET access_count = access_count + 1 WHERE cache_key = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cacheKey);
            stmt.executeUpdate();
            logger.debug("Cache access updated: {}", cacheKey);
        } catch (SQLException e) {
            logger.error("Error updating cache access: {}", cacheKey, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public void deleteCache(String cacheKey) {
        String sql = "DELETE FROM computation_cache WHERE cache_key = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cacheKey);
            stmt.executeUpdate();
            logger.debug("Cache deleted: {}", cacheKey);
        } catch (SQLException e) {
            logger.error("Error deleting cache: {}", cacheKey, e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<CacheDTO> findCacheByMultipleCriteria(UUID userId, String expressionPattern,
                                                      Integer minPoints, Integer maxPoints,
                                                      String sortBy, String sortOrder) {
        logger.info("Starting multiple criteria search for cache - user: {}, expression: {}",
                userId, expressionPattern);

        List<CacheDTO> cacheEntries = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM computation_cache WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (userId != null) {
            sql.append(" AND user_id = ?");
            params.add(userId);
        }

        if (expressionPattern != null && !expressionPattern.trim().isEmpty()) {
            sql.append(" AND function_expression ILIKE ?");
            params.add("%" + expressionPattern + "%");
        }

        if (minPoints != null) {
            sql.append(" AND points_count >= ?");
            params.add(minPoints);
        }

        if (maxPoints != null) {
            sql.append(" AND points_count <= ?");
            params.add(maxPoints);
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
                cacheEntries.add(mapResultSetToCache(rs));
            }

            logger.debug("Found {} cache entries with multiple criteria search", cacheEntries.size());
            return cacheEntries;

        } catch (SQLException e) {
            logger.error("Error in multiple criteria cache search", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<CacheDTO> findMostAccessedCache(int limit) {
        logger.info("Searching for most accessed cache entries - limit: {}", limit);

        List<CacheDTO> cacheEntries = new ArrayList<>();
        String sql = "SELECT * FROM computation_cache ORDER BY access_count DESC, computed_at DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                cacheEntries.add(mapResultSetToCache(rs));
            }

            logger.debug("Found {} most accessed cache entries", cacheEntries.size());
            return cacheEntries;

        } catch (SQLException e) {
            logger.error("Error finding most accessed cache", e);
            throw new RuntimeException("Database error", e);
        }
    }

    public List<CacheDTO> findRecentCache(UUID userId, int days) {
        logger.info("Searching for recent cache entries - user: {}, days: {}", userId, days);

        List<CacheDTO> cacheEntries = new ArrayList<>();
        String sql = "SELECT * FROM computation_cache WHERE user_id = ? AND computed_at >= CURRENT_DATE - INTERVAL ? || ' days' ORDER BY computed_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setInt(2, days);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                cacheEntries.add(mapResultSetToCache(rs));
            }

            logger.debug("Found {} recent cache entries for user {}", cacheEntries.size(), userId);
            return cacheEntries;

        } catch (SQLException e) {
            logger.error("Error finding recent cache", e);
            throw new RuntimeException("Database error", e);
        }
    }

    private String getValidSortField(String requestedField, String defaultField) {
        String[] allowedFields = {"cache_key", "function_expression", "computed_at", "access_count", "points_count"};
        for (String field : allowedFields) {
            if (field.equalsIgnoreCase(requestedField)) {
                return field;
            }
        }
        return defaultField;
    }

    private CacheDTO mapResultSetToCache(ResultSet rs) throws SQLException {
        return mapper.CacheMapper.toDTO(rs);
    }
}