package lab5.service;

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
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
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
        UserEntity mockUser = new UserEntity("testUser", "test@example.com", "hash");
        mockUser.setId(userId);
        mockUser.setCreatedAt(LocalDateTime.now());
        mockUser.setUpdatedAt(LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        Optional<UserEntity> found = searchService.findUserById(userId);

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testUser");
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
        UserEntity user1 = new UserEntity("user1", "u1@example.com", "hash1");
        UserEntity user2 = new UserEntity("user2", "u2@example.com", "hash2");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        Sort sort = Sort.by(Sort.Direction.ASC, "username");
        when(userRepository.findAll(sort)).thenReturn(Arrays.asList(user1, user2));

        List<UserEntity> found = searchService.findAllUsers(sort);

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getUsername()).isEqualTo("user1");
        verify(userRepository, times(1)).findAll(sort);
    }

    @Test
    void shouldFindFunctionsByUserIdWithSort() {
        Long userId = 1L;
        FunctionEntity func1 = new FunctionEntity(null, "func1", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        FunctionEntity func2 = new FunctionEntity(null, "func2", "COMPOSITE", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());

        Sort sort = Sort.by(Sort.Direction.DESC, "name");
        when(functionRepository.findByUserId(eq(userId), any(Sort.class))).thenReturn(Arrays.asList(func2, func1));

        List<FunctionEntity> found = searchService.findFunctionsByUserId(userId, sort);

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getName()).isEqualTo("func2");
        verify(functionRepository, times(1)).findByUserId(eq(userId), any(Sort.class));
    }

    @Test
    void shouldFindUsersPaginated() {
        Pageable pageable = PageRequest.of(0, 10);
        UserEntity user1 = new UserEntity("user1", "u1@example.com", "hash1");
        UserEntity user2 = new UserEntity("user2", "u2@example.com", "hash2");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        List<UserEntity> users = Arrays.asList(user1, user2);
        Page<UserEntity> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserEntity> result = searchService.findUsersPaginated(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        verify(userRepository, times(1)).findAll(pageable);
    }

    @Test
    void shouldFindFunctionsSortedByField() {
        String field = "name";
        String direction = "ASC";
        FunctionEntity func1 = new FunctionEntity(null, "a_func", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        FunctionEntity func2 = new FunctionEntity(null, "b_func", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());

        when(functionRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(func1, func2));

        List<FunctionEntity> found = searchService.findFunctionsSortedByField(field, direction);

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getName()).isEqualTo("a_func");
        verify(functionRepository, times(1)).findAll(any(Sort.class));
    }

    @Test
    void shouldFindDependencyFunctionsDFS() {
        Long resultFuncId = 3L;
        FunctionEntity resultFunc = new FunctionEntity(null, "result", "OPERATION_RESULT", "res", 0.0, 1.0, 10, "{\"points\": []}");
        resultFunc.setId(resultFuncId);
        resultFunc.setCreatedAt(LocalDateTime.now());
        resultFunc.setUpdatedAt(LocalDateTime.now());

        FunctionEntity depFunc1 = new FunctionEntity(null, "dep1", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        depFunc1.setId(1L);
        depFunc1.setCreatedAt(LocalDateTime.now());
        depFunc1.setUpdatedAt(LocalDateTime.now());

        FunctionEntity depFunc2 = new FunctionEntity(null, "dep2", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        depFunc2.setId(2L);
        depFunc2.setCreatedAt(LocalDateTime.now());
        depFunc2.setUpdatedAt(LocalDateTime.now());

        OperationEntity op = new OperationEntity(null, depFunc1, depFunc2, resultFunc, "ADD", "{\"param\": \"val\"}");
        op.setId(10L);
        op.setComputedAt(LocalDateTime.now());
        op.setUpdatedAt(LocalDateTime.now());

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
        FunctionEntity startFunc = new FunctionEntity(null, "start", "OPERATION_RESULT", "start", 0.0, 1.0, 10, "{\"points\": []}");
        startFunc.setId(startFuncId);
        startFunc.setCreatedAt(LocalDateTime.now());
        startFunc.setUpdatedAt(LocalDateTime.now());

        FunctionEntity depFunc1 = new FunctionEntity(null, "dep1", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        depFunc1.setId(1L);
        depFunc1.setCreatedAt(LocalDateTime.now());
        depFunc1.setUpdatedAt(LocalDateTime.now());

        FunctionEntity depFunc2 = new FunctionEntity(null, "dep2", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        depFunc2.setId(2L);
        depFunc2.setCreatedAt(LocalDateTime.now());
        depFunc2.setUpdatedAt(LocalDateTime.now());

        OperationEntity op = new OperationEntity(null, depFunc1, depFunc2, startFunc, "ADD", "{\"param\": \"val\"}");
        op.setId(10L);
        op.setComputedAt(LocalDateTime.now());
        op.setUpdatedAt(LocalDateTime.now());

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
        FunctionEntity mockFunction = new FunctionEntity(null, "testFunc", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        mockFunction.setId(functionId);
        mockFunction.setCreatedAt(LocalDateTime.now());
        mockFunction.setUpdatedAt(LocalDateTime.now());
        when(functionRepository.findById(functionId)).thenReturn(Optional.of(mockFunction));

        Optional<FunctionEntity> found = searchService.findFunctionById(functionId);

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("testFunc");
        verify(functionRepository, times(1)).findById(functionId);
    }

    @Test
    void shouldFindOperationById() {
        Long operationId = 1L;
        OperationEntity mockOperation = new OperationEntity(null, null, null, null, "ADD", "{}");
        mockOperation.setId(operationId);
        mockOperation.setComputedAt(LocalDateTime.now());
        mockOperation.setUpdatedAt(LocalDateTime.now());
        when(operationRepository.findById(operationId)).thenReturn(Optional.of(mockOperation));

        Optional<OperationEntity> found = searchService.findOperationById(operationId);

        assertThat(found).isPresent();
        assertThat(found.get().getOperationType()).isEqualTo("ADD");
        verify(operationRepository, times(1)).findById(operationId);
    }

    @Test
    void shouldFindOperationsByUserId() {
        Long userId = 1L;
        OperationEntity op1 = new OperationEntity(null, null, null, null, "ADD", "{}");
        OperationEntity op2 = new OperationEntity(null, null, null, null, "SUBTRACT", "{}");
        op1.setId(1L);
        op2.setId(2L);
        op1.setComputedAt(LocalDateTime.now());
        op1.setUpdatedAt(LocalDateTime.now());
        op2.setComputedAt(LocalDateTime.now());
        op2.setUpdatedAt(LocalDateTime.now());

        when(operationRepository.findByUserId(userId)).thenReturn(Arrays.asList(op1, op2));

        List<OperationEntity> found = searchService.findOperationsByUserId(userId);

        assertThat(found).hasSize(2);
        assertThat(found).extracting(OperationEntity::getOperationType)
                .containsExactlyInAnyOrder("ADD", "SUBTRACT");
        verify(operationRepository, times(1)).findByUserId(userId);
    }
}