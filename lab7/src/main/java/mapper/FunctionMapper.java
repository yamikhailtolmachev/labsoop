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
        try {
            FunctionDTO functionDTO = new FunctionDTO();
            functionDTO.setId((UUID) rs.getObject("id"));
            functionDTO.setUserId((UUID) rs.getObject("user_id"));
            functionDTO.setName(rs.getString("name"));
            functionDTO.setType(rs.getString("type"));
            functionDTO.setExpression(rs.getString("expression"));
            functionDTO.setLeftBound(rs.getDouble("left_bound"));
            functionDTO.setRightBound(rs.getDouble("right_bound"));
            functionDTO.setPointsCount(rs.getInt("points_count"));
            functionDTO.setPointsData(rs.getString("points_data"));
            try {
                functionDTO.setCreatedAt(rs.getTimestamp("created_at"));
            } catch (SQLException e) {
            }
            return functionDTO;
        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to FunctionDTO: {}", e.getMessage());
            throw e;
        }
    }
}