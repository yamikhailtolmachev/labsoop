package dao;

import dto.UserDTO;
import dto.FunctionDTO;
import dto.CacheDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

public class CacheDAOTest {
    private UserDAO userDAO = new UserDAOImpl();
    private FunctionDAO functionDAO = new FunctionDAOImpl();
    private CacheDAO cacheDAO = new CacheDAOImpl();
    private UUID testUserId;
    private UUID testFunctionId;

    @BeforeEach
    void setUp() {
        UserDTO user = TestDataGenerator.generateTestUser();
        testUserId = userDAO.insertUser(user);

        FunctionDTO function = TestDataGenerator.generateBasicFunction(testUserId);
        testFunctionId = functionDAO.insertFunction(function);
    }

    @Test
    void testInsertAndFindCache() {
        CacheDTO cache = TestDataGenerator.generateCacheEntry(testUserId, testFunctionId);
        cacheDAO.insertCache(cache);

        CacheDTO found = cacheDAO.findCacheByKey(cache.getCacheKey());
        assertNotNull(found);
        assertEquals(cache.getFunctionExpression(), found.getFunctionExpression());
    }

    @Test
    void testFindCacheByUser() {
        CacheDTO cache = TestDataGenerator.generateCacheEntry(testUserId, testFunctionId);
        cacheDAO.insertCache(cache);

        List<CacheDTO> cacheEntries = cacheDAO.findCacheByUserId(testUserId);
        assertFalse(cacheEntries.isEmpty());
    }

    @Test
    void testUpdateCacheAccess() {
        CacheDTO cache = TestDataGenerator.generateCacheEntry(testUserId, testFunctionId);
        cacheDAO.insertCache(cache);

        cacheDAO.updateCacheAccess(cache.getCacheKey());

        CacheDTO updated = cacheDAO.findCacheByKey(cache.getCacheKey());
        assertEquals(2, updated.getAccessCount());
    }

    @Test
    void testDeleteCache() {
        CacheDTO cache = TestDataGenerator.generateCacheEntry(testUserId, testFunctionId);
        cacheDAO.insertCache(cache);

        cacheDAO.deleteCache(cache.getCacheKey());

        CacheDTO deleted = cacheDAO.findCacheByKey(cache.getCacheKey());
        assertNull(deleted);
    }
}