package lab5.service;

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

import java.util.*;

@Service
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private OperationRepository operationRepository;

    public Optional<UserEntity> findUserById(Long id) {
        logger.info("Поиск пользователя по ID: {}", id);
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isPresent()) {
            logger.debug("Найден пользователь: ID={}, Username={}",
                    user.get().getId(), user.get().getUsername());
        } else {
            logger.warn("Пользователь с ID {} не найден.", id);
        }
        return user;
    }

    public Optional<FunctionEntity> findFunctionById(Long id) {
        logger.info("Поиск функции по ID: {}", id);
        Optional<FunctionEntity> function = functionRepository.findById(id);
        if (function.isPresent()) {
            logger.debug("Найдена функция: ID={}, Name={}",
                    function.get().getId(), function.get().getName());
        } else {
            logger.warn("Функция с ID {} не найдена.", id);
        }
        return function;
    }

    public Optional<OperationEntity> findOperationById(Long id) {
        logger.info("Поиск операции по ID: {}", id);
        Optional<OperationEntity> operation = operationRepository.findById(id);
        if (operation.isPresent()) {
            logger.debug("Найдена операция: ID={}, Type={}",
                    operation.get().getId(), operation.get().getOperationType());
        } else {
            logger.warn("Операция с ID {} не найдена.", id);
        }
        return operation;
    }

    public List<UserEntity> findAllUsers(Sort sort) {
        logger.info("Поиск всех пользователей с сортировкой: {}", sort);
        List<UserEntity> users = userRepository.findAll(sort);
        logger.debug("Найдено {} пользователей.", users.size());
        return users;
    }

    public List<FunctionEntity> findFunctionsByUserId(Long userId, Sort sort) {
        logger.info("Поиск функций по ID пользователя {} с сортировкой: {}", userId, sort);
        List<FunctionEntity> functions = functionRepository.findByUserId(userId, sort);
        logger.debug("Найдено {} функций для пользователя ID {}.", functions.size(), userId);
        return functions;
    }

    public List<OperationEntity> findOperationsByUserId(Long userId) {
        logger.info("Поиск всех операций для пользователя ID: {}", userId);
        List<OperationEntity> operations = operationRepository.findByUserId(userId);
        logger.debug("Найдено {} операций для пользователя ID {}.", operations.size(), userId);
        return operations;
    }

    public Page<UserEntity> findUsersPaginated(Pageable pageable) {
        logger.info("Поиск пользователей с пагинацией: {}", pageable);
        Page<UserEntity> users = userRepository.findAll(pageable);
        logger.debug("Получена страница пользователей, размер: {}, номер: {}, всего: {}",
                users.getSize(), users.getNumber(), users.getTotalElements());
        return users;
    }

    public List<FunctionEntity> findFunctionsSortedByField(String fieldName, String direction) {
        logger.info("Поиск функций с сортировкой по полю '{}' в направлении '{}'", fieldName, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, fieldName);
        List<FunctionEntity> functions = functionRepository.findAll(sort);
        logger.debug("Найдено {} функций, отсортированных по полю '{}'.", functions.size(), fieldName);
        return functions;
    }

    public Set<FunctionEntity> findDependencyFunctionsDFS(Long resultFunctionId) {
        logger.info("Начало DFS для поиска зависимостей функции ID: {}", resultFunctionId);
        Set<Long> visitedIds = new HashSet<>();
        Set<FunctionEntity> dependencies = new HashSet<>();

        dfsRecursive(resultFunctionId, visitedIds, dependencies);
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

    public Set<FunctionEntity> findDependencyFunctionsBFS(Long startFunctionId, int maxDepth) {
        logger.info("Начало BFS для поиска зависимостей функции ID: {} с глубиной: {}", startFunctionId, maxDepth);
        Queue<Long> queue = new LinkedList<>();
        Set<Long> visitedIds = new HashSet<>();
        Set<FunctionEntity> foundDependencies = new HashSet<>();
        int currentDepth = 0;

        queue.add(startFunctionId);
        visitedIds.add(startFunctionId);

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