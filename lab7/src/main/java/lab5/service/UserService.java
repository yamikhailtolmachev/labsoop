package lab5.service;

import lab5.dto.UserDTO;
import lab5.entity.RoleEntity;
import lab5.entity.UserEntity;
import lab5.repository.RoleRepository;
import lab5.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity createUser(String username, String email, String rawPassword) {
        logger.info("Создание нового пользователя: username='{}', email='{}'", username, email);

        if (userRepository.existsByUsername(username)) {
            logger.warn("Попытка создать пользователя с уже существующим именем: {}", username);
            throw new IllegalArgumentException("Имя пользователя уже занято: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            logger.warn("Попытка создать пользователя с уже существующим email: {}", email);
            throw new IllegalArgumentException("Email уже зарегистрирован: " + email);
        }

        RoleEntity defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Роль по умолчанию 'USER' не найдена в базе данных."));

        String encodedPassword = passwordEncoder.encode(rawPassword);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(defaultRole);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        UserEntity savedUser = userRepository.save(user);
        logger.info("Пользователь успешно создан с ID: {}", savedUser.getId());
        return savedUser;
    }

    @Transactional
    public UserEntity createUserWithRole(String username, String email, String rawPassword, Long roleId) {
        logger.info("Создание пользователя с ролью: username='{}', email='{}', roleId={}",
                username, email, roleId);

        if (userRepository.existsByUsername(username)) {
            logger.warn("Попытка создать пользователя с уже существующим именем: {}", username);
            throw new IllegalArgumentException("Имя пользователя уже занято: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            logger.warn("Попытка создать пользователя с уже существующим email: {}", email);
            throw new IllegalArgumentException("Email уже зарегистрирован: " + email);
        }

        RoleEntity role;
        if (roleId == null) {
            logger.debug("RoleId не указан, используется дефолтная роль USER");
            role = roleRepository.findByName("USER")
                    .orElseThrow(() -> new RuntimeException("Роль 'USER' не найдена."));
        } else {
            role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new RuntimeException("Роль с ID " + roleId + " не найдена."));
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole(role);

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        UserEntity savedUser = userRepository.save(user);
        logger.info("Пользователь с ролью успешно создан с ID: {}", savedUser.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            logger.debug("Найден пользователь: ID={}, Username={}", userOpt.get().getId(), userOpt.get().getUsername());
        } else {
            logger.warn("Пользователь с ID {} не найден.", id);
        }
        return userOpt;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getAllUsers() {
        logger.info("Поиск всех пользователей.");
        List<UserEntity> users = userRepository.findAll();
        logger.debug("Найдено {} пользователей.", users.size());
        return users;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsersForAdmin(String sortField, String sortDirection) {
        logger.info("Поиск всех пользователей для админа, сортировка: {} {}", sortField, sortDirection);

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        List<UserEntity> users = userRepository.findAll(sort);

        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole() != null ? user.getRole().getName() : "N/A"))
                .collect(Collectors.toList());

        logger.debug("Найдено {} пользователей для админа.", users.size());
        return userDTOs;
    }

    @Transactional(readOnly = true)
    public List<UserEntity> getUsersByRole(String roleName) {
        logger.info("Поиск пользователей по роли: {}", roleName);
        List<UserEntity> users = userRepository.findByRole_Name(roleName);
        logger.debug("Найдено {} пользователей с ролью '{}'.", users.size(), roleName);
        return users;
    }

    @Transactional
    public UserEntity assignRoleToUser(Long userId, String roleName) {
        logger.info("Попытка назначить роль '{}' пользователю ID: {}", roleName, userId);

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь с ID " + userId + " не найден."));

        RoleEntity newRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Роль '" + roleName + "' не найдена."));

        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity updatedUser = userRepository.save(user);

        logger.info("Роль '{}' успешно назначена пользователю ID: {}", roleName, userId);
        return updatedUser;
    }

    @Transactional
    public UserEntity updateUser(Long id, UserDTO userDetails) {
        logger.info("Обновление пользователя с ID: {}", id);
        Optional<UserEntity> existingUserOpt = userRepository.findById(id);
        if (existingUserOpt.isPresent()) {
            UserEntity existingUser = existingUserOpt.get();
            if (userDetails.getUsername() != null) {
                existingUser.setUsername(userDetails.getUsername());
            }
            if (userDetails.getEmail() != null) {
                existingUser.setEmail(userDetails.getEmail());
            }
            existingUser.setUpdatedAt(LocalDateTime.now());
            UserEntity updated = userRepository.save(existingUser);
            logger.info("Пользователь с ID {} обновлён.", id);
            return updated;
        } else {
            logger.warn("Пользователь с ID {} не найден для обновления.", id);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserByUsername(String username) {
        logger.info("Поиск пользователя по имени: {}", username);
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            logger.debug("Найден пользователь по имени: ID={}, Username={}",
                    userOpt.get().getId(), userOpt.get().getUsername());
        } else {
            logger.warn("Пользователь с именем {} не найден.", username);
        }
        return userOpt;
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> getUserByEmail(String email) {
        logger.info("Поиск пользователя по email: {}", email);
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            logger.debug("Найден пользователь по email: ID={}, Email={}",
                    userOpt.get().getId(), userOpt.get().getEmail());
        } else {
            logger.warn("Пользователь с email {} не найден.", email);
        }
        return userOpt;
    }

    @Transactional
    public UserEntity createUserWithDefaultRole(String username, String email, String password) {
        logger.info("Создание пользователя с дефолтной ролью USER: {}", username);

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Пользователь с именем '" + username + "' уже существует");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Пользователь с email '" + email + "' уже существует");
        }

        Optional<RoleEntity> roleOpt = roleRepository.findById(2L);
        if (roleOpt.isEmpty()) {
            throw new IllegalArgumentException("Роль USER не найдена в системе");
        }

        RoleEntity userRole = roleOpt.get();

        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(userRole);

        UserEntity savedUser = userRepository.save(user);
        logger.info("Пользователь создан с ID: {}, роль: {}", savedUser.getId(), savedUser.getRole().getName());

        return savedUser;
    }

    @Transactional
    public boolean deleteUser(Long id) {
        logger.info("Удаление пользователя с ID: {}", id);

        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            logger.warn("Пользователь с ID {} не найден", id);
            return false;
        }

        UserEntity user = userOpt.get();

        if ("ADMIN".equals(user.getRole().getName())) {
            logger.warn("Попытка удаления пользователя с ролью ADMIN, ID: {}", id);
        }

        userRepository.delete(user);
        logger.info("Пользователь с ID {} удален", id);

        return true;
    }
}