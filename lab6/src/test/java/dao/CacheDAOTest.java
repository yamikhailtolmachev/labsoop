package dao;

import dto.CacheDTO;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class CacheDAOTest {

    @Test
    void testCacheDTOCreation() {
        CacheDTO cache = new CacheDTO();
        UUID userId = UUID.randomUUID();

        cache.setCacheKey("test_key");
        cache.setUserId(userId);
        cache.setFunctionExpression("x^2");
        cache.setLeftBound(-10.0);
        cache.setRightBound(10.0);
        cache.setPointsCount(100);

        assertEquals("test_key", cache.getCacheKey());
        assertEquals(userId, cache.getUserId());
        assertEquals("x^2", cache.getFunctionExpression());
        assertEquals(-10.0, cache.getLeftBound());
        assertEquals(10.0, cache.getRightBound());
        assertEquals(100, cache.getPointsCount());
    }

    @Test
    void testCacheDAOInstantiation() {
        CacheDAO cacheDAO = new CacheDAOImpl();
        assertNotNull(cacheDAO);
    }
}