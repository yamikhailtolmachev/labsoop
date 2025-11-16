package dao;

import dto.UserDTO;
import dto.FunctionDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class FunctionDAOTest {
    private UserDAO userDAO = new UserDAOImpl();
    private FunctionDAO functionDAO = new FunctionDAOImpl();
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        UserDTO user = TestDataGenerator.generateTestUser();
        testUserId = userDAO.insertUser(user);
    }

    @Test
    void testInsertAndFindFunction() {
        FunctionDTO function = TestDataGenerator.generateBasicFunction(testUserId);
        UUID functionId = functionDAO.insertFunction(function);

        FunctionDTO found = functionDAO.findFunctionById(functionId);
        assertNotNull(found);
        assertEquals(function.getName(), found.getName());
    }

    @Test
    void testFindFunctionsByUser() {
        FunctionDTO func1 = TestDataGenerator.generateBasicFunction(testUserId);
        FunctionDTO func2 = TestDataGenerator.generateSineFunction(testUserId);

        functionDAO.insertFunction(func1);
        functionDAO.insertFunction(func2);

        List<FunctionDTO> functions = functionDAO.findFunctionsByUserId(testUserId);
        assertEquals(2, functions.size());
    }

    @Test
    void testFindFunctionsByType() {
        FunctionDTO function = TestDataGenerator.generateBasicFunction(testUserId);
        functionDAO.insertFunction(function);

        List<FunctionDTO> functions = functionDAO.findFunctionsByType("BASIC");
        assertFalse(functions.isEmpty());
    }

    @Test
    void testUpdateFunction() {
        FunctionDTO function = TestDataGenerator.generateBasicFunction(testUserId);
        UUID functionId = functionDAO.insertFunction(function);

        function.setId(functionId);
        function.setName("Updated Function");
        functionDAO.updateFunction(function);

        FunctionDTO updated = functionDAO.findFunctionById(functionId);
        assertEquals("Updated Function", updated.getName());
    }

    @Test
    void testDeleteFunction() {
        FunctionDTO function = TestDataGenerator.generateBasicFunction(testUserId);
        UUID functionId = functionDAO.insertFunction(function);

        functionDAO.deleteFunction(functionId);

        FunctionDTO deleted = functionDAO.findFunctionById(functionId);
        assertNull(deleted);
    }
}