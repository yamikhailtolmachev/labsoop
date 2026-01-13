package lab5.service;

import lab5.dto.FunctionDTO;
import lab5.dto.OperationDTO;
import lab5.entity.FunctionEntity;
import lab5.entity.OperationEntity;
import lab5.entity.UserEntity;
import lab5.repository.FunctionRepository;
import lab5.repository.OperationRepository;
import lab5.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class SearchServiceTest {

    @Autowired
    private SearchService searchService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private FunctionRepository functionRepository;

    @MockBean
    private OperationRepository operationRepository;

    @Test
    void shouldFindUserById() {
        Long userId = 1L;
        UserEntity realUser = new UserEntity();
        realUser.setId(userId);
        realUser.setUsername("test_user");
        realUser.setEmail("test@example.com");
        realUser.setPassword("hash");
        realUser.setCreatedAt(LocalDateTime.now());
        realUser.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(realUser));

        Optional<UserEntity> foundOpt = searchService.findUserById(userId);

        assertThat(foundOpt).isPresent();
        assertThat(foundOpt.get().getId()).isEqualTo(userId);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void shouldReturnEmptyOptionalIfUserNotFound() {
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<UserEntity> found = searchService.findUserById(userId);

        assertThat(found).isEmpty();
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void shouldFindAllUsersWithSort() {
        UserEntity user1 = new UserEntity();
        user1.setUsername("user1");
        user1.setEmail("u1@example.com");
        user1.setPassword("hash1");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        UserEntity user2 = new UserEntity();
        user2.setUsername("user2");
        user2.setEmail("u2@example.com");
        user2.setPassword("hash2");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        when(userRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(user1, user2));

        List<UserEntity> found = searchService.findAllUsers("username", "asc");

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getUsername()).isEqualTo("user1");
        verify(userRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void shouldFindFunctionsByUserIdWithSort() {
        Long userId = 1L;

        UserEntity user1 = new UserEntity();
        user1.setId(100L);
        user1.setUsername("user1");
        user1.setEmail("u1@example.com");
        user1.setPassword("hash");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        FunctionEntity func1 = new FunctionEntity();
        func1.setUser(user1);
        func1.setName("func1");
        func1.setType("BASIC");
        func1.setExpression("x");
        func1.setLeftBound(0.0);
        func1.setRightBound(1.0);
        func1.setPointsCount(10);
        func1.setPointsData("{\"points\": []}");
        func1.setId(10L);
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());

        UserEntity user2 = new UserEntity();
        user2.setId(101L);
        user2.setUsername("user2");
        user2.setEmail("u2@example.com");
        user2.setPassword("hash");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        FunctionEntity func2 = new FunctionEntity();
        func2.setUser(user2);
        func2.setName("func2");
        func2.setType("COMPOSITE");
        func2.setExpression("x^2");
        func2.setLeftBound(0.0);
        func2.setRightBound(1.0);
        func2.setPointsCount(10);
        func2.setPointsData("{\"points\": []}");
        func2.setId(11L);
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());

        when(functionRepository.findByUserId(eq(userId), any(Sort.class))).thenReturn(Arrays.asList(func2, func1));

        List<FunctionDTO> functions = searchService.findFunctionsByUserIdSorted(userId, "name", "desc");

        assertThat(functions).hasSize(2);
        assertThat(functions.get(0).getName()).isEqualTo("func2");
        assertThat(functions.get(0).getUserId()).isEqualTo(101L);
        verify(functionRepository, times(1)).findByUserId(eq(userId), any(Sort.class));
    }

    @Test
    void shouldFindFunctionsSortedByField() {
        String field = "name";
        String direction = "ASC";

        UserEntity user1 = new UserEntity();
        user1.setId(100L);
        user1.setUsername("user1");
        user1.setEmail("u1@example.com");
        user1.setPassword("hash");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());

        FunctionEntity func1 = new FunctionEntity();
        func1.setUser(user1);
        func1.setName("a_func");
        func1.setType("BASIC");
        func1.setExpression("x");
        func1.setLeftBound(0.0);
        func1.setRightBound(1.0);
        func1.setPointsCount(10);
        func1.setPointsData("{\"points\": []}");
        func1.setId(10L);
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());

        UserEntity user2 = new UserEntity();
        user2.setId(101L);
        user2.setUsername("user2");
        user2.setEmail("u2@example.com");
        user2.setPassword("hash");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        FunctionEntity func2 = new FunctionEntity();
        func2.setUser(user2);
        func2.setName("b_func");
        func2.setType("BASIC");
        func2.setExpression("x^2");
        func2.setLeftBound(0.0);
        func2.setRightBound(1.0);
        func2.setPointsCount(10);
        func2.setPointsData("{\"points\": []}");
        func2.setId(11L);
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());

        when(functionRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(func1, func2));

        List<FunctionDTO> sortedFunctions = searchService.findAllFunctionsSorted(field, direction);

        assertThat(sortedFunctions).hasSize(2);
        assertThat(sortedFunctions.get(0).getName()).isEqualTo("a_func");
        assertThat(sortedFunctions.get(0).getUserId()).isEqualTo(100L);
        verify(functionRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void shouldFindDependencyFunctionsDFS() {
        Long resultFuncId = 3L;
        FunctionEntity resultFunc = new FunctionEntity();
        resultFunc.setName("result");
        resultFunc.setType("OPERATION_RESULT");
        resultFunc.setExpression("res");
        resultFunc.setLeftBound(0.0);
        resultFunc.setRightBound(1.0);
        resultFunc.setPointsCount(10);
        resultFunc.setPointsData("{\"points\": []}");
        resultFunc.setId(resultFuncId);
        resultFunc.setCreatedAt(LocalDateTime.now());
        resultFunc.setUpdatedAt(LocalDateTime.now());

        FunctionEntity depFunc1 = new FunctionEntity();
        depFunc1.setName("dep1");
        depFunc1.setType("BASIC");
        depFunc1.setExpression("x");
        depFunc1.setLeftBound(0.0);
        depFunc1.setRightBound(1.0);
        depFunc1.setPointsCount(10);
        depFunc1.setPointsData("{\"points\": []}");
        depFunc1.setId(1L);
        depFunc1.setCreatedAt(LocalDateTime.now());
        depFunc1.setUpdatedAt(LocalDateTime.now());

        FunctionEntity depFunc2 = new FunctionEntity();
        depFunc2.setName("dep2");
        depFunc2.setType("BASIC");
        depFunc2.setExpression("x^2");
        depFunc2.setLeftBound(0.0);
        depFunc2.setRightBound(1.0);
        depFunc2.setPointsCount(10);
        depFunc2.setPointsData("{\"points\": []}");
        depFunc2.setId(2L);
        depFunc2.setCreatedAt(LocalDateTime.now());
        depFunc2.setUpdatedAt(LocalDateTime.now());

        OperationEntity op = new OperationEntity();
        op.setUser(null);
        op.setFunction1(depFunc1);
        op.setFunction2(depFunc2);
        op.setResultFunction(resultFunc);
        op.setOperationType("ADD");
        op.setParameters("{\"param\": \"val\"}");
        op.setComputedAt(LocalDateTime.now());
        op.setUpdatedAt(LocalDateTime.now());
        op.setId(10L);

        when(functionRepository.findById(resultFuncId)).thenReturn(Optional.of(resultFunc));
        when(operationRepository.findByResultFunction_Id(resultFuncId)).thenReturn(Arrays.asList(op));
        when(functionRepository.findById(1L)).thenReturn(Optional.of(depFunc1));
        when(functionRepository.findById(2L)).thenReturn(Optional.of(depFunc2));
        when(operationRepository.findByResultFunction_Id(1L)).thenReturn(Collections.emptyList());
        when(operationRepository.findByResultFunction_Id(2L)).thenReturn(Collections.emptyList());

        Set<FunctionEntity> dependencies = searchService.findDependencyFunctionsDFS(resultFuncId);

        assertThat(dependencies).hasSize(3);
        assertThat(dependencies).extracting(FunctionEntity::getId).containsExactlyInAnyOrder(1L, 2L, 3L);
        verify(functionRepository, times(3)).findById(anyLong());
        verify(operationRepository, times(3)).findByResultFunction_Id(anyLong());
    }

    @Test
    void shouldFindDependencyFunctionsBFS() {
        Long startFuncId = 3L;
        int maxDepth = 1;
        FunctionEntity startFunc = new FunctionEntity();
        startFunc.setName("start");
        startFunc.setType("OPERATION_RESULT");
        startFunc.setExpression("start");
        startFunc.setLeftBound(0.0);
        startFunc.setRightBound(1.0);
        startFunc.setPointsCount(10);
        startFunc.setPointsData("{\"points\": []}");
        startFunc.setId(startFuncId);
        startFunc.setCreatedAt(LocalDateTime.now());
        startFunc.setUpdatedAt(LocalDateTime.now());

        FunctionEntity depFunc1 = new FunctionEntity();
        depFunc1.setName("dep1");
        depFunc1.setType("BASIC");
        depFunc1.setExpression("x");
        depFunc1.setLeftBound(0.0);
        depFunc1.setRightBound(1.0);
        depFunc1.setPointsCount(10);
        depFunc1.setPointsData("{\"points\": []}");
        depFunc1.setId(1L);
        depFunc1.setCreatedAt(LocalDateTime.now());
        depFunc1.setUpdatedAt(LocalDateTime.now());

        FunctionEntity depFunc2 = new FunctionEntity();
        depFunc2.setName("dep2");
        depFunc2.setType("BASIC");
        depFunc2.setExpression("x^2");
        depFunc2.setLeftBound(0.0);
        depFunc2.setRightBound(1.0);
        depFunc2.setPointsCount(10);
        depFunc2.setPointsData("{\"points\": []}");
        depFunc2.setId(2L);
        depFunc2.setCreatedAt(LocalDateTime.now());
        depFunc2.setUpdatedAt(LocalDateTime.now());

        OperationEntity op = new OperationEntity();
        op.setUser(null);
        op.setFunction1(depFunc1);
        op.setFunction2(depFunc2);
        op.setResultFunction(startFunc);
        op.setOperationType("ADD");
        op.setParameters("{\"param\": \"val\"}");
        op.setComputedAt(LocalDateTime.now());
        op.setUpdatedAt(LocalDateTime.now());
        op.setId(10L);

        when(functionRepository.findById(startFuncId)).thenReturn(Optional.of(startFunc));
        when(operationRepository.findByResultFunction_Id(startFuncId)).thenReturn(Arrays.asList(op));
        when(functionRepository.findById(1L)).thenReturn(Optional.of(depFunc1));
        when(functionRepository.findById(2L)).thenReturn(Optional.of(depFunc2));

        Set<FunctionEntity> dependencies = searchService.findDependencyFunctionsBFS(startFuncId, maxDepth);

        assertThat(dependencies).hasSize(3);
        assertThat(dependencies).extracting(FunctionEntity::getId).containsExactlyInAnyOrder(1L, 2L, 3L);
        verify(functionRepository, times(3)).findById(anyLong());
        verify(operationRepository, times(1)).findByResultFunction_Id(startFuncId);
    }

    @Test
    void shouldFindFunctionById() {
        Long functionId = 1L;

        UserEntity realUser = new UserEntity();
        realUser.setUsername("testUser");
        realUser.setEmail("test@example.com");
        realUser.setPassword("hash");
        realUser.setId(100L);
        realUser.setCreatedAt(LocalDateTime.now());
        realUser.setUpdatedAt(LocalDateTime.now());

        FunctionEntity realFunction = new FunctionEntity();
        realFunction.setUser(realUser);
        realFunction.setName("testFunc");
        realFunction.setType("BASIC");
        realFunction.setExpression("x");
        realFunction.setLeftBound(0.0);
        realFunction.setRightBound(1.0);
        realFunction.setPointsCount(10);
        realFunction.setPointsData("{\"points\": []}");
        realFunction.setId(functionId);
        realFunction.setCreatedAt(LocalDateTime.now());
        realFunction.setUpdatedAt(LocalDateTime.now());

        when(functionRepository.findById(functionId)).thenReturn(Optional.of(realFunction));

        Optional<FunctionDTO> foundFunctionOpt = searchService.findFunctionById(functionId);

        assertThat(foundFunctionOpt).isPresent();
        assertThat(foundFunctionOpt.get().getUserId()).isEqualTo(100L);
        assertThat(foundFunctionOpt.get().getName()).isEqualTo("testFunc");
        verify(functionRepository, times(1)).findById(functionId);
    }

    @Test
    void shouldFindOperationById() {
        Long operationId = 1L;

        UserEntity realUser = new UserEntity();
        realUser.setUsername("opUser");
        realUser.setEmail("op@example.com");
        realUser.setPassword("hash");
        realUser.setId(200L);
        realUser.setCreatedAt(LocalDateTime.now());
        realUser.setUpdatedAt(LocalDateTime.now());

        FunctionEntity realFunc1 = new FunctionEntity();
        realFunc1.setUser(realUser);
        realFunc1.setName("f1");
        realFunc1.setType("BASIC");
        realFunc1.setExpression("x");
        realFunc1.setLeftBound(0.0);
        realFunc1.setRightBound(1.0);
        realFunc1.setPointsCount(10);
        realFunc1.setPointsData("{\"points\": []}");
        realFunc1.setId(201L);
        realFunc1.setCreatedAt(LocalDateTime.now());
        realFunc1.setUpdatedAt(LocalDateTime.now());

        FunctionEntity realFunc2 = new FunctionEntity();
        realFunc2.setUser(realUser);
        realFunc2.setName("f2");
        realFunc2.setType("BASIC");
        realFunc2.setExpression("x^2");
        realFunc2.setLeftBound(0.0);
        realFunc2.setRightBound(1.0);
        realFunc2.setPointsCount(10);
        realFunc2.setPointsData("{\"points\": []}");
        realFunc2.setId(202L);
        realFunc2.setCreatedAt(LocalDateTime.now());
        realFunc2.setUpdatedAt(LocalDateTime.now());

        FunctionEntity realResultFunc = new FunctionEntity();
        realResultFunc.setUser(realUser);
        realResultFunc.setName("res");
        realResultFunc.setType("OPERATION_RESULT");
        realResultFunc.setExpression("x+x^2");
        realResultFunc.setLeftBound(0.0);
        realResultFunc.setRightBound(1.0);
        realResultFunc.setPointsCount(10);
        realResultFunc.setPointsData("{\"points\": []}");
        realResultFunc.setId(203L);
        realResultFunc.setCreatedAt(LocalDateTime.now());
        realResultFunc.setUpdatedAt(LocalDateTime.now());

        OperationEntity realOperation = new OperationEntity();
        realOperation.setUser(realUser);
        realOperation.setFunction1(realFunc1);
        realOperation.setFunction2(realFunc2);
        realOperation.setResultFunction(realResultFunc);
        realOperation.setOperationType("ADD");
        realOperation.setParameters("{}");
        realOperation.setComputedAt(LocalDateTime.now());
        realOperation.setUpdatedAt(LocalDateTime.now());
        realOperation.setId(operationId);

        when(operationRepository.findById(operationId)).thenReturn(Optional.of(realOperation));

        Optional<OperationDTO> foundOpOpt = searchService.findOperationById(operationId);

        assertThat(foundOpOpt).isPresent();
        assertThat(foundOpOpt.get().getOperationType()).isEqualTo("ADD");
        assertThat(foundOpOpt.get().getUserId()).isEqualTo(200L);
        verify(operationRepository, times(1)).findById(operationId);
    }

    @Test
    void shouldFindOperationsByUserId() {
        Long userId = 1L;

        UserEntity realUser = new UserEntity();
        realUser.setUsername("opUser");
        realUser.setEmail("op@example.com");
        realUser.setPassword("hash");
        realUser.setId(userId);
        realUser.setCreatedAt(LocalDateTime.now());
        realUser.setUpdatedAt(LocalDateTime.now());

        FunctionEntity realFunc1 = new FunctionEntity();
        realFunc1.setUser(realUser);
        realFunc1.setName("f1");
        realFunc1.setType("BASIC");
        realFunc1.setExpression("x");
        realFunc1.setLeftBound(0.0);
        realFunc1.setRightBound(1.0);
        realFunc1.setPointsCount(10);
        realFunc1.setPointsData("{\"points\": []}");
        realFunc1.setId(10L);
        realFunc1.setCreatedAt(LocalDateTime.now());
        realFunc1.setUpdatedAt(LocalDateTime.now());

        FunctionEntity realFunc2 = new FunctionEntity();
        realFunc2.setUser(realUser);
        realFunc2.setName("f2");
        realFunc2.setType("BASIC");
        realFunc2.setExpression("x^2");
        realFunc2.setLeftBound(0.0);
        realFunc2.setRightBound(1.0);
        realFunc2.setPointsCount(10);
        realFunc2.setPointsData("{\"points\": []}");
        realFunc2.setId(11L);
        realFunc2.setCreatedAt(LocalDateTime.now());
        realFunc2.setUpdatedAt(LocalDateTime.now());

        FunctionEntity realResultFunc = new FunctionEntity();
        realResultFunc.setUser(realUser);
        realResultFunc.setName("res");
        realResultFunc.setType("OPERATION_RESULT");
        realResultFunc.setExpression("x+x^2");
        realResultFunc.setLeftBound(0.0);
        realResultFunc.setRightBound(1.0);
        realResultFunc.setPointsCount(10);
        realResultFunc.setPointsData("{\"points\": []}");
        realResultFunc.setId(12L);
        realResultFunc.setCreatedAt(LocalDateTime.now());
        realResultFunc.setUpdatedAt(LocalDateTime.now());

        OperationEntity op1 = new OperationEntity();
        op1.setUser(realUser);
        op1.setFunction1(realFunc1);
        op1.setFunction2(realFunc2);
        op1.setResultFunction(realResultFunc);
        op1.setOperationType("ADD");
        op1.setParameters("{}");
        op1.setComputedAt(LocalDateTime.now());
        op1.setUpdatedAt(LocalDateTime.now());
        op1.setId(1L);

        OperationEntity op2 = new OperationEntity();
        op2.setUser(realUser);
        op2.setFunction1(realFunc1);
        op2.setFunction2(null);
        op2.setResultFunction(realResultFunc);
        op2.setOperationType("SUBTRACT");
        op2.setParameters("{}");
        op2.setComputedAt(LocalDateTime.now());
        op2.setUpdatedAt(LocalDateTime.now());
        op2.setId(2L);

        when(operationRepository.findByUser_Id(eq(userId), any(Sort.class))).thenReturn(Arrays.asList(op1, op2));

        List<OperationDTO> ops = searchService.findOperationsByUserIdSorted(userId, "id", "asc");

        assertThat(ops).hasSize(2);
        assertThat(ops).extracting(OperationDTO::getOperationType)
                .containsExactlyInAnyOrder("ADD", "SUBTRACT");
        assertThat(ops).extracting(OperationDTO::getUserId)
                .containsExactlyInAnyOrder(userId, userId);
        verify(operationRepository, times(1)).findByUser_Id(eq(userId), any(Sort.class));
    }
}