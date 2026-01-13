package lab5.dto;

public class ComputationCacheDTO {
    private Long id;
    private String cacheKey;
    private Long userId;
    private String functionExpression;
    private Double leftBound;
    private Double rightBound;
    private Integer pointsCount;
    private Long resultFunctionId;

    public ComputationCacheDTO() {}

    public ComputationCacheDTO(Long id, String cacheKey, Long userId, String functionExpression, Double leftBound, Double rightBound, Integer pointsCount, Long resultFunctionId) {
        this.id = id;
        this.cacheKey = cacheKey;
        this.userId = userId;
        this.functionExpression = functionExpression;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.pointsCount = pointsCount;
        this.resultFunctionId = resultFunctionId;
    }

    public ComputationCacheDTO(String cacheKey, Long userId, String functionExpression, Double leftBound, Double rightBound, Integer pointsCount, Long resultFunctionId) {
        this.cacheKey = cacheKey;
        this.userId = userId;
        this.functionExpression = functionExpression;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.pointsCount = pointsCount;
        this.resultFunctionId = resultFunctionId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCacheKey() { return cacheKey; }
    public void setCacheKey(String cacheKey) { this.cacheKey = cacheKey; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFunctionExpression() { return functionExpression; }
    public void setFunctionExpression(String functionExpression) { this.functionExpression = functionExpression; }

    public Double getLeftBound() { return leftBound; }
    public void setLeftBound(Double leftBound) { this.leftBound = leftBound; }

    public Double getRightBound() { return rightBound; }
    public void setRightBound(Double rightBound) { this.rightBound = rightBound; }

    public Integer getPointsCount() { return pointsCount; }
    public void setPointsCount(Integer pointsCount) { this.pointsCount = pointsCount; }

    public Long getResultFunctionId() { return resultFunctionId; }
    public void setResultFunctionId(Long resultFunctionId) { this.resultFunctionId = resultFunctionId; }
}