package mapper;

import dto.CacheDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CacheMapper {

    private static final Logger logger = LoggerFactory.getLogger(CacheMapper.class);

    public static CacheDTO toDTO(ResultSet rs) throws SQLException {
        logger.debug("Starting transformation of ResultSet to CacheDTO");

        try {
            CacheDTO cacheDTO = new CacheDTO();

            cacheDTO.setCacheKey(rs.getString("cache_key"));

            Long userIdLong = rs.getLong("user_id");
            if (!rs.wasNull()) {
                cacheDTO.setUserId(convertLongToUUID(userIdLong));
            }

            cacheDTO.setFunctionExpression(rs.getString("function_expression"));
            cacheDTO.setLeftBound(rs.getDouble("left_bound"));
            cacheDTO.setRightBound(rs.getDouble("right_bound"));
            cacheDTO.setPointsCount(rs.getInt("points_count"));

            Long resultFunctionIdLong = rs.getLong("result_function_id");
            if (!rs.wasNull()) {
                cacheDTO.setResultFunctionId(convertLongToUUID(resultFunctionIdLong));
            }

            cacheDTO.setComputedAt(rs.getTimestamp("computed_at"));
            cacheDTO.setAccessCount(rs.getInt("access_count"));

            logger.debug("Successfully transformed CacheDTO for key: {}", cacheDTO.getCacheKey());
            return cacheDTO;

        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to CacheDTO: {}", e.getMessage());
            throw e;
        }
    }

    private static UUID convertLongToUUID(Long value) {
        if (value == null) return null;
        return UUID.nameUUIDFromBytes(value.toString().getBytes());
    }
}