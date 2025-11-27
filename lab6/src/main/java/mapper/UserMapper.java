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
        try {
            UserDTO userDTO = new UserDTO();
            userDTO.setId((UUID) rs.getObject("id"));
            userDTO.setUsername(rs.getString("username"));
            userDTO.setEmail(rs.getString("email"));
            userDTO.setPasswordHash(rs.getString("password_hash"));
            userDTO.setCreatedAt(rs.getTimestamp("created_at"));
            return userDTO;
        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to UserDTO: {}", e.getMessage());
            throw e;
        }
    }
}