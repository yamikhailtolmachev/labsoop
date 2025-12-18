package entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class OperationEntity {
    private UUID id;
    private UUID userId;
    private UUID function1Id;
    private UUID function2Id;
    private UUID resultFunctionId;
    private String operationType;
    private String parameters;
    private LocalDateTime computedAt;

    public OperationEntity() {}

    public OperationEntity(UUID id, UUID userId, UUID function1Id, UUID function2Id,
                           UUID resultFunctionId, String operationType,
                           String parameters, LocalDateTime computedAt) {
        this.id = id;
        this.userId = userId;
        this.function1Id = function1Id;
        this.function2Id = function2Id;
        this.resultFunctionId = resultFunctionId;
        this.operationType = operationType;
        this.parameters = parameters;
        this.computedAt = computedAt;
    }

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

    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }

    @Override
    public String toString() {
        return "OperationEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", function1Id=" + function1Id +
                ", function2Id=" + function2Id +
                ", resultFunctionId=" + resultFunctionId +
                ", operationType='" + operationType + '\'' +
                ", parameters='" + parameters + '\'' +
                ", computedAt=" + computedAt +
                '}';
    }
}