package lab5.repository;

import lab5.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = {"role"})
    Optional<UserEntity> findByUsername(String username);

    @EntityGraph(attributePaths = {"role"})
    Optional<UserEntity> findByEmail(String email);

    @EntityGraph(attributePaths = {"role"})
    List<UserEntity> findByRole_Name(String roleName);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.role.name = :roleName ORDER BY u.username")
    Page<UserEntity> findByRoleNameOrderByUsername(@Param("roleName") String roleName, Pageable pageable);
}