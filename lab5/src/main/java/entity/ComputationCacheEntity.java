package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "computation_cache")
public class ComputationCacheEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "cache_key", unique = true, nullable = false, length = 512)
    private String cacheKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "function_expression", columnDefinition = "TEXT", nullable = false)
    private String functionExpression;

    @Column(name = "left_bound", nullable = false)
    private Double leftBound;

    @Column(name = "right_bound", nullable = false)
    private Double rightBound;

    @Column(name = "points_count", nullable = false)
    private Integer pointsCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_function_id", nullable = false)
    private FunctionEntity resultFunction;

    @Column(name = "computed_at", nullable = false, updatable = false)
    private LocalDateTime computedAt;

    @Column(name = "access_count", nullable = false)
    private Integer accessCount = 1;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected ComputationCacheEntity() {}

    public ComputationCacheEntity(String cacheKey, UserEntity user, String functionExpression, Double leftBound, Double rightBound, Integer pointsCount, FunctionEntity resultFunction) {
        this.cacheKey = cacheKey;
        this.user = user;
        this.functionExpression = functionExpression;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.pointsCount = pointsCount;
        this.resultFunction = resultFunction;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getFunctionExpression() {
        return functionExpression;
    }

    public void setFunctionExpression(String functionExpression) {
        this.functionExpression = functionExpression;
    }

    public Double getLeftBound() {
        return leftBound;
    }

    public void setLeftBound(Double leftBound) {
        this.leftBound = leftBound;
    }

    public Double getRightBound() {
        return rightBound;
    }

    public void setRightBound(Double rightBound) {
        this.rightBound = rightBound;
    }

    public Integer getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(Integer pointsCount) {
        this.pointsCount = pointsCount;
    }

    public FunctionEntity getResultFunction() {
        return resultFunction;
    }

    public void setResultFunction(FunctionEntity resultFunction) {
        this.resultFunction = resultFunction;
    }

    public LocalDateTime getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(LocalDateTime computedAt) {
        this.computedAt = computedAt;
    }

    public Integer getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(Integer accessCount) {
        this.accessCount = accessCount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}