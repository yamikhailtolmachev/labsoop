package lab5.controller;

import lab5.dto.UserDTO;
import lab5.dto.UserWithRoleDTO;
import lab5.entity.UserEntity;
import lab5.service.UserService;
import lab5.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SearchService searchService;

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех пользователей, сортировка: {}:{}", sortField, sortDirection);
        try {
            List<UserEntity> users = searchService.findAllUsers(sortField, sortDirection);
            List<UserDTO> userDTOs = users.stream()
                    .map(user -> new UserDTO(
                            user.getId(),
                            user.getUsername(),
                            user.getEmail(),
                            user.getRole().getName()))
                    .collect(Collectors.toList());
            logger.debug("Найдено {} пользователей.", users.size());
            return ResponseEntity.ok(userDTOs);
        } catch (IllegalArgumentException e) {
            logger.warn("Некорректный параметр сортировки: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Некорректный параметр сортировки: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Ошибка при получении всех пользователей: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/me")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        logger.info("Получен запрос на получение профиля текущего пользователя");
        try {
            String username = authentication.getName();
            Optional<UserEntity> userOpt = userService.getUserByUsername(username);

            if (userOpt.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Пользователь не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            UserEntity user = userOpt.get();
            UserDTO userDTO = new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().getName());

            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            logger.error("Ошибка при получении профиля: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserDTO userDTO) {
        logger.info("Получен запрос на создание пользователя: {}", userDTO.getUsername());
        try {
            if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Имя пользователя обязательно");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Email обязателен");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Пароль обязателен");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            UserEntity createdUser = userService.createUserWithDefaultRole(
                    userDTO.getUsername(),
                    userDTO.getEmail(),
                    userDTO.getPassword());

            if (createdUser != null) {
                UserDTO responseDTO = new UserDTO(
                        createdUser.getId(),
                        createdUser.getUsername(),
                        createdUser.getEmail(),
                        createdUser.getRole().getName());

                logger.info("Пользователь создан с ID: {}, роль: {}",
                        createdUser.getId(), createdUser.getRole().getName());

                return ResponseEntity.status(HttpStatus.CREATED)
                        .header("Location", "/api/users/" + createdUser.getId())
                        .body(responseDTO);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Не удалось создать пользователя");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Некорректные данные пользователя: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id, Authentication authentication) {
        logger.info("Получен запрос на получение пользователя по ID: {}", id);
        try {
            if (id == null || id <= 0) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Некорректный ID пользователя");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String currentUsername = authentication.getName();
            Optional<UserEntity> currentUserOpt = userService.getUserByUsername(currentUsername);

            if (currentUserOpt.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Текущий пользователь не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            UserEntity currentUser = currentUserOpt.get();
            boolean isAdmin = "ADMIN".equals(currentUser.getRole().getName());
            boolean isOwnProfile = currentUser.getId().equals(id);

            if (!isAdmin && !isOwnProfile) {
                logger.warn("Пользователь {} пытается получить доступ к профилю пользователя {}", currentUsername, id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Доступ запрещен. Вы можете просматривать только свой профиль");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            Optional<UserEntity> userOpt = userService.getUserById(id);
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                UserDTO userDTO = new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().getName());
                logger.debug("Возвращён пользователь по ID: {}", id);
                return ResponseEntity.ok(userDTO);
            } else {
                logger.warn("Пользователь с ID '{}' не найден.", id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Пользователь с ID " + id + " не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении пользователя по ID {}: {}", id, e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDetails, Authentication authentication) {
        logger.info("Получен запрос на обновление пользователя с ID: {}", id);
        try {
            if (id == null || id <= 0) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Некорректный ID пользователя");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String currentUsername = authentication.getName();
            Optional<UserEntity> currentUserOpt = userService.getUserByUsername(currentUsername);

            if (currentUserOpt.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Текущий пользователь не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            UserEntity currentUser = currentUserOpt.get();
            boolean isAdmin = "ADMIN".equals(currentUser.getRole().getName());
            boolean isOwnProfile = currentUser.getId().equals(id);

            if (!isAdmin && !isOwnProfile) {
                logger.warn("Пользователь {} пытается обновить профиль пользователя {}", currentUsername, id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Доступ запрещен. Вы можете обновлять только свой профиль");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            UserEntity updatedUser = userService.updateUser(id, userDetails);
            if (updatedUser != null) {
                UserDTO userDTO = new UserDTO(
                        updatedUser.getId(),
                        updatedUser.getUsername(),
                        updatedUser.getEmail(),
                        updatedUser.getRole().getName());
                logger.info("Пользователь с ID {} обновлён.", id);
                return ResponseEntity.ok(userDTO);
            } else {
                logger.warn("Пользователь с ID '{}' не найден для обновления.", id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Пользователь с ID " + id + " не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Некорректные данные для обновления пользователя: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Ошибка при обновлении пользователя с ID {}: {}", id, e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/search/username/{username}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username, Authentication authentication) {
        logger.info("Получен запрос на поиск пользователя по имени: {}", username);
        try {
            if (username == null || username.trim().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Имя пользователя не может быть пустым");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String currentUsername = authentication.getName();
            Optional<UserEntity> currentUserOpt = userService.getUserByUsername(currentUsername);

            if (currentUserOpt.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Текущий пользователь не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            UserEntity currentUser = currentUserOpt.get();
            boolean isAdmin = "ADMIN".equals(currentUser.getRole().getName());
            boolean isSearchingSelf = currentUser.getUsername().equals(username);

            if (!isAdmin && !isSearchingSelf) {
                logger.warn("Пользователь {} пытается найти другого пользователя {}", currentUsername, username);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Доступ запрещен. Вы можете искать только себя");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            Optional<UserEntity> userOpt = userService.getUserByUsername(username);
            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();
                UserDTO userDTO = new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole().getName());
                logger.debug("Возвращён пользователь по имени: {}", username);
                return ResponseEntity.ok(userDTO);
            } else {
                logger.warn("Пользователь с именем '{}' не найден.", username);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Пользователь с именем '" + username + "' не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Ошибка при поиске пользователя по имени: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, Authentication authentication) {
        logger.info("Получен запрос на удаление пользователя с ID: {}", id);
        try {
            if (id == null || id <= 0) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Некорректный ID пользователя");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            String currentUsername = authentication.getName();
            Optional<UserEntity> currentUserOpt = userService.getUserByUsername(currentUsername);

            if (currentUserOpt.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Текущий пользователь не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            UserEntity currentUser = currentUserOpt.get();

            if (!"ADMIN".equals(currentUser.getRole().getName())) {
                logger.warn("Пользователь {} (не ADMIN) пытается удалить пользователя {}", currentUsername, id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Доступ запрещен. Только администраторы могут удалять пользователей");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            if (currentUser.getId().equals(id)) {
                logger.warn("Админ {} пытается удалить себя", currentUsername);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Вы не можете удалить свой собственный аккаунт");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            Optional<UserEntity> userToDeleteOpt = userService.getUserById(id);
            if (userToDeleteOpt.isEmpty()) {
                logger.warn("Пользователь с ID {} не найден для удаления.", id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Пользователь с ID " + id + " не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            boolean deleted = userService.deleteUser(id);
            if (deleted) {
                logger.info("Пользователь с ID {} удалён.", id);
                return ResponseEntity.noContent().build();
            } else {
                logger.warn("Не удалось удалить пользователя с ID {}", id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Не удалось удалить пользователя с ID " + id);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Ошибка при удалении пользователя с ID {}: {}", id, e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/with-role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUserWithRole(@RequestBody UserWithRoleDTO userWithRoleDTO) {
        logger.info("Получен запрос на создание пользователя с ролью: {}", userWithRoleDTO.getUsername());
        try {
            if (userWithRoleDTO.getUsername() == null || userWithRoleDTO.getUsername().trim().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Имя пользователя обязательно");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (userWithRoleDTO.getEmail() == null || userWithRoleDTO.getEmail().trim().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Email обязателен");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            if (userWithRoleDTO.getPassword() == null || userWithRoleDTO.getPassword().trim().isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Пароль обязателен");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Long roleId = userWithRoleDTO.getRoleId();
            if (roleId == null) {
                roleId = 2L;
                logger.debug("RoleId не указан, используется дефолтная роль USER (ID={})", roleId);
            }

            if (roleId <= 0) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Некорректный ID роли");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            UserEntity createdUser = userService.createUserWithRole(
                    userWithRoleDTO.getUsername(),
                    userWithRoleDTO.getEmail(),
                    userWithRoleDTO.getPassword(),
                    roleId);

            if (createdUser != null) {
                UserDTO responseDTO = new UserDTO(
                        createdUser.getId(),
                        createdUser.getUsername(),
                        createdUser.getEmail(),
                        createdUser.getRole().getName());
                logger.info("Пользователь с ролью создан с ID: {}", createdUser.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
            } else {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Не удалось создать пользователя с указанной ролью");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (IllegalArgumentException e) {
            logger.warn("Некорректные данные для создания пользователя с ролью: {}", e.getMessage());
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя с ролью: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}