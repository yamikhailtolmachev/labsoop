package lab5.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "functions")
public class FunctionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "type", nullable = false, length = 20)
    private String type;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "expression", columnDefinition = "TEXT")
    private String expression;

    @Column(name = "left_bound")
    private Double leftBound;

    @Column(name = "right_bound")
    private Double rightBound;

    @Column(name = "points_count")
    private Integer pointsCount;

    @Column(name = "points_data", columnDefinition = "TEXT NOT NULL")
    private String pointsData = "{\"x\":[],\"y\":[]}";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public FunctionEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UserEntity getUser() { return user; }
    public void setUser(UserEntity user) { this.user = user; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getExpression() { return expression; }
    public void setExpression(String expression) { this.expression = expression; }

    public Double getLeftBound() { return leftBound; }
    public void setLeftBound(Double leftBound) { this.leftBound = leftBound; }

    public Double getRightBound() { return rightBound; }
    public void setRightBound(Double rightBound) { this.rightBound = rightBound; }

    public Integer getPointsCount() { return pointsCount; }
    public void setPointsCount(Integer pointsCount) { this.pointsCount = pointsCount; }

    public String getPointsData() {
        if (this.pointsData == null || this.pointsData.trim().isEmpty()) {
            return "{\"x\":[],\"y\":[]}";
        }
        return this.pointsData;
    }

    public void setPointsData(String pointsData) {
        if (pointsData == null || pointsData.trim().isEmpty()) {
            this.pointsData = "{\"x\":[],\"y\":[]}";
        } else {
            this.pointsData = pointsData;
        }
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    @PreUpdate
    public void ensurePointsData() {
        if (this.pointsData == null || this.pointsData.trim().isEmpty()) {
            this.pointsData = "{\"x\":[],\"y\":[]}";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionEntity that = (FunctionEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}