package lab5.repository;

import lab5.entity.FunctionEntity;
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
class FunctionRepositoryTest {
    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindFunctionById() {
        UserEntity user = new UserEntity("funcUser", "fu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);

        FunctionEntity function = new FunctionEntity();
        function.setUser(savedUser);
        function.setName("testFunc");
        function.setType("BASIC");
        function.setExpression("x^2");
        function.setLeftBound(0.0);
        function.setRightBound(1.0);
        function.setPointsCount(10);
        function.setPointsData("{\"points\": []}");
        function.setCreatedAt(LocalDateTime.now());
        function.setUpdatedAt(LocalDateTime.now());

        FunctionEntity saved = functionRepository.save(function);
        Long savedId = saved.getId();

        assertThat(savedId).isNotNull();
        Optional<FunctionEntity> found = functionRepository.findById(savedId);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("testFunc");
        assertThat(found.get().getUser().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void shouldFindFunctionsByUserId() {
        UserEntity user = new UserEntity("funcUser2", "fu2@example.com", "hash2");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);

        FunctionEntity func1 = new FunctionEntity();
        func1.setUser(savedUser);
        func1.setName("func1");
        func1.setType("BASIC");
        func1.setExpression("x");
        func1.setLeftBound(0.0);
        func1.setRightBound(1.0);
        func1.setPointsCount(5);
        func1.setPointsData("{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        functionRepository.save(func1);

        FunctionEntity func2 = new FunctionEntity();
        func2.setUser(savedUser);
        func2.setName("func2");
        func2.setType("COMPOSITE");
        func2.setExpression("sin(x)");
        func2.setLeftBound(0.0);
        func2.setRightBound(2.0);
        func2.setPointsCount(20);
        func2.setPointsData("{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        functionRepository.save(func2);

        List<FunctionEntity> found = functionRepository.findByUserId(savedUser.getId());

        assertThat(found).hasSize(2);
        assertThat(found).extracting(FunctionEntity::getName)
                .containsExactlyInAnyOrder("func1", "func2");
    }

    @Test
    void shouldDeleteFunction() {
        UserEntity user = new UserEntity("delFuncUser", "dfu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity savedUser = userRepository.save(user);

        FunctionEntity function = new FunctionEntity();
        function.setUser(savedUser);
        function.setName("toDeleteFunc");
        function.setType("BASIC");
        function.setExpression("x");
        function.setLeftBound(0.0);
        function.setRightBound(1.0);
        function.setPointsCount(10);
        function.setPointsData("{\"points\": []}");
        function.setCreatedAt(LocalDateTime.now());
        function.setUpdatedAt(LocalDateTime.now());
        FunctionEntity savedFunction = functionRepository.save(function);

        Long idToDelete = savedFunction.getId();
        assertThat(functionRepository.findById(idToDelete)).isPresent();

        functionRepository.deleteById(idToDelete);

        Optional<FunctionEntity> found = functionRepository.findById(idToDelete);
        assertThat(found).isEmpty();
    }
}