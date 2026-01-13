package lab5.dto;

public class FunctionDTO {
    private Long id;
    private Long userId;
    private String name;
    private String type;
    private String expression;
    private Double leftBound;
    private Double rightBound;
    private Integer pointsCount;
    private String pointsData;

    public FunctionDTO() {}

    public FunctionDTO(Long id, Long userId, String name, String type, String expression, Double leftBound, Double rightBound, Integer pointsCount, String pointsData) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.expression = expression;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.pointsCount = pointsCount;
        this.pointsData = pointsData;
    }

    public FunctionDTO(Long userId, String name, String type, String expression, Double leftBound, Double rightBound, Integer pointsCount, String pointsData) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.expression = expression;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.pointsCount = pointsCount;
        this.pointsData = pointsData;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }

    public Double getLeftBound() { return leftBound; }
    public void setLeftBound(Double leftBound) { this.leftBound = leftBound; }

    public Double getRightBound() { return rightBound; }
    public void setRightBound(Double rightBound) { this.rightBound = rightBound; }

    public Integer getPointsCount() { return pointsCount; }
    public void setPointsCount(Integer pointsCount) { this.pointsCount = pointsCount; }

    public String getPointsData() { return pointsData; }
    public void setPointsData(String pointsData) { this.pointsData = pointsData; }
}