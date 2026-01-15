package lab5.repository;

import lab5.entity.ComputationCacheEntity;
import lab5.entity.FunctionEntity;
import lab5.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ComputationCacheRepositoryTest {
    @Autowired
    private ComputationCacheRepository cacheRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Test
    void shouldSaveAndFindCacheById() {
        UserEntity user = new UserEntity("cacheUser", "cu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);

        FunctionEntity resultFunc = new FunctionEntity();
        resultFunc.setUser(savedUser);
        resultFunc.setName("resultFunc");
        resultFunc.setType("OPERATION_RESULT");
        resultFunc.setExpression("cached_result");
        resultFunc.setLeftBound(0.0);
        resultFunc.setRightBound(1.0);
        resultFunc.setPointsCount(100);
        resultFunc.setPointsData("{\"points\": []}");
        resultFunc.setCreatedAt(LocalDateTime.now());
        resultFunc.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResultFunc = functionRepository.save(resultFunc);

        String cacheKey = "unique_cache_key_123";
        String pointsData = "{}";
        ComputationCacheEntity cache = new ComputationCacheEntity(cacheKey, savedUser, "x^2", 0.0, 1.0, 100, pointsData, savedResultFunc);
        cache.setComputedAt(LocalDateTime.now());
        cache.setUpdatedAt(LocalDateTime.now());

        ComputationCacheEntity saved = cacheRepository.save(cache);
        Long savedId = saved.getId();

        assertThat(savedId).isNotNull();
        Optional<ComputationCacheEntity> found = cacheRepository.findById(savedId);
        assertThat(found).isPresent();
        assertThat(found.get().getCacheKey()).isEqualTo(cacheKey);
        assertThat(found.get().getFunctionExpression()).isEqualTo("x^2");
        assertThat(found.get().getResultFunction().getId()).isEqualTo(savedResultFunc.getId());
    }

    @Test
    void shouldFindCacheByCacheKey() {
        UserEntity user = new UserEntity("cacheUser2", "cu2@example.com", "hash2");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);

        FunctionEntity resultFunc = new FunctionEntity();
        resultFunc.setUser(savedUser);
        resultFunc.setName("resultFunc2");
        resultFunc.setType("OPERATION_RESULT");
        resultFunc.setExpression("cached_result2");
        resultFunc.setLeftBound(0.0);
        resultFunc.setRightBound(2.0);
        resultFunc.setPointsCount(200);
        resultFunc.setPointsData("{\"points\": []}");
        resultFunc.setCreatedAt(LocalDateTime.now());
        resultFunc.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResultFunc = functionRepository.save(resultFunc);

        String cacheKey = "unique_cache_key_456";
        String pointsData = "{}";
        ComputationCacheEntity cache = new ComputationCacheEntity(cacheKey, savedUser, "sin(x)", 0.0, 2.0, 200, pointsData, savedResultFunc);
        cache.setComputedAt(LocalDateTime.now());
        cache.setUpdatedAt(LocalDateTime.now());
        cacheRepository.save(cache);

        Optional<ComputationCacheEntity> found = cacheRepository.findByCacheKey(cacheKey);

        assertThat(found).isPresent();
        assertThat(found.get().getFunctionExpression()).isEqualTo("sin(x)");
        assertThat(found.get().getCacheKey()).isEqualTo(cacheKey);
    }

    @Test
    void shouldDeleteCache() {
        UserEntity user = new UserEntity("delCacheUser", "dcu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);

        FunctionEntity resultFunc = new FunctionEntity();
        resultFunc.setUser(savedUser);
        resultFunc.setName("resultFunc3");
        resultFunc.setType("OPERATION_RESULT");
        resultFunc.setExpression("cached_result3");
        resultFunc.setLeftBound(-1.0);
        resultFunc.setRightBound(1.0);
        resultFunc.setPointsCount(50);
        resultFunc.setPointsData("{\"points\": []}");
        resultFunc.setCreatedAt(LocalDateTime.now());
        resultFunc.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResultFunc = functionRepository.save(resultFunc);

        String cacheKey = "unique_cache_key_789";
        String pointsData = "{}";
        ComputationCacheEntity cache = new ComputationCacheEntity(cacheKey, savedUser, "cos(x)", -1.0, 1.0, 50, pointsData, savedResultFunc);
        cache.setComputedAt(LocalDateTime.now());
        cache.setUpdatedAt(LocalDateTime.now());
        ComputationCacheEntity savedCache = cacheRepository.save(cache);

        Long idToDelete = savedCache.getId();
        assertThat(cacheRepository.findById(idToDelete)).isPresent();

        cacheRepository.deleteById(idToDelete);

        Optional<ComputationCacheEntity> found = cacheRepository.findById(idToDelete);
        assertThat(found).isEmpty();
    }
}