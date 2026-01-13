package lab5.repository;

import lab5.entity.OperationEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, Long> {
    List<OperationEntity> findByUser_Id(Long userId);

    List<OperationEntity> findByUser_Id(Long userId, Sort sort);

    List<OperationEntity> findByOperationType(String operationType);

    List<OperationEntity> findByFunction1_IdOrFunction2_Id(Long function1_Id, Long function2_Id);

    List<OperationEntity> findByResultFunction_Id(Long resultFunction_Id);
}