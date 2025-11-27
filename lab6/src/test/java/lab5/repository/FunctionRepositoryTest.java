package lab5.repository;

import lab5.entity.FunctionEntity;
import lab5.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class FunctionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FunctionRepository functionRepository;

    @Test
    void shouldSaveAndFindFunctionById() {
        UserEntity user = new UserEntity("funcUser", "fu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user);

        FunctionEntity function = new FunctionEntity(user, "testFunc", "BASIC", "x^2", 0.0, 1.0, 10, "{\"points\": []}");
        function.setCreatedAt(LocalDateTime.now());
        function.setUpdatedAt(LocalDateTime.now());

        FunctionEntity saved = functionRepository.save(function);
        Long savedId = saved.getId();

        assertThat(savedId).isNotNull();
        Optional<FunctionEntity> found = functionRepository.findById(savedId);
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("testFunc");
        assertThat(found.get().getUser().getId()).isEqualTo(user.getId());
    }

    @Test
    void shouldFindFunctionsByUserId() {
        UserEntity user = new UserEntity("funcUser2", "fu2@example.com", "hash2");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user);

        FunctionEntity func1 = new FunctionEntity(user, "func1", "BASIC", "x", 0.0, 1.0, 5, "{\"points\": []}");
        func1.setCreatedAt(LocalDateTime.now());
        func1.setUpdatedAt(LocalDateTime.now());
        FunctionEntity func2 = new FunctionEntity(user, "func2", "COMPOSITE", "sin(x)", 0.0, 2.0, 20, "{\"points\": []}");
        func2.setCreatedAt(LocalDateTime.now());
        func2.setUpdatedAt(LocalDateTime.now());
        entityManager.persist(func1);
        entityManager.persist(func2);
        entityManager.flush();

        List<FunctionEntity> found = functionRepository.findByUserId(user.getId());

        assertThat(found).hasSize(2);
        assertThat(found).extracting(FunctionEntity::getName).containsExactlyInAnyOrder("func1", "func2");
    }

    @Test
    void shouldDeleteFunction() {
        UserEntity user = new UserEntity("delFuncUser", "dfu@example.com", "hash");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(user);

        FunctionEntity function = new FunctionEntity(user, "toDeleteFunc", "BASIC", "x", 0.0, 1.0, 10, "{\"points\": []}");
        function.setCreatedAt(LocalDateTime.now());
        function.setUpdatedAt(LocalDateTime.now());
        entityManager.persistAndFlush(function);
        Long idToDelete = function.getId();
        assertThat(functionRepository.findById(idToDelete)).isPresent();

        functionRepository.deleteById(idToDelete);
        entityManager.flush();

        Optional<FunctionEntity> found = functionRepository.findById(idToDelete);
        assertThat(found).isEmpty();
    }
}