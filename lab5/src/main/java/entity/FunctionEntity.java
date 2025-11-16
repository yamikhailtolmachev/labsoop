package entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;

@Entity
@Table(name = "functions")
public class FunctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "expression", columnDefinition = "TEXT")
    private String expression;

    @Column(name = "left_bound")
    private Double leftBound;

    @Column(name = "right_bound")
    private Double rightBound;

    @Column(name = "points_count")
    private Integer pointsCount;

    @Column(name = "points_data", columnDefinition = "jsonb", nullable = false)
    private JsonNode pointsData;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    protected FunctionEntity() {}

    public FunctionEntity(UserEntity user, String name, String type, String expression, Double leftBound, Double rightBound, Integer pointsCount, JsonNode pointsData) {
        this.user = user;
        this.name = name;
        this.type = type;
        this.expression = expression;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.pointsCount = pointsCount;
        this.pointsData = pointsData;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Double getLeftBound() {
        return leftBound;
    }

    public void setLeftBound(Double leftBound) {
        this.leftBound = leftBound;
    }

    public Double getRightBound() {
        return rightBound;
    }

    public void setRightBound(Double rightBound) {
        this.rightBound = rightBound;
    }

    public Integer getPointsCount() {
        return pointsCount;
    }

    public void setPointsCount(Integer pointsCount) {
        this.pointsCount = pointsCount;
    }

    public JsonNode getPointsData() {
        return pointsData;
    }

    public void setPointsData(JsonNode pointsData) {
        this.pointsData = pointsData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}