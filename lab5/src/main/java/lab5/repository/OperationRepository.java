package lab5.repository;

import lab5.entity.OperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, Long> {
    List<OperationEntity> findByUserId(Long userId);

    List<OperationEntity> findByOperationType(String operationType);

    List<OperationEntity> findByFunction1IdOrFunction2Id(Long function1Id, Long function2Id);

    List<OperationEntity> findByResultFunctionId(Long resultFunctionId);
}