package dto.request;
import java.util.UUID;
public class ComputeRequest {
    private UUID functionId;
    private double x;
    public UUID getFunctionId() { return functionId; }
    public void setFunctionId(UUID functionId) { this.functionId = functionId; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
}