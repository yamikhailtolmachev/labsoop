package dto;

import java.sql.Timestamp;
import java.util.UUID;

public class OperationDTO {
    private UUID id;
    private UUID userId;
    private UUID function1Id;
    private UUID function2Id;
    private UUID resultFunctionId;
    private String operationType;
    private String parameters;
    private Timestamp computedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getFunction1Id() { return function1Id; }
    public void setFunction1Id(UUID function1Id) { this.function1Id = function1Id; }
    public UUID getFunction2Id() { return function2Id; }
    public void setFunction2Id(UUID function2Id) { this.function2Id = function2Id; }
    public UUID getResultFunctionId() { return resultFunctionId; }
    public void setResultFunctionId(UUID resultFunctionId) { this.resultFunctionId = resultFunctionId; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }
    public Timestamp getComputedAt() { return computedAt; }
    public void setComputedAt(Timestamp computedAt) { this.computedAt = computedAt; }
}