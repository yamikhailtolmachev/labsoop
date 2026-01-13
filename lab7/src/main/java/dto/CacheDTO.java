package dto;

import java.sql.Timestamp;
import java.util.UUID;

public class CacheDTO {
    private String cacheKey;
    private UUID userId;
    private String functionExpression;
    private Double leftBound;
    private Double rightBound;
    private Integer pointsCount;
    private UUID resultFunctionId;
    private Timestamp computedAt;
    private Integer accessCount;

    public String getCacheKey() { return cacheKey; }
    public void setCacheKey(String cacheKey) { this.cacheKey = cacheKey; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getFunctionExpression() { return functionExpression; }
    public void setFunctionExpression(String functionExpression) { this.functionExpression = functionExpression; }
    public Double getLeftBound() { return leftBound; }
    public void setLeftBound(Double leftBound) { this.leftBound = leftBound; }
    public Double getRightBound() { return rightBound; }
    public void setRightBound(Double rightBound) { this.rightBound = rightBound; }
    public Integer getPointsCount() { return pointsCount; }
    public void setPointsCount(Integer pointsCount) { this.pointsCount = pointsCount; }
    public UUID getResultFunctionId() { return resultFunctionId; }
    public void setResultFunctionId(UUID resultFunctionId) { this.resultFunctionId = resultFunctionId; }
    public Timestamp getComputedAt() { return computedAt; }
    public void setComputedAt(Timestamp computedAt) { this.computedAt = computedAt; }
    public Integer getAccessCount() { return accessCount; }
    public void setAccessCount(Integer accessCount) { this.accessCount = accessCount; }
}