package lab5.repository;

import lab5.entity.FunctionEntity;
import lab5.entity.OperationEntity;
import lab5.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class OperationRepositoryTest {
    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Test
    void shouldSaveAndFindOperationById() {
        UserEntity user = new UserEntity("opUser", "opu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);

        FunctionEntity func1 = new FunctionEntity(savedUser, "f1", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc1 = functionRepository.save(func1);

        FunctionEntity func2 = new FunctionEntity(savedUser, "f2", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc2 = functionRepository.save(func2);

        FunctionEntity resFunc = new FunctionEntity(savedUser, "res", "OPERATION_RESULT", "x+x^2", 0.0, 1.0, 10, "{\"points\": []}");
        resFunc.setCreatedAt(LocalDateTime.now());
        resFunc.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResFunc = functionRepository.save(resFunc);

        OperationEntity operation = new OperationEntity(savedUser, savedFunc1, savedFunc2, savedResFunc, "ADD", "{\"param\": \"value\"}");
        operation.setComputedAt(LocalDateTime.now());
        operation.setUpdatedAt(LocalDateTime.now());

        OperationEntity saved = operationRepository.save(operation);
        Long savedId = saved.getId();

        assertThat(savedId).isNotNull();
        Optional<OperationEntity> found = operationRepository.findById(savedId);
        assertThat(found).isPresent();
        assertThat(found.get().getOperationType()).isEqualTo("ADD");
        assertThat(found.get().getUser().getId()).isEqualTo(savedUser.getId());
        assertThat(found.get().getFunction1().getId()).isEqualTo(savedFunc1.getId());
        assertThat(found.get().getFunction2().getId()).isEqualTo(savedFunc2.getId());
        assertThat(found.get().getResultFunction().getId()).isEqualTo(savedResFunc.getId());
    }

    @Test
    void shouldFindOperationsByUserId() {
        UserEntity user = new UserEntity("opUser2", "opu2@example.com", "hash2");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);

        FunctionEntity func1 = new FunctionEntity(savedUser, "f1", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc1 = functionRepository.save(func1);

        FunctionEntity func2 = new FunctionEntity(savedUser, "f2", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc2 = functionRepository.save(func2);

        FunctionEntity resFunc1 = new FunctionEntity(savedUser, "res1", "OPERATION_RESULT", "x+x^2", 0.0, 1.0, 10, "{\"points\": []}");
        resFunc1.setCreatedAt(LocalDateTime.now());
        resFunc1.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResFunc1 = functionRepository.save(resFunc1);

        FunctionEntity resFunc2 = new FunctionEntity(savedUser, "res2", "OPERATION_RESULT", "x-x^2", 0.0, 1.0, 10, "{\"points\": []}");
        resFunc2.setCreatedAt(LocalDateTime.now());
        resFunc2.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResFunc2 = functionRepository.save(resFunc2);

        OperationEntity op1 = new OperationEntity(savedUser, savedFunc1, savedFunc2, savedResFunc1, "ADD", "{\"param\": \"value1\"}");
        op1.setComputedAt(LocalDateTime.now());
        op1.setUpdatedAt(LocalDateTime.now());
        operationRepository.save(op1);

        OperationEntity op2 = new OperationEntity(savedUser, savedFunc1, savedFunc2, savedResFunc2, "SUBTRACT", "{\"param\": \"value2\"}");
        op2.setComputedAt(LocalDateTime.now());
        op2.setUpdatedAt(LocalDateTime.now());
        operationRepository.save(op2);

        List<OperationEntity> found = operationRepository.findByUserId(savedUser.getId());

        assertThat(found).hasSize(2);
        assertThat(found).extracting(OperationEntity::getOperationType)
                .containsExactlyInAnyOrder("ADD", "SUBTRACT");
    }

    @Test
    void shouldDeleteOperation() {
        UserEntity user = new UserEntity("delOpUser", "dopu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);

        FunctionEntity func1 = new FunctionEntity(savedUser, "f1", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc1 = functionRepository.save(func1);

        FunctionEntity func2 = new FunctionEntity(savedUser, "f2", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc2 = functionRepository.save(func2);

        FunctionEntity resFunc = new FunctionEntity(savedUser, "res", "OPERATION_RESULT", "x*x^2", 0.0, 1.0, 10, "{\"points\": []}");
        resFunc.setCreatedAt(LocalDateTime.now());
        resFunc.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResFunc = functionRepository.save(resFunc);

        OperationEntity operation = new OperationEntity(savedUser, savedFunc1, savedFunc2, savedResFunc, "MULTIPLY", "{\"param\": \"value3\"}");
        operation.setComputedAt(LocalDateTime.now());
        operation.setUpdatedAt(LocalDateTime.now());
        OperationEntity savedOperation = operationRepository.save(operation);

        Long idToDelete = savedOperation.getId();
        assertThat(operationRepository.findById(idToDelete)).isPresent();

        operationRepository.deleteById(idToDelete);

        Optional<OperationEntity> found = operationRepository.findById(idToDelete);
        assertThat(found).isEmpty();
    }
}