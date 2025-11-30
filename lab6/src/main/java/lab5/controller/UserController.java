package lab5.controller;

import lab5.entity.UserEntity;
import lab5.service.SearchService;
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
    private SearchService searchService;

    @GetMapping("/{id}")
    public ResponseEntity<UserEntity> getUserById(@PathVariable Long id) {
        logger.info("Получен запрос на получение пользователя с ID: {}", id);
        Optional<UserEntity> userOpt = searchService.findUserById(id);
        if (userOpt.isPresent()) {
            logger.debug("Возвращён пользователь с ID: {}", id);
            return new ResponseEntity<>(userOpt.get(), HttpStatus.OK);
        } else {
            logger.warn("Пользователь с ID {} не найден.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех пользователей, сортировка: {}:{}", sortField, sortDirection);
        List<UserEntity> users = searchService.findAllUsersSorted(sortField, sortDirection);
        logger.debug("Возвращено {} пользователей.", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity user) {
        logger.info("Получен запрос на создание пользователя: {}", user.getUsername());
        UserEntity createdUser = searchService.createUser(user.getUsername(), user.getEmail(), user.getPasswordHash());
        logger.info("Пользователь создан с ID: {}", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(@PathVariable Long id, @RequestBody UserEntity userDetails) {
        logger.info("Получен запрос на обновление пользователя с ID: {}", id);
        // Вызовите метод в SearchService для обновления
        UserEntity updatedUser = searchService.updateUser(id, userDetails);
        if (updatedUser != null) {
            logger.info("Пользователь с ID {} обновлён.", id);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            logger.warn("Пользователь с ID {} не найден для обновления.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("Получен запрос на удаление пользователя с ID: {}", id);
        boolean deleted = searchService.deleteUserById(id);
        if (deleted) {
            logger.info("Пользователь с ID {} удалён.", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("Пользователь с ID {} не найден для удаления.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search/username/{username}")
    public ResponseEntity<UserEntity> getUserByUsername(@PathVariable String username) {
        logger.info("Получен запрос на поиск пользователя по имени: {}", username);
        Optional<UserEntity> userOpt = searchService.findUserByUsername(username);
        if (userOpt.isPresent()) {
            logger.debug("Найден пользователь по имени: {}", username);
            return new ResponseEntity<>(userOpt.get(), HttpStatus.OK);
        } else {
            logger.warn("Пользователь с именем {} не найден.", username);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/search/email/{email}")
    public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String email) {
        logger.info("Получен запрос на поиск пользователя по email: {}", email);
        Optional<UserEntity> userOpt = searchService.findUserByEmail(email);
        if (userOpt.isPresent()) {
            logger.debug("Найден пользователь по email: {}", email);
            return new ResponseEntity<>(userOpt.get(), HttpStatus.OK);
        } else {
            logger.warn("Пользователь с email {} не найден.", email);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}