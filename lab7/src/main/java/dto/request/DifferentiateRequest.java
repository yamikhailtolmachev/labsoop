package dto.request;
import java.util.UUID;

public class DifferentiateRequest {
    private UUID functionId;
    private String operatorType;
    private Double step;

    public UUID getFunctionId() { return functionId; }
    public void setFunctionId(UUID functionId) { this.functionId = functionId; }
    public String getOperatorType() { return operatorType; }
    public void setOperatorType(String operatorType) { this.operatorType = operatorType; }
    public Double getStep() { return step; }
    public void setStep(Double step) { this.step = step; }
}