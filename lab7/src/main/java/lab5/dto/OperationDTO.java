package lab5.dto;

import java.time.LocalDateTime;

public class OperationDTO {
    private Long id;
    private String operationType;
    private String parameters;
    private Long userId;
    private Long function1Id;
    private Long function2Id;
    private Long resultFunctionId;
    private LocalDateTime computedAt;
    private LocalDateTime updatedAt;

    public OperationDTO() {
    }

    public OperationDTO(Long id, Long userId, Long function1Id, Long function2Id,
                        Long resultFunctionId, String operationType, String parameters,
                        LocalDateTime computedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.function1Id = function1Id;
        this.function2Id = function2Id;
        this.resultFunctionId = resultFunctionId;
        this.operationType = operationType;
        this.parameters = parameters;
        this.computedAt = computedAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }
    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getFunction1Id() { return function1Id; }
    public void setFunction1Id(Long function1Id) { this.function1Id = function1Id; }
    public Long getFunction2Id() { return function2Id; }
    public void setFunction2Id(Long function2Id) { this.function2Id = function2Id; }
    public Long getResultFunctionId() { return resultFunctionId; }
    public void setResultFunctionId(Long resultFunctionId) { this.resultFunctionId = resultFunctionId; }
    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}