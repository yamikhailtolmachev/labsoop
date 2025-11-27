package mapper;

import dto.OperationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class OperationMapper {
    private static final Logger logger = LoggerFactory.getLogger(OperationMapper.class);

    public static OperationDTO toDTO(ResultSet rs) throws SQLException {
        try {
            OperationDTO operationDTO = new OperationDTO();
            operationDTO.setId((UUID) rs.getObject("id"));
            operationDTO.setUserId((UUID) rs.getObject("user_id"));
            operationDTO.setFunction1Id((UUID) rs.getObject("function1_id"));
            operationDTO.setFunction2Id((UUID) rs.getObject("function2_id"));
            operationDTO.setResultFunctionId((UUID) rs.getObject("result_function_id"));
            operationDTO.setOperationType(rs.getString("operation_type"));
            operationDTO.setParameters(rs.getString("parameters"));
            operationDTO.setComputedAt(rs.getTimestamp("computed_at"));
            return operationDTO;
        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to OperationDTO: {}", e.getMessage());
            throw e;
        }
    }
}