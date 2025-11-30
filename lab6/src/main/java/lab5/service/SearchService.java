package lab5.service;

import lab5.entity.ComputationCacheEntity;
import lab5.entity.FunctionEntity;
import lab5.entity.OperationEntity;
import lab5.entity.UserEntity;
import lab5.repository.ComputationCacheRepository;
import lab5.repository.FunctionRepository;
import lab5.repository.OperationRepository;
import lab5.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private lab5.repository.UserRepository userRepository;

    @Autowired
    private lab5.repository.FunctionRepository functionRepository;

    @Autowired
    private lab5.repository.OperationRepository operationRepository;

    @Autowired
    private lab5.repository.ComputationCacheRepository cacheRepository;

    public Optional<UserEntity> findUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.debug("Найден пользователь: ID={}, Username={}", user.get().getId(), user.get().getUsername());
        } else {
            logger.warn("Пользователь с ID {} не найден.", id);
        }
        return user;
    }

    public List<UserEntity> findAllUsers() {
        logger.info("Поиск всех пользователей");
        List<UserEntity> users = userRepository.findAll();
        logger.debug("Найдено {} пользователей.", users.size());
        return users;
    }

    public List<UserEntity> findAllUsersSorted(String field, String direction) {
        logger.info("Поиск всех пользователей с сортировкой по полю '{}' в направлении '{}'", field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<UserEntity> users = userRepository.findAll(sort);
        logger.debug("Найдено {} пользователей, отсортированных по полю '{}'.", users.size(), field);
        return users;
    }

    public Optional<UserEntity> findUserByUsername(String username) {
        logger.info("Поиск пользователя по имени: {}", username);
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            logger.debug("Найден пользователь по имени: {}", username);
        } else {
            logger.warn("Пользователь с именем {} не найден.", username);
        }
        return user;
    }

    public Optional<UserEntity> findUserByEmail(String email) {
        logger.info("Поиск пользователя по email: {}", email);
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            logger.debug("Найден пользователь по email: {}", email);
        } else {
            logger.warn("Пользователь с email {} не найден.", email);
        }
        return user;
    }

    public UserEntity createUser(String username, String email, String passwordHash) {
        logger.info("Создание нового пользователя: username='{}', email='{}'", username, email);
        UserEntity user = new UserEntity(username, email, passwordHash);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        UserEntity saved = userRepository.save(user);
        logger.info("Пользователь создан с ID: {}", saved.getId());
        return saved;
    }

    public UserEntity updateUser(Long id, UserEntity userDetails) {
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
            if (userDetails.getPasswordHash() != null) {
                existingUser.setPasswordHash(userDetails.getPasswordHash());
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

    public boolean deleteUserById(Long id) {
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

    public Optional<FunctionEntity> findFunctionById(Long id) {
        logger.info("Поиск функции по ID: {}", id);
        Optional<FunctionEntity> function = functionRepository.findById(id);
        if (function.isPresent()) {
            logger.debug("Найдена функция: ID={}, Name={}", function.get().getId(), function.get().getName());
        } else {
            logger.warn("Функция с ID {} не найдена.", id);
        }
        return function;
    }

    public List<FunctionEntity> findAllFunctions() {
        logger.info("Поиск всех функций");
        List<FunctionEntity> functions = functionRepository.findAll();
        logger.debug("Найдено {} функций.", functions.size());
        return functions;
    }

    public List<FunctionEntity> findAllFunctionsSorted(String field, String direction) {
        logger.info("Поиск всех функций с сортировкой по полю '{}' в направлении '{}'", field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<FunctionEntity> functions = functionRepository.findAll(sort);
        logger.debug("Найдено {} функций, отсортированных по полю '{}'.", functions.size(), field);
        return functions;
    }

    public List<FunctionEntity> findFunctionsByUserId(Long userId, Sort sort) {
        logger.info("Поиск функций по ID пользователя {} с сортировкой: {}", userId, sort);
        List<FunctionEntity> functions = functionRepository.findByUserId(userId, sort);
        logger.debug("Найдено {} функций для пользователя ID {} с сортировкой.", functions.size(), userId);
        return functions;
    }

    public List<FunctionEntity> findFunctionsByUserIdSorted(Long userId, String field, String direction) {
        logger.info("Поиск функций по ID пользователя {} с сортировкой по полю '{}' в направлении '{}'", userId, field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<FunctionEntity> functions = functionRepository.findByUserId(userId, sort);
        logger.debug("Найдено {} функций для пользователя ID {}, отсортированных по полю '{}'.", functions.size(), userId, field);
        return functions;
    }

    public FunctionEntity createFunction(FunctionEntity function) {
        logger.info("Создание новой функции: name='{}', userId='{}'", function.getName(), function.getUser().getId());
        function.setCreatedAt(LocalDateTime.now());
        function.setUpdatedAt(LocalDateTime.now());
        FunctionEntity saved = functionRepository.save(function);
        logger.info("Функция создана с ID: {}", saved.getId());
        return saved;
    }

    public FunctionEntity updateFunction(Long id, FunctionEntity functionDetails) {
        logger.info("Обновление функции с ID: {}", id);
        Optional<FunctionEntity> existingFunctionOpt = functionRepository.findById(id);
        if (existingFunctionOpt.isPresent()) {
            FunctionEntity existingFunction = existingFunctionOpt.get();
            if (functionDetails.getName() != null) {
                existingFunction.setName(functionDetails.getName());
            }
            if (functionDetails.getType() != null) {
                existingFunction.setType(functionDetails.getType());
            }
            if (functionDetails.getExpression() != null) {
                existingFunction.setExpression(functionDetails.getExpression());
            }
            if (functionDetails.getLeftBound() != null) {
                existingFunction.setLeftBound(functionDetails.getLeftBound());
            }
            if (functionDetails.getRightBound() != null) {
                existingFunction.setRightBound(functionDetails.getRightBound());
            }
            if (functionDetails.getPointsCount() != null) {
                existingFunction.setPointsCount(functionDetails.getPointsCount());
            }
            if (functionDetails.getPointsData() != null) {
                existingFunction.setPointsData(functionDetails.getPointsData());
            }
            existingFunction.setUpdatedAt(LocalDateTime.now());
            FunctionEntity updated = functionRepository.save(existingFunction);
            logger.info("Функция с ID {} обновлена.", id);
            return updated;
        } else {
            logger.warn("Функция с ID {} не найдена для обновления.", id);
            return null;
        }
    }

    public boolean deleteFunctionById(Long id) {
        logger.info("Удаление функции с ID: {}", id);
        if (functionRepository.existsById(id)) {
            functionRepository.deleteById(id);
            logger.info("Функция с ID {} удалена.", id);
            return true;
        } else {
            logger.warn("Функция с ID {} не найдена для удаления.", id);
            return false;
        }
    }

    public Optional<OperationEntity> findOperationById(Long id) {
        logger.info("Поиск операции по ID: {}", id);
        Optional<OperationEntity> operation = operationRepository.findById(id);
        if (operation.isPresent()) {
            logger.debug("Найдена операция: ID={}, Type={}", operation.get().getId(), operation.get().getOperationType());
        } else {
            logger.warn("Операция с ID {} не найдена.", id);
        }
        return operation;
    }

    public List<OperationEntity> findAllOperations() {
        logger.info("Поиск всех операций");
        List<OperationEntity> operations = operationRepository.findAll();
        logger.debug("Найдено {} операций.", operations.size());
        return operations;
    }

    public List<OperationEntity> findAllOperationsSorted(String field, String direction) {
        logger.info("Поиск всех операций с сортировкой по полю '{}' в направлении '{}'", field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<OperationEntity> operations = operationRepository.findAll(sort);
        logger.debug("Найдено {} операций, отсортированных по полю '{}'.", operations.size(), field);
        return operations;
    }

    public List<OperationEntity> findOperationsByUserId(Long userId) {
        logger.info("Поиск операций по ID пользователя: {}", userId);
        List<OperationEntity> operations = operationRepository.findByUserId(userId);
        logger.debug("Найдено {} операций для пользователя ID {}.", operations.size(), userId);
        return operations;
    }

    public List<OperationEntity> findOperationsByUserIdSorted(Long userId, String field, String direction) {
        logger.info("Поиск операций по ID пользователя {} с сортировкой по полю '{}' в направлении '{}'", userId, field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<OperationEntity> operations = operationRepository.findByUserId(userId, sort);
        logger.debug("Найдено {} операций для пользователя ID {}, отсортированных по полю '{}'.", operations.size(), userId, field);
        return operations;
    }

    public OperationEntity createOperation(OperationEntity operation) {
        logger.info("Создание новой операции: type='{}', userId='{}'", operation.getOperationType(), operation.getUser().getId());
        operation.setComputedAt(LocalDateTime.now());
        operation.setUpdatedAt(LocalDateTime.now());
        OperationEntity saved = operationRepository.save(operation);
        logger.info("Операция создана с ID: {}", saved.getId());
        return saved;
    }

    public OperationEntity updateOperation(Long id, OperationEntity operationDetails) {
        logger.info("Обновление операции с ID: {}", id);
        Optional<OperationEntity> existingOperationOpt = operationRepository.findById(id);
        if (existingOperationOpt.isPresent()) {
            OperationEntity existingOperation = existingOperationOpt.get();
            if (operationDetails.getOperationType() != null) {
                existingOperation.setOperationType(operationDetails.getOperationType());
            }
            if (operationDetails.getParameters() != null) {
                existingOperation.setParameters(operationDetails.getParameters());
            }
            existingOperation.setUpdatedAt(LocalDateTime.now());
            OperationEntity updated = operationRepository.save(existingOperation);
            logger.info("Операция с ID {} обновлена.", id);
            return updated;
        } else {
            logger.warn("Операция с ID {} не найдена для обновления.", id);
            return null;
        }
    }

    public boolean deleteOperationById(Long id) {
        logger.info("Удаление операции с ID: {}", id);
        if (operationRepository.existsById(id)) {
            operationRepository.deleteById(id);
            logger.info("Операция с ID {} удалена.", id);
            return true;
        } else {
            logger.warn("Операция с ID {} не найдена для удаления.", id);
            return false;
        }
    }

    public Optional<ComputationCacheEntity> findComputationCacheById(Long id) {
        logger.info("Поиск записи кэша по ID: {}", id);
        Optional<ComputationCacheEntity> cache = cacheRepository.findById(id);
        if (cache.isPresent()) {
            logger.debug("Найдена запись кэша: ID={}", cache.get().getId());
        } else {
            logger.warn("Запись кэша с ID {} не найдена.", id);
        }
        return cache;
    }

    public List<ComputationCacheEntity> findAllComputationCaches() {
        logger.info("Поиск всех записей кэша");
        List<ComputationCacheEntity> caches = cacheRepository.findAll();
        logger.debug("Найдено {} записей кэша.", caches.size());
        return caches;
    }

    public List<ComputationCacheEntity> findAllComputationCachesSorted(String field, String direction) {
        logger.info("Поиск всех записей кэша с сортировкой по полю '{}' в направлении '{}'", field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<ComputationCacheEntity> caches = cacheRepository.findAll(sort);
        logger.debug("Найдено {} записей кэша, отсортированных по полю '{}'.", caches.size(), field);
        return caches;
    }

    public Optional<ComputationCacheEntity> findComputationCacheByCacheKey(String cacheKey) {
        logger.info("Поиск записи кэша по ключу: {}", cacheKey);
        Optional<ComputationCacheEntity> cache = cacheRepository.findByCacheKey(cacheKey);
        if (cache.isPresent()) {
            logger.debug("Найдена запись кэша по ключу: {}", cacheKey);
        } else {
            logger.warn("Запись кэша с ключом {} не найдена.", cacheKey);
        }
        return cache;
    }

    public ComputationCacheEntity createComputationCache(ComputationCacheEntity cache) {
        logger.info("Создание новой записи кэша: key='{}', userId='{}'", cache.getCacheKey(), cache.getUser().getId());
        cache.setComputedAt(LocalDateTime.now());
        cache.setUpdatedAt(LocalDateTime.now());
        ComputationCacheEntity saved = cacheRepository.save(cache);
        logger.info("Запись кэша создана с ID: {}", saved.getId());
        return saved;
    }

    public ComputationCacheEntity updateComputationCache(Long id, ComputationCacheEntity cacheDetails) {
        logger.info("Обновление записи кэша с ID: {}", id);
        Optional<ComputationCacheEntity> existingCacheOpt = cacheRepository.findById(id);
        if (existingCacheOpt.isPresent()) {
            ComputationCacheEntity existingCache = existingCacheOpt.get();
            if (cacheDetails.getCacheKey() != null) {
                existingCache.setCacheKey(cacheDetails.getCacheKey());
            }
            if (cacheDetails.getFunctionExpression() != null) {
                existingCache.setFunctionExpression(cacheDetails.getFunctionExpression());
            }
            if (cacheDetails.getLeftBound() != null) {
                existingCache.setLeftBound(cacheDetails.getLeftBound());
            }
            if (cacheDetails.getRightBound() != null) {
                existingCache.setRightBound(cacheDetails.getRightBound());
            }
            if (cacheDetails.getPointsCount() != null) {
                existingCache.setPointsCount(cacheDetails.getPointsCount());
            }
            existingCache.setUpdatedAt(LocalDateTime.now());
            ComputationCacheEntity updated = cacheRepository.save(existingCache);
            logger.info("Запись кэша с ID {} обновлена.", id);
            return updated;
        } else {
            logger.warn("Запись кэша с ID {} не найдена для обновления.", id);
            return null;
        }
    }

    public boolean deleteComputationCacheById(Long id) {
        logger.info("Удаление записи кэша с ID: {}", id);
        if (cacheRepository.existsById(id)) {
            cacheRepository.deleteById(id);
            logger.info("Запись кэша с ID {} удалена.", id);
            return true;
        } else {
            logger.warn("Запись кэша с ID {} не найдена для удаления.", id);
            return false;
        }
    }

    public List<UserEntity> findAllUsers(Sort sort) { // <-- НОВЫЙ МЕТОД, принимает Sort
        logger.info("Поиск всех пользователей с сортировкой: {}", sort);
        List<UserEntity> users = userRepository.findAll(sort); // <-- Вызов метода репозитория с Sort
        logger.debug("Найдено {} пользователей, отсортированных по: {}", users.size(), sort);
        return users;
    }

    public Page<UserEntity> findUsersPaginated(Pageable pageable) {
        logger.info("Поиск пользователей с пагинацией: {}", pageable);
        Page<UserEntity> users = userRepository.findAll(pageable); // Вызов метода репозитория с Pageable
        logger.debug("Получена страница пользователей, размер: {}, номер: {}, всего: {}", users.getSize(), users.getNumber(), users.getTotalElements());
        return users;
    }

    public List<FunctionEntity> findFunctionsSortedByField(String field, String direction) {
        logger.info("Поиск функций с сортировкой по полю '{}' в направлении '{}'", field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<FunctionEntity> functions = functionRepository.findAll(sort);
        logger.debug("Найдено {} функций, отсортированных по полю '{}'.", functions.size(), field);
        return functions;
    }

    public Set<FunctionEntity> findDependencyFunctionsDFS(Long resultFuncId) {
        logger.info("Начало DFS для поиска зависимостей функции ID: {}", resultFuncId);
        Set<Long> visitedIds = new HashSet<>();
        Set<FunctionEntity> dependencies = new HashSet<>();

        dfsRecursive(resultFuncId, visitedIds, dependencies);
        logger.info("DFS завершён. Найдено {} зависимых функций.", dependencies.size());
        return dependencies;
    }

    private void dfsRecursive(Long functionId, Set<Long> visitedIds, Set<FunctionEntity> dependencies) {
        if (!visitedIds.add(functionId)) {
            logger.debug("Функция ID {} уже посещена в DFS, пропускаем.", functionId);
            return;
        }

        Optional<FunctionEntity> currentFuncOpt = functionRepository.findById(functionId);
        if (currentFuncOpt.isEmpty()) {
            logger.warn("Функция ID {} не найдена во время DFS.", functionId);
            return;
        }
        FunctionEntity currentFunc = currentFuncOpt.get();
        dependencies.add(currentFunc);
        logger.debug("DFS: Обработана функция ID {}", functionId);

        List<OperationEntity> operationsResultingInCurrent = operationRepository.findByResultFunction_Id(functionId);

        for (OperationEntity op : operationsResultingInCurrent) {
            if (op.getFunction1Id() != null) {
                dfsRecursive(op.getFunction1Id(), visitedIds, dependencies);
            }
            if (op.getFunction2Id() != null) {
                dfsRecursive(op.getFunction2Id(), visitedIds, dependencies);
            }
        }
    }

    public Set<FunctionEntity> findDependencyFunctionsBFS(Long startFuncId, int maxDepth) {
        logger.info("Начало BFS для поиска зависимостей функции ID: {} с глубиной: {}", startFuncId, maxDepth);
        Queue<Long> queue = new LinkedList<>();
        Set<Long> visitedIds = new HashSet<>();
        Set<FunctionEntity> foundDependencies = new HashSet<>();
        int currentDepth = 0;

        queue.add(startFuncId);
        visitedIds.add(startFuncId);

        while (!queue.isEmpty() && currentDepth <= maxDepth) {
            int levelSize = queue.size();
            logger.debug("BFS: Обработка уровня {}", currentDepth);

            for (int i = 0; i < levelSize; i++) {
                Long currentId = queue.poll();

                Optional<FunctionEntity> currentFuncOpt = functionRepository.findById(currentId);
                if (currentFuncOpt.isEmpty()) {
                    logger.warn("Функция ID {} не найдена во время BFS на уровне {}.", currentId, currentDepth);
                    continue;
                }
                FunctionEntity currentFunc = currentFuncOpt.get();
                foundDependencies.add(currentFunc);

                if (currentDepth == maxDepth) {
                    continue;
                }

                List<OperationEntity> operationsResultingInCurrent = operationRepository.findByResultFunction_Id(currentId);

                for (OperationEntity op : operationsResultingInCurrent) {
                    if (op.getFunction1Id() != null && !visitedIds.contains(op.getFunction1Id())) {
                        visitedIds.add(op.getFunction1Id());
                        queue.add(op.getFunction1Id());
                    }
                    if (op.getFunction2Id() != null && !visitedIds.contains(op.getFunction2Id())) {
                        visitedIds.add(op.getFunction2Id());
                        queue.add(op.getFunction2Id());
                    }
                }
            }
            currentDepth++;
        }
        logger.info("BFS завершён. Найдено {} зависимых функций до глубины {}.", foundDependencies.size(), maxDepth);
        return foundDependencies;
    }
}