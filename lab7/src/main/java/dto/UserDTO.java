package dto;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.sql.Timestamp;

public class UserDTO {
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private List<String> roles;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public UserDTO() {
        this.roles = new ArrayList<>();
    }

    public UserDTO(UUID id, String username, String email, String passwordHash, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = roles != null ? roles : new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(role);
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }
}