package mapper;

import dto.FunctionDTO;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class FunctionMapperTest {
    @Test
    void testFunctionMapper() {
        FunctionDTO function = new FunctionDTO();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        function.setId(id);
        function.setUserId(userId);
        function.setName("test function");
        function.setType("linear");
        function.setExpression("x+1");
        function.setLeftBound(0.0);
        function.setRightBound(10.0);
        function.setPointsCount(100);
        function.setPointsData("[{\"x\":0,\"y\":1}]");
        function.setCreatedAt(now);

        assertEquals(id, function.getId());
        assertEquals(userId, function.getUserId());
        assertEquals("test function", function.getName());
        assertEquals("linear", function.getType());
        assertEquals("x+1", function.getExpression());
        assertEquals(0.0, function.getLeftBound());
        assertEquals(10.0, function.getRightBound());
        assertEquals(100, function.getPointsCount());
        assertEquals("[{\"x\":0,\"y\":1}]", function.getPointsData());
        assertEquals(now, function.getCreatedAt());
    }
}