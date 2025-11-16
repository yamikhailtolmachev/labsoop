package mapper;

import dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserMapper {

    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    public static UserDTO toDTO(ResultSet rs) throws SQLException {
        logger.debug("Starting transformation of ResultSet to UserDTO");

        try {
            UserDTO userDTO = new UserDTO();

            Long idLong = rs.getLong("id");
            if (!rs.wasNull()) {
                userDTO.setId(convertLongToUUID(idLong));
            }

            userDTO.setUsername(rs.getString("username"));
            userDTO.setEmail(rs.getString("email"));
            userDTO.setPasswordHash(rs.getString("password_hash"));
            userDTO.setCreatedAt(rs.getTimestamp("created_at"));

            logger.debug("Successfully transformed UserDTO for user: {}", userDTO.getUsername());
            return userDTO;

        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to UserDTO: {}", e.getMessage());
            throw e;
        }
    }

    private static UUID convertLongToUUID(Long value) {
        if (value == null) return null;
        return UUID.nameUUIDFromBytes(value.toString().getBytes());
    }
}