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

        FunctionEntity func1 = new FunctionEntity();
        func1.setUser(savedUser);
        func1.setName("f1");
        func1.setType("BASIC");
        func1.setExpression("x");
        func1.setLeftBound(0.0);
        func1.setRightBound(1.0);
        func1.setPointsCount(10);
        func1.setPointsData("{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc1 = functionRepository.save(func1);

        FunctionEntity func2 = new FunctionEntity();
        func2.setUser(savedUser);
        func2.setName("f2");
        func2.setType("BASIC");
        func2.setExpression("x^2");
        func2.setLeftBound(0.0);
        func2.setRightBound(1.0);
        func2.setPointsCount(10);
        func2.setPointsData("{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc2 = functionRepository.save(func2);

        FunctionEntity resFunc = new FunctionEntity();
        resFunc.setUser(savedUser);
        resFunc.setName("res");
        resFunc.setType("OPERATION_RESULT");
        resFunc.setExpression("x+x^2");
        resFunc.setLeftBound(0.0);
        resFunc.setRightBound(1.0);
        resFunc.setPointsCount(10);
        resFunc.setPointsData("{\"points\": []}");
        resFunc.setCreatedAt(LocalDateTime.now());
        resFunc.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResFunc = functionRepository.save(resFunc);

        OperationEntity operation = new OperationEntity();
        operation.setUser(savedUser);
        operation.setFunction1(savedFunc1);
        operation.setFunction2(savedFunc2);
        operation.setResultFunction(savedResFunc);
        operation.setOperationType("ADD");
        operation.setParameters("{\"param\": \"value\"}");
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

        FunctionEntity func1 = new FunctionEntity();
        func1.setUser(savedUser);
        func1.setName("f1");
        func1.setType("BASIC");
        func1.setExpression("x");
        func1.setLeftBound(0.0);
        func1.setRightBound(1.0);
        func1.setPointsCount(10);
        func1.setPointsData("{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc1 = functionRepository.save(func1);

        FunctionEntity func2 = new FunctionEntity();
        func2.setUser(savedUser);
        func2.setName("f2");
        func2.setType("BASIC");
        func2.setExpression("x^2");
        func2.setLeftBound(0.0);
        func2.setRightBound(1.0);
        func2.setPointsCount(10);
        func2.setPointsData("{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc2 = functionRepository.save(func2);

        FunctionEntity resFunc1 = new FunctionEntity();
        resFunc1.setUser(savedUser);
        resFunc1.setName("res1");
        resFunc1.setType("OPERATION_RESULT");
        resFunc1.setExpression("x+x^2");
        resFunc1.setLeftBound(0.0);
        resFunc1.setRightBound(1.0);
        resFunc1.setPointsCount(10);
        resFunc1.setPointsData("{\"points\": []}");
        resFunc1.setCreatedAt(LocalDateTime.now());
        resFunc1.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResFunc1 = functionRepository.save(resFunc1);

        FunctionEntity resFunc2 = new FunctionEntity();
        resFunc2.setUser(savedUser);
        resFunc2.setName("res2");
        resFunc2.setType("OPERATION_RESULT");
        resFunc2.setExpression("x-x^2");
        resFunc2.setLeftBound(0.0);
        resFunc2.setRightBound(1.0);
        resFunc2.setPointsCount(10);
        resFunc2.setPointsData("{\"points\": []}");
        resFunc2.setCreatedAt(LocalDateTime.now());
        resFunc2.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResFunc2 = functionRepository.save(resFunc2);

        OperationEntity op1 = new OperationEntity();
        op1.setUser(savedUser);
        op1.setFunction1(savedFunc1);
        op1.setFunction2(savedFunc2);
        op1.setResultFunction(savedResFunc1);
        op1.setOperationType("ADD");
        op1.setParameters("{\"param\": \"value1\"}");
        op1.setComputedAt(LocalDateTime.now());
        op1.setUpdatedAt(LocalDateTime.now());
        operationRepository.save(op1);

        OperationEntity op2 = new OperationEntity();
        op2.setUser(savedUser);
        op2.setFunction1(savedFunc1);
        op2.setFunction2(savedFunc2);
        op2.setResultFunction(savedResFunc2);
        op2.setOperationType("SUBTRACT");
        op2.setParameters("{\"param\": \"value2\"}");
        op2.setComputedAt(LocalDateTime.now());
        op2.setUpdatedAt(LocalDateTime.now());
        operationRepository.save(op2);

        List<OperationEntity> found = operationRepository.findByUser_Id(savedUser.getId());

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

        FunctionEntity func1 = new FunctionEntity();
        func1.setUser(savedUser);
        func1.setName("f1");
        func1.setType("BASIC");
        func1.setExpression("x");
        func1.setLeftBound(0.0);
        func1.setRightBound(1.0);
        func1.setPointsCount(10);
        func1.setPointsData("{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc1 = functionRepository.save(func1);

        FunctionEntity func2 = new FunctionEntity();
        func2.setUser(savedUser);
        func2.setName("f2");
        func2.setType("BASIC");
        func2.setExpression("x^2");
        func2.setLeftBound(0.0);
        func2.setRightBound(1.0);
        func2.setPointsCount(10);
        func2.setPointsData("{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunc2 = functionRepository.save(func2);

        FunctionEntity resFunc = new FunctionEntity();
        resFunc.setUser(savedUser);
        resFunc.setName("res");
        resFunc.setType("OPERATION_RESULT");
        resFunc.setExpression("x*x^2");
        resFunc.setLeftBound(0.0);
        resFunc.setRightBound(1.0);
        resFunc.setPointsCount(10);
        resFunc.setPointsData("{\"points\": []}");
        resFunc.setCreatedAt(LocalDateTime.now());
        resFunc.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedResFunc = functionRepository.save(resFunc);

        OperationEntity operation = new OperationEntity();
        operation.setUser(savedUser);
        operation.setFunction1(savedFunc1);
        operation.setFunction2(savedFunc2);
        operation.setResultFunction(savedResFunc);
        operation.setOperationType("MULTIPLY");
        operation.setParameters("{\"param\": \"value3\"}");
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