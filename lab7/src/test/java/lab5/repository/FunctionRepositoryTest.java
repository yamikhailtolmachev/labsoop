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

        FunctionEntity function = new FunctionEntity(savedUser, "testFunc", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
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

        FunctionEntity func1 = new FunctionEntity(savedUser, "func1", "BASIC", "x", 0.0, 1.0, 5, "{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        functionRepository.save(func1);

        FunctionEntity func2 = new FunctionEntity(savedUser, "func2", "COMPOSITE", "sin(x)", 0.0, 2.0, 20, "{\"points\": []}");
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

        FunctionEntity function = new FunctionEntity(savedUser, "toDeleteFunc", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
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