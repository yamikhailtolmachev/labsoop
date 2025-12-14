package lab5.service;

import lab5.dto.FunctionDTO;
import lab5.dto.OperationDTO;
import lab5.dto.UserDTO;
import lab5.entity.FunctionEntity;
import lab5.entity.OperationEntity;
import lab5.entity.UserEntity;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
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

    public Optional<FunctionDTO> findFunctionById(Long id) {
        logger.info("Поиск функции по ID: {}", id);
        Optional<FunctionEntity> entityOpt = functionRepository.findById(id);
        if (entityOpt.isPresent()) {
            FunctionEntity entity = entityOpt.get();
            FunctionDTO dto = new FunctionDTO(
                    entity.getId(),
                    entity.getUser().getId(),
                    entity.getName(),
                    entity.getType(),
                    entity.getExpression(),
                    entity.getLeftBound(),
                    entity.getRightBound(),
                    entity.getPointsCount(),
                    entity.getPointsData()
            );
            logger.debug("Найдена функция DTO: ID={}, Name={}", dto.getId(), dto.getName());
            return Optional.of(dto);
        } else {
            logger.warn("Функция с ID {} не найдена.", id);
            return Optional.empty();
        }
    }

    public List<FunctionDTO> findAllFunctions() {
        logger.info("Поиск всех функций (возвращая DTO)");
        List<FunctionEntity> entities = functionRepository.findAll();
        List<FunctionDTO> dtos = entities.stream()
                .map(entity -> new FunctionDTO(
                        entity.getId(),
                        entity.getUser().getId(),
                        entity.getName(),
                        entity.getType(),
                        entity.getExpression(),
                        entity.getLeftBound(),
                        entity.getRightBound(),
                        entity.getPointsCount(),
                        entity.getPointsData()
                ))
                .collect(Collectors.toList());
        logger.debug("Найдено {} функций DTO.", dtos.size());
        return dtos;
    }

    public List<FunctionDTO> findAllFunctionsSorted(String field, String direction) {
        logger.info("Поиск всех функций (возвращая DTO) с сортировкой по полю '{}' в направлении '{}'", field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<FunctionEntity> entities = functionRepository.findAll(sort);
        List<FunctionDTO> dtos = entities.stream()
                .map(entity -> new FunctionDTO(
                        entity.getId(),
                        entity.getUser().getId(),
                        entity.getName(),
                        entity.getType(),
                        entity.getExpression(),
                        entity.getLeftBound(),
                        entity.getRightBound(),
                        entity.getPointsCount(),
                        entity.getPointsData()
                ))
                .collect(Collectors.toList());
        logger.debug("Найдено {} функций DTO, отсортированных по полю '{}'.", dtos.size(), field);
        return dtos;
    }

    public List<FunctionDTO> findFunctionsByUserId(Long userId, Sort sort) {
        logger.info("Поиск функций по ID пользователя {} с сортировкой: {}", userId, sort);
        List<FunctionEntity> entities = functionRepository.findByUserId(userId, sort);
        List<FunctionDTO> dtos = entities.stream()
                .map(entity -> new FunctionDTO(
                        entity.getId(),
                        entity.getUser().getId(),
                        entity.getName(),
                        entity.getType(),
                        entity.getExpression(),
                        entity.getLeftBound(),
                        entity.getRightBound(),
                        entity.getPointsCount(),
                        entity.getPointsData()
                ))
                .collect(Collectors.toList());
        logger.debug("Найдено {} функций DTO для пользователя ID {} с сортировкой.", dtos.size(), userId);
        return dtos;
    }

    public List<FunctionDTO> findFunctionsByUserIdSorted(Long userId, String field, String direction) {
        logger.info("Поиск функций по ID пользователя {} с сортировкой по полю '{}' в направлении '{}'", userId, field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<FunctionEntity> entities = functionRepository.findByUserId(userId, sort);
        logger.debug("Найдено {} функций для пользователя ID {}, отсортированных по полю '{}'.", entities.size(), userId, field);
        List<FunctionDTO> dtos = entities.stream()
                .map(entity -> new FunctionDTO(
                        entity.getId(),
                        entity.getUser().getId(),
                        entity.getName(),
                        entity.getType(),
                        entity.getExpression(),
                        entity.getLeftBound(),
                        entity.getRightBound(),
                        entity.getPointsCount(),
                        entity.getPointsData()
                ))
                .collect(Collectors.toList());
        return dtos;
    }

    @Transactional
    public FunctionDTO createFunction(FunctionDTO functionDTO) {
        try {
            logger.info("=== START createFunction (DTO) ===");
            logger.info("FunctionDTO: name={}, userId={}, type={}, expression={}",
                    functionDTO.getName(), functionDTO.getUserId(), functionDTO.getType(), functionDTO.getExpression());

            if (functionDTO.getUserId() == null) {
                throw new RuntimeException("userId is required");
            }

            UserEntity user = userRepository.findById(functionDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("Пользователь с ID " + functionDTO.getUserId() + " не найден"));

            FunctionEntity function = new FunctionEntity();
            function.setUser(user);
            function.setName(functionDTO.getName());
            function.setType(functionDTO.getType());
            function.setExpression(functionDTO.getExpression());
            function.setLeftBound(functionDTO.getLeftBound());
            function.setRightBound(functionDTO.getRightBound());
            function.setPointsCount(functionDTO.getPointsCount());

            if (functionDTO.getPointsData() != null) {
                function.setPointsData(functionDTO.getPointsData());
            } else {
                function.setPointsData("{}");
            }

            function.setCreatedAt(LocalDateTime.now());
            function.setUpdatedAt(LocalDateTime.now());

            FunctionEntity savedEntity = functionRepository.save(function);
            logger.info("FunctionEntity saved with ID: {}", savedEntity.getId());

            FunctionDTO savedDto = new FunctionDTO(
                    savedEntity.getId(),
                    savedEntity.getUser().getId(),
                    savedEntity.getName(),
                    savedEntity.getType(),
                    savedEntity.getExpression(),
                    savedEntity.getLeftBound(),
                    savedEntity.getRightBound(),
                    savedEntity.getPointsCount(),
                    savedEntity.getPointsData()
            );
            logger.info("FunctionDTO created with ID: {}", savedDto.getId());
            logger.info("=== END createFunction (DTO) ===");

            return savedDto;

        } catch (Exception e) {
            logger.error("ERROR in createFunction (DTO): {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка создания функции DTO: " + e.getMessage(), e);
        }
    }

    @Transactional
    public FunctionDTO updateFunction(Long id, FunctionDTO functionDetails) {
        logger.info("Обновление функции DTO с ID: {}", id);
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
            FunctionEntity updatedEntity = functionRepository.save(existingFunction);
            logger.info("FunctionEntity с ID {} обновлена.", id);

            FunctionDTO updatedDto = new FunctionDTO(
                    updatedEntity.getId(),
                    updatedEntity.getUser().getId(),
                    updatedEntity.getName(),
                    updatedEntity.getType(),
                    updatedEntity.getExpression(),
                    updatedEntity.getLeftBound(),
                    updatedEntity.getRightBound(),
                    updatedEntity.getPointsCount(),
                    updatedEntity.getPointsData()
            );
            logger.info("FunctionDTO с ID {} обновлена.", id);
            return updatedDto;
        } else {
            logger.warn("Функция DTO с ID {} не найдена для обновления.", id);
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

    public Optional<OperationDTO> findOperationById(Long id) {
        logger.info("Поиск операции по ID: {}", id);
        Optional<OperationEntity> entityOpt = operationRepository.findById(id);
        if (entityOpt.isPresent()) {
            OperationEntity entity = entityOpt.get();
            OperationDTO dto = new OperationDTO(
                    entity.getId(),
                    entity.getUser().getId(),
                    entity.getFunction1().getId(),
                    entity.getFunction2().getId(),
                    entity.getResultFunction().getId(),
                    entity.getOperationType(),
                    entity.getParameters(),
                    entity.getComputedAt(),
                    entity.getUpdatedAt()
            );
            logger.debug("Найдена операция DTO: ID={}, Type={}", dto.getId(), dto.getOperationType());
            return Optional.of(dto);
        } else {
            logger.warn("Операция с ID {} не найдена.", id);
            return Optional.empty();
        }
    }

    public List<OperationDTO> findAllOperations() {
        logger.info("Поиск всех операций (возвращая DTO)");
        List<OperationEntity> entities = operationRepository.findAll();
        List<OperationDTO> dtos = entities.stream()
                .map(entity -> new OperationDTO(
                        entity.getId(),
                        entity.getUser().getId(),
                        entity.getFunction1().getId(),
                        entity.getFunction2().getId(),
                        entity.getResultFunction().getId(),
                        entity.getOperationType(),
                        entity.getParameters(),
                        entity.getComputedAt(),
                        entity.getUpdatedAt()
                ))
                .collect(Collectors.toList());
        logger.debug("Найдено {} операций DTO.", dtos.size());
        return dtos;
    }

    public List<OperationDTO> findAllOperationsSorted(String field, String direction) {
        logger.info("Поиск всех операций (возвращая DTO) с сортировкой по полю '{}' в направлении '{}'", field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<OperationEntity> entities = operationRepository.findAll(sort);
        List<OperationDTO> dtos = entities.stream()
                .map(entity -> new OperationDTO(
                        entity.getId(),
                        entity.getUser().getId(),
                        entity.getFunction1().getId(),
                        entity.getFunction2().getId(),
                        entity.getResultFunction().getId(),
                        entity.getOperationType(),
                        entity.getParameters(),
                        entity.getComputedAt(),
                        entity.getUpdatedAt()
                ))
                .collect(Collectors.toList());
        logger.debug("Найдено {} операций DTO, отсортированных по полю '{}'.", dtos.size(), field);
        return dtos;
    }

    public List<OperationEntity> findOperationsByUserId(Long userId) {
        logger.info("Поиск операций по ID пользователя: {}", userId);
        List<OperationEntity> operations = operationRepository.findByUser_Id(userId);
        logger.debug("Найдено {} операций для пользователя ID {}.", operations.size(), userId);
        return operations;
    }

    public List<OperationDTO> findOperationsByUserIdSorted(Long userId, String field, String direction) {
        logger.info("Поиск операций по ID пользователя {} с сортировкой по полю '{}' в направлении '{}'", userId, field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<OperationEntity> entities = operationRepository.findByUser_Id(userId, sort);
        logger.debug("Найдено {} операций для пользователя ID {}, отсортированных по полю '{}'.", entities.size(), userId, field);
        List<OperationDTO> dtos = entities.stream()
                .map(entity -> new OperationDTO(
                        entity.getId(),
                        entity.getUser().getId(),
                        entity.getFunction1().getId(),
                        entity.getFunction2().getId(),
                        entity.getResultFunction().getId(),
                        entity.getOperationType(),
                        entity.getParameters(),
                        entity.getComputedAt(),
                        entity.getUpdatedAt()
                ))
                .collect(Collectors.toList());
        return dtos;
    }

    @Transactional
    public OperationDTO createOperation(OperationDTO operationDTO) {
        try {
            logger.info("=== START createOperation (DTO) ===");
            logger.info("OperationDTO: type={}, userId={}", operationDTO.getOperationType(), operationDTO.getUserId());

            UserEntity user = userRepository.findById(operationDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("Пользователь с ID " + operationDTO.getUserId() + " не найден"));

            FunctionEntity func1 = functionRepository.findById(operationDTO.getFunction1Id())
                    .orElseThrow(() -> new RuntimeException("Функция 1 с ID " + operationDTO.getFunction1Id() + " не найдена"));
            FunctionEntity func2 = null;
            if (operationDTO.getFunction2Id() != null) {
                func2 = functionRepository.findById(operationDTO.getFunction2Id())
                        .orElseThrow(() -> new RuntimeException("Функция 2 с ID " + operationDTO.getFunction2Id() + " не найдена"));
            }
            FunctionEntity resultFunc = functionRepository.findById(operationDTO.getResultFunctionId())
                    .orElseThrow(() -> new RuntimeException("Результирующая функция с ID " + operationDTO.getResultFunctionId() + " не найдена"));

            OperationEntity operation = new OperationEntity();
            operation.setUser(user);
            operation.setFunction1(func1);
            operation.setFunction2(func2);
            operation.setResultFunction(resultFunc);
            operation.setOperationType(operationDTO.getOperationType());
            operation.setParameters(operationDTO.getParameters());
            operation.setComputedAt(LocalDateTime.now());
            operation.setUpdatedAt(LocalDateTime.now());

            OperationEntity savedEntity = operationRepository.save(operation);
            logger.info("OperationEntity saved with ID: {}", savedEntity.getId());

            OperationDTO savedDto = new OperationDTO(
                    savedEntity.getId(),
                    savedEntity.getUser().getId(),
                    savedEntity.getFunction1().getId(),
                    savedEntity.getFunction2().getId(),
                    savedEntity.getResultFunction().getId(),
                    savedEntity.getOperationType(),
                    savedEntity.getParameters(),
                    savedEntity.getComputedAt(),
                    savedEntity.getUpdatedAt()
            );
            logger.info("OperationDTO created with ID: {}", savedDto.getId());
            logger.info("=== END createOperation (DTO) ===");

            return savedDto;

        } catch (Exception e) {
            logger.error("ERROR in createOperation (DTO): {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка создания операции DTO: " + e.getMessage(), e);
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

    public List<UserEntity> findAllUsers(Sort sort) {
        return null;
    }
}