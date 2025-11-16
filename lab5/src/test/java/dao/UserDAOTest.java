package dao;

import dto.UserDTO;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    private UserDAO userDAO = new UserDAOImpl();

    @Test
    void testInsertAndFindUser() {
        UserDTO user = TestDataGenerator.generateTestUser();
        UUID userId = userDAO.insertUser(user);

        UserDTO found = userDAO.findUserById(userId);
        assertNotNull(found);
        assertEquals(user.getUsername(), found.getUsername());
    }

    @Test
    void testFindUserByUsername() {
        UserDTO user = TestDataGenerator.generateTestUser();
        userDAO.insertUser(user);

        UserDTO found = userDAO.findUserByUsername(user.getUsername());
        assertNotNull(found);
        assertEquals(user.getEmail(), found.getEmail());
    }

    @Test
    void testUpdateUser() {
        UserDTO user = TestDataGenerator.generateTestUser();
        UUID userId = userDAO.insertUser(user);

        user.setId(userId);
        user.setUsername("updated_username");
        userDAO.updateUser(user);

        UserDTO updated = userDAO.findUserById(userId);
        assertEquals("updated_username", updated.getUsername());
    }

    @Test
    void testDeleteUser() {
        UserDTO user = TestDataGenerator.generateTestUser();
        UUID userId = userDAO.insertUser(user);

        userDAO.deleteUser(userId);

        UserDTO deleted = userDAO.findUserById(userId);
        assertNull(deleted);
    }
}