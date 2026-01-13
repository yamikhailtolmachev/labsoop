package dao;

import dto.OperationDTO;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class OperationDAOTest {

    @Test
    void testOperationDTOCreation() {
        OperationDTO operation = new OperationDTO();
        UUID operationId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        operation.setId(operationId);
        operation.setUserId(userId);
        operation.setOperationType("ADD");
        operation.setParameters("{\"param\": \"value\"}");

        assertEquals(operationId, operation.getId());
        assertEquals(userId, operation.getUserId());
        assertEquals("ADD", operation.getOperationType());
    }

    @Test
    void testOperationDAOInstantiation() {
        OperationDAO operationDAO = new OperationDAOImpl();
        assertNotNull(operationDAO);
    }
}