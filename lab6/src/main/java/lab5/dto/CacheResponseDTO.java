package lab5.dto;

import java.time.LocalDateTime;

public class CacheResponseDTO {
    private Long id;
    private String cacheKey;
    private String functionExpression;
    private Double leftBound;
    private Double rightBound;
    private Integer pointsCount;
    private String pointsData;
    private Long userId;
    private Long resultFunctionId;
    private Integer accessCount;
    private LocalDateTime computedAt;
    private LocalDateTime updatedAt;

    public CacheResponseDTO() {
    }

    public CacheResponseDTO(Long id, String cacheKey, String functionExpression,
                            Double leftBound, Double rightBound, Integer pointsCount,
                            String pointsData, Long userId, Long resultFunctionId,
                            Integer accessCount, LocalDateTime computedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.cacheKey = cacheKey;
        this.functionExpression = functionExpression;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.pointsCount = pointsCount;
        this.pointsData = pointsData;
        this.userId = userId;
        this.resultFunctionId = resultFunctionId;
        this.accessCount = accessCount;
        this.computedAt = computedAt;
        this.updatedAt = updatedAt;
    }

    public static CacheResponseDTO fromEntity(lab5.entity.ComputationCacheEntity entity) {
        if (entity == null) {
            return null;
        }
        return new CacheResponseDTO(
                entity.getId(),
                entity.getCacheKey(),
                entity.getFunctionExpression(),
                entity.getLeftBound(),
                entity.getRightBound(),
                entity.getPointsCount(),
                entity.getPointsData(),
                entity.getUser() != null ? entity.getUser().getId() : null,
                entity.getResultFunction() != null ? entity.getResultFunction().getId() : null,
                entity.getAccessCount(),
                entity.getComputedAt(),
                entity.getUpdatedAt()
        );
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCacheKey() { return cacheKey; }
    public void setCacheKey(String cacheKey) { this.cacheKey = cacheKey; }
    public String getFunctionExpression() { return functionExpression; }
    public void setFunctionExpression(String functionExpression) { this.functionExpression = functionExpression; }
    public Double getLeftBound() { return leftBound; }
    public void setLeftBound(Double leftBound) { this.leftBound = leftBound; }
    public Double getRightBound() { return rightBound; }
    public void setRightBound(Double rightBound) { this.rightBound = rightBound; }
    public Integer getPointsCount() { return pointsCount; }
    public void setPointsCount(Integer pointsCount) { this.pointsCount = pointsCount; }
    public String getPointsData() { return pointsData; }
    public void setPointsData(String pointsData) { this.pointsData = pointsData; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getResultFunctionId() { return resultFunctionId; }
    public void setResultFunctionId(Long resultFunctionId) { this.resultFunctionId = resultFunctionId; }
    public Integer getAccessCount() { return accessCount; }
    public void setAccessCount(Integer accessCount) { this.accessCount = accessCount; }
    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}