package mapper;

import dto.OperationDTO;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class OperationMapperTest {
    @Test
    void testOperationMapper() {
        OperationDTO operation = new OperationDTO();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID func1Id = UUID.randomUUID();
        UUID func2Id = UUID.randomUUID();
        UUID resultId = UUID.randomUUID();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        operation.setId(id);
        operation.setUserId(userId);
        operation.setFunction1Id(func1Id);
        operation.setFunction2Id(func2Id);
        operation.setResultFunctionId(resultId);
        operation.setOperationType("addition");
        operation.setParameters("{\"param\":\"value\"}");
        operation.setComputedAt(now);

        assertEquals(id, operation.getId());
        assertEquals(userId, operation.getUserId());
        assertEquals(func1Id, operation.getFunction1Id());
        assertEquals(func2Id, operation.getFunction2Id());
        assertEquals(resultId, operation.getResultFunctionId());
        assertEquals("addition", operation.getOperationType());
        assertEquals("{\"param\":\"value\"}", operation.getParameters());
        assertEquals(now, operation.getComputedAt());
    }
}