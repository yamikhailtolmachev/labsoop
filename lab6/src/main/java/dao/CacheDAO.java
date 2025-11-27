package dao;

import dto.CacheDTO;
import java.util.List;
import java.util.UUID;

public interface CacheDAO {
    void insertCache(CacheDTO cache);
    CacheDTO findCacheByKey(String cacheKey);
    List<CacheDTO> findCacheByUserId(UUID userId);
    List<CacheDTO> findAllCache();
    void updateCacheAccess(String cacheKey);
    void deleteCache(String cacheKey);

    List<CacheDTO> findCacheByMultipleCriteria(UUID userId, String expressionPattern, Integer minPoints, Integer maxPoints, String sortBy, String sortOrder);
    List<CacheDTO> findMostAccessedCache(int limit);
    List<CacheDTO> findRecentCache(UUID userId, int days);
}