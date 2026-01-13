package lab5.config;

import lab5.entity.FunctionEntity;
import lab5.entity.RoleEntity;
import lab5.entity.UserEntity;
import lab5.repository.FunctionRepository;
import lab5.repository.RoleRepository;
import lab5.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        logger.info("Инициализация начальных данных");
        RoleEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName("ADMIN");
                    logger.info("Создана роль: ADMIN");
                    return roleRepository.save(role);
                });

        RoleEntity userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    RoleEntity role = new RoleEntity();
                    role.setName("USER");
                    logger.info("Создана роль: USER");
                    return roleRepository.save(role);
                });

        Optional<UserEntity> existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin.isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(adminRole);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin);
            logger.info("Создан пользователь admin с паролем admin123, роль ADMIN");
        } else {
            logger.info("Пользователь admin уже существует.");
            UserEntity admin = existingAdmin.get();
            if (!"ADMIN".equals(admin.getRole().getName())) {
                admin.setRole(adminRole);
                userRepository.save(admin);
                logger.info("Роль пользователя admin обновлена на ADMIN.");
            }
        }

        Optional<UserEntity> existingUser1 = userRepository.findByUsername("user1");
        if (existingUser1.isEmpty()) {
            UserEntity user1 = new UserEntity();
            user1.setUsername("user1");
            user1.setEmail("user1@example.com");
            user1.setPassword(passwordEncoder.encode("mypassword123"));
            user1.setRole(adminRole);
            user1.setCreatedAt(LocalDateTime.now());
            user1.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user1);
            logger.info("Создан пользователь user1 с паролем mypassword123, роль ADMIN");
        } else {
            logger.info("Пользователь user1 уже существует.");
            UserEntity user1 = existingUser1.get();
            if (!"ADMIN".equals(user1.getRole().getName())) {
                user1.setRole(adminRole);
                userRepository.save(user1);
                logger.info("Роль пользователя user1 обновлена на ADMIN.");
            }
        }

        Optional<UserEntity> existingUser40ByUsername = userRepository.findByUsername("testuser40");
        Optional<UserEntity> existingUser40ByEmail = userRepository.findByEmail("testuser40@example.com");

        if (existingUser40ByUsername.isEmpty() && existingUser40ByEmail.isEmpty()) {
            UserEntity testUser40 = new UserEntity();
            testUser40.setUsername("testuser40");
            testUser40.setEmail("testuser40@example.com");
            testUser40.setPassword(passwordEncoder.encode("test123"));
            testUser40.setRole(userRole);
            testUser40.setCreatedAt(LocalDateTime.now());
            testUser40.setUpdatedAt(LocalDateTime.now());
            userRepository.save(testUser40);
            logger.info("Создан тестовый пользователь для тестов с username='testuser40', email='testuser40@example.com'");
        } else {
            String existingUsername = existingUser40ByUsername.map(UserEntity::getUsername).orElse(null);
            String existingEmail = existingUser40ByEmail.map(UserEntity::getEmail).orElse(null);
            logger.info("Тестовый пользователь 'testuser40' уже существует (username: {}, email: {}).", existingUsername, existingEmail);
        }

        Optional<UserEntity> existingUserWithId1 = userRepository.findById(1L);

        if (existingUserWithId1.isPresent()) {
            UserEntity userWithId1 = existingUserWithId1.get();
            logger.info("Пользователь с ID=1 уже существует: {} (роль: {}).", userWithId1.getUsername(), userWithId1.getRole().getName());
            if (!"USER".equals(userWithId1.getRole().getName()) && !"ADMIN".equals(userWithId1.getRole().getName())) {
                userWithId1.setRole(userRole);
                userRepository.save(userWithId1);
                logger.info("Роль пользователя с ID=1 обновлена на USER.");
            }
        } else {
            String id1Username = "user_with_id_1";
            String id1Email = "user_with_id_1@example.com";

            UserEntity userWithId1 = new UserEntity();
            userWithId1.setId(1L);
            userWithId1.setUsername(id1Username);
            userWithId1.setEmail(id1Email);
            userWithId1.setPassword(passwordEncoder.encode("default_password_for_id_1"));
            userWithId1.setRole(userRole);
            userWithId1.setCreatedAt(LocalDateTime.now());
            userWithId1.setUpdatedAt(LocalDateTime.now());
            userRepository.save(userWithId1);
            logger.info("Создан пользователь с ID=1, username='{}', email='{}', роль USER.", id1Username, id1Email);
        }

        Optional<UserEntity> existingUserWithId999 = userRepository.findById(999L);

        if (existingUserWithId999.isPresent()) {
            UserEntity userWithId999 = existingUserWithId999.get();
            logger.info("Пользователь с ID=999 уже существует: {} (роль: {}).", userWithId999.getUsername(), userWithId999.getRole().getName());
            if (!"USER".equals(userWithId999.getRole().getName()) && !"ADMIN".equals(userWithId999.getRole().getName())) {
                userWithId999.setRole(userRole);
                userRepository.save(userWithId999);
                logger.info("Роль пользователя с ID=999 обновлена на USER.");
            }
        } else {
            String id999Username = "user_with_id_999";
            String id999Email = "user_with_id_999@example.com";

            UserEntity userWithId999 = new UserEntity();
            userWithId999.setId(999L);
            userWithId999.setUsername(id999Username);
            userWithId999.setEmail(id999Email);
            userWithId999.setPassword(passwordEncoder.encode("default_password_for_id_999"));
            userWithId999.setRole(userRole);
            userWithId999.setCreatedAt(LocalDateTime.now());
            userWithId999.setUpdatedAt(LocalDateTime.now());
            userRepository.save(userWithId999);
            logger.info("Создан пользователь с ID=999, username='{}', email='{}', роль USER.", id999Username, id999Email);
        }
        initializeInitialFunction();

        logger.info("Инициализация данных завершена");
    }

    private void initializeInitialFunction() {
        Optional<UserEntity> userOpt = userRepository.findByUsername("user1");
        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            Long userId = user.getId();

            if (functionRepository.findByNameAndUserId("initial_func", userId) == null) {
                FunctionEntity initialFunc = new FunctionEntity();
                initialFunc.setName("initial_func");
                initialFunc.setType("BASIC");
                initialFunc.setExpression("x");
                initialFunc.setLeftBound(0.0);
                initialFunc.setRightBound(1.0);
                initialFunc.setPointsCount(100);
                initialFunc.setUser(user);
                initialFunc.setPointsData("{}");
                initialFunc.setCreatedAt(LocalDateTime.now());
                initialFunc.setUpdatedAt(LocalDateTime.now());

                functionRepository.save(initialFunc);
                logger.info("Создана функция: initial_func для пользователя ID=" + userId);
            } else {
                logger.info("Функция 'initial_func' уже существует для пользователя ID=" + userId);
            }
        } else {
            logger.warn("Пользователь user1 не найден, невозможно создать начальную функцию.");
        }
    }
}