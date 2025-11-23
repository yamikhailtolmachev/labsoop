package lab5.repository;

import lab5.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/postgres",
        "spring.datasource.username=postgres",
        "spring.datasource.password=password",
        "spring.datasource.driver-class-name=org.postgresql.Driver",

//        "spring.jpa.hibernate.ddl-auto=create-drop",
//        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
//        "spring.jpa.show-sql=true"
})
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserById() {
        String username = "testUser";
        String email = "test@example.com";
        String passwordHash = "hashed_password";
        UserEntity user = new UserEntity(username, email, passwordHash);

        UserEntity saved = entityManager.persistAndFlush(user);
        Long savedId = saved.getId();

        assertThat(savedId).isNotNull();
        Optional<UserEntity> found = userRepository.findById(savedId);
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo(username);
        assertThat(found.get().getEmail()).isEqualTo(email);
    }

    @Test
    void shouldFindUserByUsername() {
        String username = "findMe";
        String email = "findme@example.com";
        String passwordHash = "hash";
        UserEntity user = new UserEntity(username, email, passwordHash);
        entityManager.persistAndFlush(user);

        Optional<UserEntity> found = userRepository.findByUsername(username);

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo(email);
    }

    @Test
    void shouldDeleteUser() {
        String username = "toDelete";
        String email = "delete@example.com";
        String passwordHash = "hash";
        UserEntity user = new UserEntity(username, email, passwordHash);
        UserEntity saved = entityManager.persistAndFlush(user);
        Long idToDelete = saved.getId();

        assertThat(userRepository.findById(idToDelete)).isPresent();

        userRepository.deleteById(idToDelete);
        entityManager.flush();

        Optional<UserEntity> found = userRepository.findById(idToDelete);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllUsers() {
        UserEntity user1 = new UserEntity("user1", "u1@example.com", "hash1");
        UserEntity user2 = new UserEntity("user2", "u2@example.com", "hash2");

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        List<UserEntity> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserEntity::getUsername)
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void shouldSaveUsingRepository() {
        String username = "repoUser";
        String email = "repo@example.com";
        String passwordHash = "hash";
        UserEntity user = new UserEntity(username, email, passwordHash);

        UserEntity saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo(username);
        assertThat(saved.getEmail()).isEqualTo(email);
    }
}