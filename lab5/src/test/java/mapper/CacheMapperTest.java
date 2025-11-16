package mapper;

import dto.CacheDTO;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class CacheMapperTest {
    @Test
    void testCacheMapper() {
        CacheDTO cache = new CacheDTO();
        UUID userId = UUID.randomUUID();
        UUID resultId = UUID.randomUUID();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        cache.setCacheKey("key123");
        cache.setUserId(userId);
        cache.setFunctionExpression("x*2");
        cache.setLeftBound(0.0);
        cache.setRightBound(5.0);
        cache.setPointsCount(50);
        cache.setResultFunctionId(resultId);
        cache.setComputedAt(now);
        cache.setAccessCount(5);

        assertEquals("key123", cache.getCacheKey());
        assertEquals(userId, cache.getUserId());
        assertEquals("x*2", cache.getFunctionExpression());
        assertEquals(0.0, cache.getLeftBound());
        assertEquals(5.0, cache.getRightBound());
        assertEquals(50, cache.getPointsCount());
        assertEquals(resultId, cache.getResultFunctionId());
        assertEquals(now, cache.getComputedAt());
        assertEquals(5, cache.getAccessCount());
    }
}