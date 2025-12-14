package lab5.dto;

import java.time.LocalDateTime;

public class OperationDTO {
    private Long id;
    private Long userId;
    private String operationType;
    private String parameters;
    private Long function1Id;
    private Long function2Id;
    private Long resultFunctionId;

    public OperationDTO(Long id, Long aLong, Long id1, Long aLong1, Long id2, String operationType, String parameters, LocalDateTime computedAt, LocalDateTime updatedAt) {}

    public OperationDTO(Long id, Long userId, String operationType, String parameters, Long function1Id, Long function2Id, Long resultFunctionId) {
        this.id = id;
        this.userId = userId;
        this.operationType = operationType;
        this.parameters = parameters;
        this.function1Id = function1Id;
        this.function2Id = function2Id;
        this.resultFunctionId = resultFunctionId;
    }

    public OperationDTO(Long userId, String operationType, String parameters, Long function1Id, Long function2Id, Long resultFunctionId) {
        this.userId = userId;
        this.operationType = operationType;
        this.parameters = parameters;
        this.function1Id = function1Id;
        this.function2Id = function2Id;
        this.resultFunctionId = resultFunctionId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }

    public Long getFunction1Id() { return function1Id; }
    public void setFunction1Id(Long function1Id) { this.function1Id = function1Id; }

    public Long getFunction2Id() { return function2Id; }
    public void setFunction2Id(Long function2Id) { this.function2Id = function2Id; }

    public Long getResultFunctionId() { return resultFunctionId; }
    public void setResultFunctionId(Long resultFunctionId) { this.resultFunctionId = resultFunctionId; }
}