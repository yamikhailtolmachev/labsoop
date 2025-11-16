package dao;

import dto.FunctionDTO;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class FunctionDAOTest {

    @Test
    void testFunctionDTOCreation() {
        FunctionDTO function = new FunctionDTO();
        UUID functionId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        function.setId(functionId);
        function.setUserId(userId);
        function.setName("Quadratic");
        function.setType("BASIC");
        function.setExpression("x^2");

        assertEquals(functionId, function.getId());
        assertEquals(userId, function.getUserId());
        assertEquals("Quadratic", function.getName());
        assertEquals("BASIC", function.getType());
        assertEquals("x^2", function.getExpression());
    }

    @Test
    void testFunctionDAOInstantiation() {
        FunctionDAO functionDAO = new FunctionDAOImpl();
        assertNotNull(functionDAO);
    }
}