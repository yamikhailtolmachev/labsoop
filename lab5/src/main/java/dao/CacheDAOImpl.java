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

    private CacheDTO mapResultSetToCache(ResultSet rs) throws SQLException {
        CacheDTO cache = new CacheDTO();
        cache.setCacheKey(rs.getString("cache_key"));
        cache.setUserId((UUID) rs.getObject("user_id"));
        cache.setFunctionExpression(rs.getString("function_expression"));
        cache.setLeftBound(rs.getDouble("left_bound"));
        cache.setRightBound(rs.getDouble("right_bound"));
        cache.setPointsCount(rs.getInt("points_count"));
        cache.setResultFunctionId((UUID) rs.getObject("result_function_id"));
        cache.setComputedAt(rs.getTimestamp("computed_at"));
        cache.setAccessCount(rs.getInt("access_count"));
        return cache;
    }
}