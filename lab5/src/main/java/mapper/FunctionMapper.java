package mapper;

import dto.FunctionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class FunctionMapper {

    private static final Logger logger = LoggerFactory.getLogger(FunctionMapper.class);

    public static FunctionDTO toDTO(ResultSet rs) throws SQLException {
        logger.debug("Starting transformation of ResultSet to FunctionDTO");

        try {
            FunctionDTO functionDTO = new FunctionDTO();

            Long idLong = rs.getLong("id");
            if (!rs.wasNull()) {
                functionDTO.setId(convertLongToUUID(idLong));
            }

            Long userIdLong = rs.getLong("user_id");
            if (!rs.wasNull()) {
                functionDTO.setUserId(convertLongToUUID(userIdLong));
            }

            functionDTO.setName(rs.getString("name"));
            functionDTO.setType(rs.getString("type"));
            functionDTO.setExpression(rs.getString("expression"));
            functionDTO.setLeftBound(rs.getDouble("left_bound"));
            functionDTO.setRightBound(rs.getDouble("right_bound"));
            functionDTO.setPointsCount(rs.getInt("points_count"));
            functionDTO.setPointsData(rs.getString("points_data"));
            functionDTO.setCreatedAt(rs.getTimestamp("created_at"));

            logger.debug("Successfully transformed FunctionDTO for function: {}", functionDTO.getName());
            return functionDTO;

        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to FunctionDTO: {}", e.getMessage());
            throw e;
        }
    }

    private static UUID convertLongToUUID(Long value) {
        if (value == null) return null;
        return UUID.nameUUIDFromBytes(value.toString().getBytes());
    }
}