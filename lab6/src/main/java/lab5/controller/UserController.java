package lab5.controller;

import lab5.dto.UserDTO;
import lab5.entity.UserEntity;
import lab5.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        logger.info("Получен запрос на получение пользователя с ID: {}", id);
        Optional<UserEntity> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            logger.debug("Возвращён пользователь с ID: {}", id);
            return ResponseEntity.ok(userOpt.get());
        } else {
            logger.warn("Пользователь с ID {} не найден.", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех пользователей, сортировка: {}:{}", sortField, sortDirection);
        List<UserEntity> users = userService.getAllUsers(sortField, sortDirection);
        logger.debug("Возвращено {} пользователей.", users.size());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserDTO userDTO) {
        logger.info("Получен запрос на создание пользователя: {}", userDTO.getUsername());
        try {
            UserEntity createdUser = userService.createUser(userDTO);
            logger.info("Пользователь создан с ID: {}", createdUser.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        logger.info("Получен запрос на обновление пользователя с ID: {}", id);
        try {
            UserEntity updatedUser = userService.updateUser(id, userDTO);
            if (updatedUser != null) {
                logger.info("Пользователь с ID {} обновлён.", id);
                return ResponseEntity.ok(updatedUser);
            } else {
                logger.warn("Пользователь с ID {} не найден для обновления.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Получен запрос на удаление пользователя с ID: {}", id);
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            logger.info("Пользователь с ID {} удалён.", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Пользователь с ID {} не найден для удаления.", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/username/{username}")
    public ResponseEntity<UserEntity> getUserByUsername(@PathVariable String username) {
        logger.info("Получен запрос на поиск пользователя по имени: {}", username);
        Optional<UserEntity> userOpt = userService.getUserByUsername(username);
        if (userOpt.isPresent()) {
            logger.debug("Найден пользователь по имени: {}", username);
            return ResponseEntity.ok(userOpt.get());
        } else {
            logger.warn("Пользователь с именем {} не найден.", username);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/email/{email}")
    public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String email) {
        logger.info("Получен запрос на поиск пользователя по email: {}", email);
        Optional<UserEntity> userOpt = userService.getUserByEmail(email);
        if (userOpt.isPresent()) {
            logger.debug("Найден пользователь по email: {}", email);
            return ResponseEntity.ok(userOpt.get());
        } else {
            logger.warn("Пользователь с email {} не найден.", email);
            return ResponseEntity.notFound().build();
        }
    }
}