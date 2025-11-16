package mapper;

import dto.UserDTO;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    @Test
    void testUserMapper() {
        UserDTO user = new UserDTO();
        UUID id = UUID.randomUUID();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        user.setId(id);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hash123");
        user.setCreatedAt(now);

        assertEquals(id, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hash123", user.getPasswordHash());
        assertEquals(now, user.getCreatedAt());
    }
}