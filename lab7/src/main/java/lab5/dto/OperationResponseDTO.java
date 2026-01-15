package lab5.dto;

import java.time.LocalDateTime;
import lab5.entity.OperationEntity;
import lab5.entity.FunctionEntity;

public class OperationResponseDTO {
    private Long id;
    private Long userId;
    private Long function1Id;
    private Long function2Id;
    private FunctionDTO resultFunction; // ← Объект, а не ID!
    private String operationType;
    private String parameters;
    private LocalDateTime computedAt;
    private LocalDateTime updatedAt;

    public OperationResponseDTO() {}

    public OperationResponseDTO(Long id, Long userId, Long function1Id, Long function2Id,
                                FunctionDTO resultFunction, String operationType,
                                String parameters, LocalDateTime computedAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.function1Id = function1Id;
        this.function2Id = function2Id;
        this.resultFunction = resultFunction;
        this.operationType = operationType;
        this.parameters = parameters;
        this.computedAt = computedAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFunction1Id() { return function1Id; }
    public void setFunction1Id(Long function1Id) { this.function1Id = function1Id; }

    public Long getFunction2Id() { return function2Id; }
    public void setFunction2Id(Long function2Id) { this.function2Id = function2Id; }

    public FunctionDTO getResultFunction() { return resultFunction; }
    public void setResultFunction(FunctionDTO resultFunction) { this.resultFunction = resultFunction; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }

    public LocalDateTime getComputedAt() { return computedAt; }
    public void setComputedAt(LocalDateTime computedAt) { this.computedAt = computedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static OperationResponseDTO fromEntity(OperationEntity entity) {
        if (entity == null) {
            return null;
        }

        OperationResponseDTO dto = new OperationResponseDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setFunction1Id(entity.getFunction1().getId());
        dto.setFunction2Id(entity.getFunction2() != null ? entity.getFunction2().getId() : null);

        FunctionEntity resultFunc = entity.getResultFunction();
        FunctionDTO resultDto = new FunctionDTO(
                resultFunc.getId(),
                resultFunc.getUser().getId(),
                resultFunc.getName(),
                resultFunc.getType(),
                resultFunc.getExpression(),
                resultFunc.getLeftBound(),
                resultFunc.getRightBound(),
                resultFunc.getPointsCount(),
                resultFunc.getPointsData()
        );
        dto.setResultFunction(resultDto);

        dto.setOperationType(entity.getOperationType());
        dto.setParameters(entity.getParameters());
        dto.setComputedAt(entity.getComputedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}