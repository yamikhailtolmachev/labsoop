package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operations")
public class OperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function1_id", nullable = false)
    private FunctionEntity function1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function2_id")
    private FunctionEntity function2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "result_function_id", nullable = false)
    private FunctionEntity resultFunction;

    @Column(name = "operation_type", nullable = false, length = 20)
    private String operationType;

    @Column(name = "parameters", columnDefinition = "jsonb")
    private String parameters;

    @Column(name = "computed_at", nullable = false, updatable = false)
    private LocalDateTime computedAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected OperationEntity() {}

    public OperationEntity(UserEntity user, FunctionEntity function1, FunctionEntity function2, FunctionEntity resultFunction, String operationType, String parameters) {
        this.user = user;
        this.function1 = function1;
        this.function2 = function2;
        this.resultFunction = resultFunction;
        this.operationType = operationType;
        this.parameters = parameters; // Принимаем строку
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public FunctionEntity getFunction1() {
        return function1;
    }

    public void setFunction1(FunctionEntity function1) {
        this.function1 = function1;
    }

    public FunctionEntity getFunction2() {
        return function2;
    }

    public void setFunction2(FunctionEntity function2) {
        this.function2 = function2;
    }

    public FunctionEntity getResultFunction() {
        return resultFunction;
    }

    public void setResultFunction(FunctionEntity resultFunction) {
        this.resultFunction = resultFunction;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public LocalDateTime getComputedAt() {
        return computedAt;
    }

    public void setComputedAt(LocalDateTime computedAt) {
        this.computedAt = computedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}