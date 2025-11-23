package lab5.repository;

import lab5.entity.FunctionEntity;
import lab5.entity.OperationEntity;
import lab5.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
class OperationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OperationRepository operationRepository;

    @Test
    void shouldSaveAndFindOperationById() {
        UserEntity user = new UserEntity("opUser", "opu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user);

        FunctionEntity func1 = new FunctionEntity(user, "f1", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(func1);

        FunctionEntity func2 = new FunctionEntity(user, "f2", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(func2);

        FunctionEntity resFunc = new FunctionEntity(user, "res", "OPERATION_RESULT", "x+x^2", 0.0, 1.0, 10, "{\"points\": []}");
        resFunc.setCreatedAt(LocalDateTime.now());
        resFunc.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(resFunc);

        OperationEntity operation = new OperationEntity(user, func1, func2, resFunc, "ADD", "{\"param\": \"value\"}");

        OperationEntity saved = operationRepository.save(operation);
        Long savedId = saved.getId();

        assertThat(savedId).isNotNull();
        Optional<OperationEntity> found = operationRepository.findById(savedId);
        assertThat(found).isPresent();
        assertThat(found.get().getOperationType()).isEqualTo("ADD");
        assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
        assertThat(found.get().getFunction1().getId()).isEqualTo(func1.getId());
        assertThat(found.get().getFunction2().getId()).isEqualTo(func2.getId());
        assertThat(found.get().getResultFunction().getId()).isEqualTo(resFunc.getId());
    }

    @Test
    void shouldFindOperationsByUserId() {
        UserEntity user = new UserEntity("opUser2", "opu2@example.com", "hash2");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user);

        FunctionEntity func1 = new FunctionEntity(user, "f1", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(func1);

        FunctionEntity func2 = new FunctionEntity(user, "f2", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(func2);

        FunctionEntity resFunc1 = new FunctionEntity(user, "res1", "OPERATION_RESULT", "x+x^2", 0.0, 1.0, 10, "{\"points\": []}");
        resFunc1.setCreatedAt(LocalDateTime.now());
        resFunc1.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(resFunc1);

        FunctionEntity resFunc2 = new FunctionEntity(user, "res2", "OPERATION_RESULT", "x-x^2", 0.0, 1.0, 10, "{\"points\": []}");
        resFunc2.setCreatedAt(LocalDateTime.now());
        resFunc2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(resFunc2);

        OperationEntity op1 = new OperationEntity(user, func1, func2, resFunc1, "ADD", "{\"param\": \"value1\"}");
        op1.setComputedAt(LocalDateTime.now());
        op1.setUpdatedAt(LocalDateTime.now());
        OperationEntity op2 = new OperationEntity(user, func1, func2, resFunc2, "SUBTRACT", "{\"param\": \"value2\"}");
        op2.setComputedAt(LocalDateTime.now());
        op2.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(op1);
        entityManager.persist(op2);
        entityManager.flush();

        List<OperationEntity> found = operationRepository.findByUserId(user.getId());

        assertThat(found).hasSize(2);
        assertThat(found).extracting(OperationEntity::getOperationType).containsExactlyInAnyOrder("ADD", "SUBTRACT");
    }

    @Test
    void shouldDeleteOperation() {
        UserEntity user = new UserEntity("delOpUser", "dopu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user);

        FunctionEntity func1 = new FunctionEntity(user, "f1", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(func1);

        FunctionEntity func2 = new FunctionEntity(user, "f2", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(func2);

        FunctionEntity resFunc = new FunctionEntity(user, "res", "OPERATION_RESULT", "x*x^2", 0.0, 1.0, 10, "{\"points\": []}");
        resFunc.setCreatedAt(LocalDateTime.now());
        resFunc.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(resFunc);

        OperationEntity operation = new OperationEntity(user, func1, func2, resFunc, "MULTIPLY", "{\"param\": \"value3\"}");
        entityManager.persistAndFlush(operation);
        Long idToDelete = operation.getId();
        assertThat(operationRepository.findById(idToDelete)).isPresent();

        operationRepository.deleteById(idToDelete);
        entityManager.flush();

        Optional<OperationEntity> found = operationRepository.findById(idToDelete);
        assertThat(found).isEmpty();
    }
}