package mapper;

import dto.UserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

            Array rolesArray = rs.getArray("roles");
            Set<String> roles = new HashSet<>();
            if (rolesArray != null) {
                String[] rolesFromDb = (String[]) rolesArray.getArray();
                if (rolesFromDb != null) {
                    roles.addAll(Arrays.asList(rolesFromDb));
                }
            } else {
                roles.add("USER");
            }
            userDTO.setRoles(roles);

            return userDTO;
        } catch (SQLException e) {
            logger.error("Error transforming ResultSet to UserDTO: {}", e.getMessage());
            throw e;
        }
    }

    public static Array toRolesArray(java.sql.Connection conn, Set<String> roles) throws SQLException {
        if (roles == null || roles.isEmpty()) {
            return conn.createArrayOf("text", new String[]{"USER"});
        }
        return conn.createArrayOf("text", roles.toArray(new String[0]));
    }
}