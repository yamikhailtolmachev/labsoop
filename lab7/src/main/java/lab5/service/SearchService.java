package lab5.service;

import lab5.dto.FunctionDTO;
import lab5.dto.OperationDTO;
import lab5.entity.FunctionEntity;
import lab5.entity.OperationEntity;
import lab5.entity.UserEntity;
import lab5.repository.FunctionRepository;
import lab5.repository.OperationRepository;
import lab5.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<UserEntity> findAllUsers(String sortField, String sortDirection) {
        logger.info("Поиск всех пользователей с сортировкой по полю '{}' в направлении '{}'", sortField, sortDirection);
        if (sortField == null || sortField.trim().isEmpty()) {
            sortField = "id";
        }
        if (sortDirection == null || sortDirection.trim().isEmpty()) {
            sortDirection = "asc";
        }

        Sort.Direction dir = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(dir, sortField);
        List<UserEntity> users = userRepository.findAll(sort);
        logger.debug("Найдено {} пользователей.", users.size());
        return users;
    }

    @Transactional(readOnly = true)
    public List<FunctionDTO> findFunctionsByUserIdSorted(Long userId, String field, String direction) {
        logger.info("Поиск функций по ID пользователя {} с сортировкой по полю '{}' в направлении '{}'",
                userId, field, direction);
        if (field == null || field.trim().isEmpty()) {
            field = "id";
        }
        if (direction == null || direction.trim().isEmpty()) {
            direction = "asc";
        }

        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<FunctionEntity> entities = functionRepository.findByUserId(userId, sort);
        List<FunctionDTO> dtos = entities.stream()
                .map(entity -> new FunctionDTO(
                        entity.getId(),
                        entity.getUser() != null ? entity.getUser().getId() : null,
                        entity.getName(),
                        entity.getType(),
                        entity.getExpression(),
                        entity.getLeftBound(),
                        entity.getRightBound(),
                        entity.getPointsCount(),
                        entity.getPointsData()
                ))
                .collect(Collectors.toList());
        logger.debug("Найдено {} функций для пользователя ID {}", dtos.size(), userId);
        return dtos;
    }

    @Transactional(readOnly = true)
    public List<FunctionDTO> findAllFunctionsSorted(String field, String direction) {
        logger.info("Поиск всех функций с сортировкой по полю '{}' в направлении '{}'", field, direction);
        if (field == null || field.trim().isEmpty()) {
            field = "id";
        }
        if (direction == null || direction.trim().isEmpty()) {
            direction = "asc";
        }

        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<FunctionEntity> entities = functionRepository.findAll(sort);
        List<FunctionDTO> dtos = entities.stream()
                .map(entity -> new FunctionDTO(
                        entity.getId(),
                        entity.getUser() != null ? entity.getUser().getId() : null,
                        entity.getName(),
                        entity.getType(),
                        entity.getExpression(),
                        entity.getLeftBound(),
                        entity.getRightBound(),
                        entity.getPointsCount(),
                        entity.getPointsData()
                ))
                .collect(Collectors.toList());
        logger.debug("Найдено {} функций", dtos.size());
        return dtos;
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

        List<OperationEntity> operations = operationRepository.findByResultFunction_Id(functionId);
        for (OperationEntity op : operations) {
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
                    logger.warn("Функция ID {} не найдена во время BFS.", currentId);
                    continue;
                }

                FunctionEntity currentFunc = currentFuncOpt.get();
                foundDependencies.add(currentFunc);

                if (currentDepth == maxDepth) {
                    continue;
                }

                List<OperationEntity> operations = operationRepository.findByResultFunction_Id(currentId);
                for (OperationEntity op : operations) {
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

        logger.info("BFS завершён. Найдено {} зависимых функций.", foundDependencies.size());
        return foundDependencies;
    }

    @Transactional(readOnly = true)
    public Optional<FunctionDTO> findFunctionById(Long id) {
        logger.info("Поиск функции по ID: {}", id);
        Optional<FunctionEntity> entityOpt = functionRepository.findById(id);
        if (entityOpt.isPresent()) {
            FunctionEntity entity = entityOpt.get();
            FunctionDTO dto = new FunctionDTO(
                    entity.getId(),
                    entity.getUser() != null ? entity.getUser().getId() : null,
                    entity.getName(),
                    entity.getType(),
                    entity.getExpression(),
                    entity.getLeftBound(),
                    entity.getRightBound(),
                    entity.getPointsCount(),
                    entity.getPointsData()
            );
            logger.debug("Найдена функция: ID={}, Name={}", dto.getId(), dto.getName());
            return Optional.of(dto);
        } else {
            logger.warn("Функция с ID {} не найдена.", id);
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public Optional<OperationDTO> findOperationById(Long id) {
        logger.info("Поиск операции по ID: {}", id);
        Optional<OperationEntity> entityOpt = operationRepository.findById(id);

        if (entityOpt.isPresent()) {
            OperationEntity entity = entityOpt.get();
            OperationDTO dto = new OperationDTO(
                    entity.getId(),
                    entity.getUser() != null ? entity.getUser().getId() : null,
                    entity.getFunction1() != null ? entity.getFunction1().getId() : null,
                    entity.getFunction2() != null ? entity.getFunction2().getId() : null,
                    entity.getResultFunction() != null ? entity.getResultFunction().getId() : null,
                    entity.getOperationType(),
                    entity.getParameters(),
                    entity.getComputedAt(),
                    entity.getUpdatedAt()
            );
            logger.debug("Найдена операция: ID={}, Type={}", dto.getId(), dto.getOperationType());
            return Optional.of(dto);
        } else {
            logger.warn("Операция с ID {} не найдена.", id);
            return Optional.empty();
        }
    }

    @Transactional(readOnly = true)
    public List<OperationDTO> findOperationsByUserIdSorted(Long userId, String field, String direction) {
        logger.info("Поиск операций по ID пользователя {} с сортировкой по полю '{}' в направлении '{}'",
                userId, field, direction);

        if (field == null || field.trim().isEmpty()) {
            field = "id";
        }
        if (direction == null || direction.trim().isEmpty()) {
            direction = "asc";
        }

        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
        List<OperationEntity> entities = operationRepository.findByUser_Id(userId, sort);

        List<OperationDTO> dtos = entities.stream()
                .map(entity -> new OperationDTO(
                        entity.getId(),
                        entity.getUser() != null ? entity.getUser().getId() : null,
                        entity.getFunction1() != null ? entity.getFunction1().getId() : null,
                        entity.getFunction2() != null ? entity.getFunction2().getId() : null,
                        entity.getResultFunction() != null ? entity.getResultFunction().getId() : null,
                        entity.getOperationType(),
                        entity.getParameters(),
                        entity.getComputedAt(),
                        entity.getUpdatedAt()
                ))
                .collect(Collectors.toList());

        logger.debug("Найдено {} операций для пользователя ID {}", dtos.size(), userId);
        return dtos;
    }

    @Transactional
    public FunctionDTO createFunction(FunctionDTO functionDTO) {
        logger.info("Создание функции: {}", functionDTO.getName());

        if (functionDTO.getUserId() == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        UserEntity user = userRepository.findById(functionDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + functionDTO.getUserId()));

        FunctionEntity function = new FunctionEntity();
        function.setUser(user);
        function.setName(functionDTO.getName());
        function.setType(functionDTO.getType());
        function.setExpression(functionDTO.getExpression());
        function.setLeftBound(functionDTO.getLeftBound());
        function.setRightBound(functionDTO.getRightBound());
        function.setPointsCount(functionDTO.getPointsCount());
        function.setPointsData(functionDTO.getPointsData() != null ? functionDTO.getPointsData() : "{}");
        function.setCreatedAt(LocalDateTime.now());
        function.setUpdatedAt(LocalDateTime.now());

        FunctionEntity saved = functionRepository.save(function);

        return new FunctionDTO(
                saved.getId(),
                saved.getUser().getId(),
                saved.getName(),
                saved.getType(),
                saved.getExpression(),
                saved.getLeftBound(),
                saved.getRightBound(),
                saved.getPointsCount(),
                saved.getPointsData()
        );
    }

    @Transactional
    public FunctionDTO updateFunction(Long id, FunctionDTO functionDetails) {
        logger.info("Обновление функции с ID: {}", id);

        Optional<FunctionEntity> existingOpt = functionRepository.findById(id);
        if (existingOpt.isEmpty()) {
            logger.warn("Функция с ID {} не найдена для обновления", id);
            return null;
        }

        FunctionEntity existing = existingOpt.get();

        if (functionDetails.getName() != null) {
            existing.setName(functionDetails.getName());
        }
        if (functionDetails.getType() != null) {
            existing.setType(functionDetails.getType());
        }
        if (functionDetails.getExpression() != null) {
            existing.setExpression(functionDetails.getExpression());
        }
        if (functionDetails.getLeftBound() != null) {
            existing.setLeftBound(functionDetails.getLeftBound());
        }
        if (functionDetails.getRightBound() != null) {
            existing.setRightBound(functionDetails.getRightBound());
        }
        if (functionDetails.getPointsCount() != null) {
            existing.setPointsCount(functionDetails.getPointsCount());
        }
        if (functionDetails.getPointsData() != null) {
            existing.setPointsData(functionDetails.getPointsData());
        }

        existing.setUpdatedAt(LocalDateTime.now());

        FunctionEntity updated = functionRepository.save(existing);

        return new FunctionDTO(
                updated.getId(),
                updated.getUser() != null ? updated.getUser().getId() : null,
                updated.getName(),
                updated.getType(),
                updated.getExpression(),
                updated.getLeftBound(),
                updated.getRightBound(),
                updated.getPointsCount(),
                updated.getPointsData()
        );
    }

    @Transactional
    public boolean deleteFunctionById(Long id) {
        if (functionRepository.existsById(id)) {
            functionRepository.deleteById(id);
            logger.info("Функция с ID {} удалена", id);
            return true;
        }
        logger.warn("Функция с ID {} не найдена", id);
        return false;
    }

    @Transactional
    public OperationDTO createOperation(OperationDTO operationDTO) {
        logger.info("Создание операции: {}", operationDTO.getOperationType());

        UserEntity user = userRepository.findById(operationDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found: " + operationDTO.getUserId()));

        FunctionEntity func1 = functionRepository.findById(operationDTO.getFunction1Id())
                .orElseThrow(() -> new RuntimeException("Function1 not found: " + operationDTO.getFunction1Id()));

        FunctionEntity func2 = operationDTO.getFunction2Id() != null ?
                functionRepository.findById(operationDTO.getFunction2Id())
                        .orElseThrow(() -> new RuntimeException("Function2 not found: " + operationDTO.getFunction2Id())) :
                null;

        FunctionEntity resultFunc = functionRepository.findById(operationDTO.getResultFunctionId())
                .orElseThrow(() -> new RuntimeException("Result function not found: " + operationDTO.getResultFunctionId()));

        OperationEntity operation = new OperationEntity();
        operation.setUser(user);
        operation.setFunction1(func1);
        operation.setFunction2(func2);
        operation.setResultFunction(resultFunc);
        operation.setOperationType(operationDTO.getOperationType());
        operation.setParameters(operationDTO.getParameters() != null ? operationDTO.getParameters() : "{}");
        operation.setComputedAt(LocalDateTime.now());
        operation.setUpdatedAt(LocalDateTime.now());

        OperationEntity saved = operationRepository.save(operation);

        return new OperationDTO(
                saved.getId(),
                saved.getUser() != null ? saved.getUser().getId() : null,
                saved.getFunction1() != null ? saved.getFunction1().getId() : null,
                saved.getFunction2() != null ? saved.getFunction2().getId() : null,
                saved.getResultFunction() != null ? saved.getResultFunction().getId() : null,
                saved.getOperationType(),
                saved.getParameters(),
                saved.getComputedAt(),
                saved.getUpdatedAt()
        );
    }

    @Transactional
    public boolean deleteOperationById(Long id) {
        if (operationRepository.existsById(id)) {
            operationRepository.deleteById(id);
            logger.info("Операция с ID {} удалена", id);
            return true;
        }
        logger.warn("Операция с ID {} не найдена", id);
        return false;
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findUserByUsername(String username) {
        logger.info("Поиск пользователя по имени: {}", username);
        return userRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findUserByEmail(String email) {
        logger.info("Поиск пользователя по email: {}", email);
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserEntity createUser(String username, String email, String passwordHash) {
        logger.info("Создание пользователя: {}", username);
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordHash);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public boolean deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            logger.info("Пользователь с ID {} удалён", id);
            return true;
        }
        logger.warn("Пользователь с ID {} не найден", id);
        return false;
    }
}