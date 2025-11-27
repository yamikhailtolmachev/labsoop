package lab5.repository;

import lab5.entity.FunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FunctionRepository extends JpaRepository<FunctionEntity, Long> {
    List<FunctionEntity> findByUserId(Long userId);

    List<FunctionEntity> findByUserId(Long userId, Sort sort);

    List<FunctionEntity> findByType(String type);

    List<FunctionEntity> findByUserIdAndType(Long userId, String type);

    FunctionEntity findByNameAndUserId(String name, Long userId);
}