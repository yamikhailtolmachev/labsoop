package lab5.service;

import lab5.dto.UserDTO;
import lab5.entity.UserEntity;
import lab5.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserEntity createUser(UserDTO userDTO) {
        try {
            logger.info("Создание нового пользователя: username='{}', email='{}'",
                    userDTO.getUsername(), userDTO.getEmail());

            if (userRepository.existsByUsername(userDTO.getUsername())) {
                logger.warn("Имя пользователя '{}' уже занято.", userDTO.getUsername());
                throw new RuntimeException("Имя пользователя уже занято: " + userDTO.getUsername());
            }

            if (userRepository.existsByEmail(userDTO.getEmail())) {
                logger.warn("Email '{}' уже зарегистрирован.", userDTO.getEmail());
                throw new RuntimeException("Email уже зарегистрирован: " + userDTO.getEmail());
            }

            UserEntity user = new UserEntity();
            user.setUsername(userDTO.getUsername());
            user.setEmail(userDTO.getEmail());
            user.setPasswordHash(userDTO.getPasswordHash());

            UserEntity savedUser = userRepository.save(user);
            logger.info("Пользователь создан с ID: {}", savedUser.getId());

            return savedUser;

        } catch (Exception e) {
            logger.error("Ошибка при создании пользователя: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при создании пользователя: " + e.getMessage(), e);
        }
    }

    public Optional<UserEntity> getUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        Optional<UserEntity> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            logger.debug("Пользователь с ID {} найден.", id);
        } else {
            logger.warn("Пользователь с ID {} не найден.", id);
        }
        return userOpt;
    }

    public List<UserEntity> getAllUsers(String sortField, String sortDirection) {
        logger.info("Поиск всех пользователей с сортировкой по полю '{}' в направлении '{}'",
                sortField, sortDirection);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        List<UserEntity> users = userRepository.findAll(sort);
        logger.debug("Найдено {} пользователей.", users.size());
        return users;
    }

    @Transactional
    public UserEntity updateUser(Long id, UserDTO userDTO) {
        logger.info("Обновление пользователя с ID: {}", id);

        return userRepository.findById(id)
                .map(existingUser -> {
                    try {
                        if (!existingUser.getUsername().equals(userDTO.getUsername()) &&
                                userRepository.existsByUsername(userDTO.getUsername())) {
                            logger.warn("Имя пользователя '{}' уже занято.", userDTO.getUsername());
                            throw new RuntimeException("Имя пользователя уже занято: " + userDTO.getUsername());
                        }

                        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                                userRepository.existsByEmail(userDTO.getEmail())) {
                            logger.warn("Email '{}' уже зарегистрирован.", userDTO.getEmail());
                            throw new RuntimeException("Email уже зарегистрирован: " + userDTO.getEmail());
                        }

                        existingUser.setUsername(userDTO.getUsername());
                        existingUser.setEmail(userDTO.getEmail());
                        existingUser.setPasswordHash(userDTO.getPasswordHash());
                        existingUser.setUpdatedAt(LocalDateTime.now());

                        UserEntity updatedUser = userRepository.save(existingUser);
                        logger.info("Пользователь с ID {} обновлён.", id);

                        return updatedUser;

                    } catch (Exception e) {
                        logger.error("Ошибка при обновлении пользователя: {}", e.getMessage(), e);
                        throw new RuntimeException("Ошибка при обновлении пользователя: " + e.getMessage(), e);
                    }
                })
                .orElse(null);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        logger.info("Удаление пользователя с ID: {}", id);
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("Пользователь с ID {} удалён.", id);
            return true;
        } else {
            logger.warn("Пользователь с ID {} не найден для удаления.", id);
            return false;
        }
    }

    public Optional<UserEntity> getUserByUsername(String username) {
        logger.info("Поиск пользователя по имени: {}", username);
        Optional<UserEntity> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            logger.debug("Пользователь с именем '{}' найден.", username);
        } else {
            logger.warn("Пользователь с именем '{}' не найден.", username);
        }
        return userOpt;
    }

    public Optional<UserEntity> getUserByEmail(String email) {
        logger.info("Поиск пользователя по email: {}", email);
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            logger.debug("Пользователь с email '{}' найден.", email);
        } else {
            logger.warn("Пользователь с email '{}' не найден.", email);
        }
        return userOpt;
    }
}