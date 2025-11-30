package lab5.repository;

import lab5.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserById() {
        String username = "testUser";
        String email = "test@example.com";
        String passwordHash = "hashed_password";
        UserEntity user = new UserEntity(username, email, passwordHash);

        UserEntity saved = userRepository.save(user);
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
        userRepository.save(user);

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
        UserEntity saved = userRepository.save(user);
        Long idToDelete = saved.getId();

        assertThat(userRepository.findById(idToDelete)).isPresent();

        userRepository.deleteById(idToDelete);

        Optional<UserEntity> found = userRepository.findById(idToDelete);
        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllUsers() {
        UserEntity user1 = new UserEntity("user1", "u1@example.com", "hash1");
        UserEntity user2 = new UserEntity("user2", "u2@example.com", "hash2");

        userRepository.save(user1);
        userRepository.save(user2);

        List<UserEntity> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserEntity::getUsername)
                .containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    void shouldFindAllUsersWithSort() {
        UserEntity user1 = new UserEntity("b_user", "b@example.com", "hash_b");
        UserEntity user2 = new UserEntity("a_user", "a@example.com", "hash_a");
        user1.setCreatedAt(LocalDateTime.now());
        user1.setUpdatedAt(LocalDateTime.now());
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user1);
        userRepository.save(user2);

        Sort sort = Sort.by(Sort.Direction.ASC, "username");
        List<UserEntity> found = userRepository.findAll(sort);

        assertThat(found).hasSize(2);
        assertThat(found.get(0).getUsername()).isEqualTo("a_user");
        assertThat(found.get(1).getUsername()).isEqualTo("b_user");
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