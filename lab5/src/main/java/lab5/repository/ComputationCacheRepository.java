package lab5.repository;

import lab5.entity.ComputationCacheEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComputationCacheRepository extends JpaRepository<ComputationCacheEntity, Long> {
    Optional<ComputationCacheEntity> findByCacheKey(String cacheKey);

    Optional<ComputationCacheEntity> findByUserIdAndFunctionExpressionAndLeftBoundAndRightBoundAndPointsCount(
            Long userId, String functionExpression, Double leftBound, Double rightBound, Integer pointsCount);
}