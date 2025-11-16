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
        logger.debug("Starting transformation of ResultSet to OperationDTO");

        try {
            OperationDTO operationDTO = new OperationDTO();

            Long idLong = rs.getLong("id");
            if (!rs.wasNull()) {
                operationDTO.setId(convertLongToUUID(idLong));
            }

            Long userIdLong = rs.getLong("user_id");
            if (!rs.wasNull()) {
                operationDTO.setUserId(convertLongToUUID(userIdLong));
            }

            Long function1IdLong = rs.getLong("function1_id");
            if (!rs.wasNull()) {
                operationDTO.setFunction1Id(convertLongToUUID(function1IdLong));
            }

            Long function2IdLong = rs.getLong("function2_id");
            if (!rs.wasNull()) {
                operationDTO.setFunction2Id(convertLongToUUID(function2IdLong));
            }

            Long resultFunctionIdLong = rs.getLong("result_function_id");
            if (!rs.wasNull()) {
                operationDTO.setResultFunctionId(convertLongToUUID(resultFunctionIdLong));
            }

            operationDTO.setOperationType(rs.getString("operation_type"));
            operationDTO.setParameters(rs.getString("parameters"));
            operationDTO.setComputedAt(rs.getTimestamp("computed_at"));

            logger.debug("Successfully transformed OperationDTO");
            return operationDTO;

        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to OperationDTO: {}", e.getMessage());
            throw e;
        }
    }

    private static UUID convertLongToUUID(Long value) {
        if (value == null) return null;
        return UUID.nameUUIDFromBytes(value.toString().getBytes());
    }
}