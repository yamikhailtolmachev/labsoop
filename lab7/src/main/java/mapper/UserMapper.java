package mapper;

import dto.UserDTO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Array;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class UserMapper {

    public static UserDTO mapRow(ResultSet rs) throws SQLException {
        UserDTO user = new UserDTO();

        String idStr = rs.getString("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                user.setId(UUID.fromString(idStr));
            } catch (IllegalArgumentException e) {
                user.setId(UUID.randomUUID());
            }
        }

        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));

        Array rolesArray = rs.getArray("roles");
        List<String> roles = new ArrayList<>();

        if (rolesArray != null) {
            String[] roleStrings = (String[]) rolesArray.getArray();
            if (roleStrings != null) {
                roles.addAll(Arrays.asList(roleStrings));
            }
        }

        if (roles.isEmpty()) {
            roles.add("USER");
        }

        user.setRoles(roles);
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));

        return user;
    }

    public static List<String> parseRoles(String rolesString) {
        List<String> roles = new ArrayList<>();

        if (rolesString != null && !rolesString.trim().isEmpty()) {
            rolesString = rolesString.replace("[", "").replace("]", "");
            rolesString = rolesString.replace("\"", "").replace("'", "");

            String[] roleArray = rolesString.split(",");
            for (String role : roleArray) {
                String trimmedRole = role.trim();
                if (!trimmedRole.isEmpty() && !trimmedRole.equals("null")) {
                    roles.add(trimmedRole);
                }
            }
        }

        if (roles.isEmpty()) {
            roles.add("USER");
        }

        return roles;
    }

    public static String serializeRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return "{USER}";
        }

        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < roles.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"").append(roles.get(i)).append("\"");
        }
        sb.append("}");
        return sb.toString();
    }
}