package dto.request;
public class CreateFunctionRequest {
    private String name;
    private String type;
    private String expression;
    private Double leftBound;
    private Double rightBound;
    private Integer pointsCount;
    private double[] xValues;
    private double[] yValues;
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
    public double[] getXValues() { return xValues; }
    public void setXValues(double[] xValues) { this.xValues = xValues; }
    public double[] getYValues() { return yValues; }
    public void setYValues(double[] yValues) { this.yValues = yValues; }
}