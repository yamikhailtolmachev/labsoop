package dao;

import dto.UserDTO;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {

    @Test
    void testUserDTOCreation() {
        UserDTO user = new UserDTO();
        UUID userId = UUID.randomUUID();

        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed_password");

        assertEquals(userId, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashed_password", user.getPasswordHash());
    }

    @Test
    void testUserDAOInstantiation() {
        UserDAO userDAO = new UserDAOImpl();
        assertNotNull(userDAO);
    }
}