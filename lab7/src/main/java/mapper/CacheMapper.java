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
        try {
            CacheDTO cacheDTO = new CacheDTO();
            cacheDTO.setCacheKey(rs.getString("cache_key"));
            cacheDTO.setUserId((UUID) rs.getObject("user_id"));
            cacheDTO.setFunctionExpression(rs.getString("function_expression"));
            cacheDTO.setLeftBound(rs.getDouble("left_bound"));
            cacheDTO.setRightBound(rs.getDouble("right_bound"));
            cacheDTO.setPointsCount(rs.getInt("points_count"));
            cacheDTO.setResultFunctionId((UUID) rs.getObject("result_function_id"));
            cacheDTO.setComputedAt(rs.getTimestamp("computed_at"));
            cacheDTO.setAccessCount(rs.getInt("access_count"));
            return cacheDTO;
        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to CacheDTO: {}", e.getMessage());
            throw e;
        }
    }
}