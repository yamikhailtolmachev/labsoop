package lab5.repository;

import lab5.entity.ComputationCacheEntity;
import lab5.entity.FunctionEntity;
import lab5.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ComputationCacheRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ComputationCacheRepository cacheRepository;

    @Test
    void shouldSaveAndFindCacheById() {
        UserEntity user = new UserEntity("cacheUser", "cu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user);

        FunctionEntity resultFunc = new FunctionEntity(user, "resultFunc", "OPERATION_RESULT", "cached_result", 0.0, 1.0, 100, "{\"points\": []}");
        resultFunc.setCreatedAt(LocalDateTime.now());
        resultFunc.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(resultFunc);

        String cacheKey = "unique_cache_key_123";
        ComputationCacheEntity cache = new ComputationCacheEntity(cacheKey, user, "x^2", 0.0, 1.0, 100, resultFunc);
        cache.setComputedAt(LocalDateTime.now());
        cache.setUpdatedAt(LocalDateTime.now());

        ComputationCacheEntity saved = cacheRepository.save(cache);
        Long savedId = saved.getId();

        assertThat(savedId).isNotNull();
        Optional<ComputationCacheEntity> found = cacheRepository.findById(savedId);
        assertThat(found).isPresent();
        assertThat(found.get().getCacheKey()).isEqualTo(cacheKey);
        assertThat(found.get().getFunctionExpression()).isEqualTo("x^2");
        assertThat(found.get().getResultFunction().getId()).isEqualTo(resultFunc.getId());
    }

    @Test
    void shouldFindCacheByCacheKey() {
        UserEntity user = new UserEntity("cacheUser2", "cu2@example.com", "hash2");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user);

        FunctionEntity resultFunc = new FunctionEntity(user, "resultFunc2", "OPERATION_RESULT", "cached_result2", 0.0, 2.0, 200, "{\"points\": []}");
        resultFunc.setCreatedAt(LocalDateTime.now());
        resultFunc.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(resultFunc);

        String cacheKey = "unique_cache_key_456";
        ComputationCacheEntity cache = new ComputationCacheEntity(cacheKey, user, "sin(x)", 0.0, 2.0, 200, resultFunc);
        cache.setComputedAt(LocalDateTime.now());
        cache.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(cache);

        Optional<ComputationCacheEntity> found = cacheRepository.findByCacheKey(cacheKey);

        assertThat(found).isPresent();
        assertThat(found.get().getFunctionExpression()).isEqualTo("sin(x)");
    }

    @Test
    void shouldDeleteCache() {
        UserEntity user = new UserEntity("delCacheUser", "dcu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user);

        FunctionEntity resultFunc = new FunctionEntity(user, "resultFunc3", "OPERATION_RESULT", "cached_result3", -1.0, 1.0, 50, "{\"points\": []}");
        resultFunc.setCreatedAt(LocalDateTime.now());
        resultFunc.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(resultFunc);

        String cacheKey = "unique_cache_key_789";
        ComputationCacheEntity cache = new ComputationCacheEntity(cacheKey, user, "cos(x)", -1.0, 1.0, 50, resultFunc);
        cache.setComputedAt(LocalDateTime.now());
        cache.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(cache);
        Long idToDelete = cache.getId();
        assertThat(cacheRepository.findById(idToDelete)).isPresent();

        cacheRepository.deleteById(idToDelete);
        entityManager.flush();

        Optional<ComputationCacheEntity> found = cacheRepository.findById(idToDelete);
        assertThat(found).isEmpty();
    }
}