package lab5.service;

import lab5.dto.FunctionDTO;
import lab5.entity.FunctionEntity;
import lab5.entity.OperationEntity;
import lab5.entity.UserEntity;
import lab5.repository.FunctionRepository;
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
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;

@Service
@Transactional
public class FunctionService {

    private static final Logger logger = LoggerFactory.getLogger(FunctionService.class);

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private lab5.repository.OperationRepository operationRepository;

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
        logger.info("Поиск всех функций (без сортировки)");
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
                .toList();
        logger.debug("Найдено {} функций DTO.", dtos.size());
        return dtos;
    }

    public List<FunctionDTO> findAllFunctionsSorted(String field, String direction) {
        logger.info("Поиск всех функций с сортировкой по полю '{}' в направлении '{}'", field, direction);
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
                .toList();
        logger.debug("Найдено {} функций DTO, отсортированных по полю '{}'.", dtos.size(), field);
        return dtos;
    }

    public List<FunctionDTO> findFunctionsByUserId(Long userId) {
        logger.info("Поиск функций по ID пользователя: {}", userId);
        List<FunctionEntity> entities = functionRepository.findByUserId(userId);
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
                .toList();
        logger.debug("Найдено {} функций DTO для пользователя ID {}.", dtos.size(), userId);
        return dtos;
    }

    public List<FunctionDTO> findFunctionsByUserIdSorted(Long userId, String field, String direction) {
        logger.info("Поиск функций по ID пользователя {} с сортировкой по полю '{}' в направлении '{}'", userId, field, direction);
        Sort.Direction dir = Sort.Direction.fromString(direction);
        Sort sort = Sort.by(dir, field);
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
                .toList();
        logger.debug("Найдено {} функций DTO для пользователя ID {}, отсортированных по полю '{}'.", dtos.size(), userId, field);
        return dtos;
    }

    @Transactional
    public FunctionDTO createFunction(FunctionDTO functionDTO) {
        try {
            logger.info("=== START createFunction ===");
            logger.info("FunctionDTO: name='{}', userId='{}', type='{}', expression='{}'",
                    functionDTO.getName(), functionDTO.getUserId(), functionDTO.getType(), functionDTO.getExpression());

            if (functionDTO.getUserId() == null) {
                logger.error("userId is null!");
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
                logger.warn("FunctionDTO.pointsData is null, using default: \"{}\"");
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
            logger.info("=== END createFunction ===");

            return savedDto;

        } catch (Exception e) {
            logger.error("ERROR in createFunction: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка создания функции: " + e.getMessage(), e);
        }
    }

    @Transactional
    public FunctionDTO updateFunction(Long id, FunctionDTO functionDetails) {
        logger.info("Обновление функции DTO с ID: {}", id);
        return functionRepository.findById(id)
                .map(existingFunction -> {
                    logger.info("FunctionEntity found, updating fields");

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
                    logger.info("FunctionEntity updated successfully, ID: {}", id);

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
                    logger.info("FunctionDTO updated successfully, ID: {}", id);
                    return updatedDto;

                })
                .orElse(null);
    }

    @Transactional
    public boolean deleteFunctionById(Long id) {
        logger.info("Удаление функции DTO с ID: {}", id);
        if (functionRepository.existsById(id)) {
            functionRepository.deleteById(id);
            logger.info("FunctionDTO с ID {} удалена.", id);
            return true;
        } else {
            logger.warn("FunctionDTO с ID {} не найдена для удаления.", id);
            return false;
        }
    }

     public Set<FunctionDTO> findDependencyFunctionsDFS(Long resultFunctionId) {
         logger.info("Начало DFS для поиска зависимостей функции ID: {}", resultFunctionId);
         Set<Long> visitedIds = new HashSet<>();
         Set<FunctionDTO> dependencies = new HashSet<>();

         dfsRecursive(resultFunctionId, visitedIds, dependencies);
         logger.info("DFS завершён. Найдено {} зависимых функций DTO.", dependencies.size());
         return dependencies;
     }

     private void dfsRecursive(Long functionId, Set<Long> visitedIds, Set<FunctionDTO> dependencies) {
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
         FunctionDTO currentDto = new FunctionDTO(
                 currentFunc.getId(),
                 currentFunc.getUser().getId(),
                 currentFunc.getName(),
                 currentFunc.getType(),
                 currentFunc.getExpression(),
                 currentFunc.getLeftBound(),
                 currentFunc.getRightBound(),
                 currentFunc.getPointsCount(),
                 currentFunc.getPointsData()
         );
         dependencies.add(currentDto);
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

     public Set<FunctionDTO> findDependencyFunctionsBFS(Long startFunctionId, int maxDepth) {
         logger.info("Начало BFS для поиска зависимостей функции ID: {} с глубиной: {}", startFunctionId, maxDepth);
         Queue<Long> queue = new LinkedList<>();
         Set<Long> visitedIds = new HashSet<>();
         Set<FunctionDTO> foundDependencies = new HashSet<>();
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
                 FunctionDTO currentDto = new FunctionDTO(
                         currentFunc.getId(),
                         currentFunc.getUser().getId(),
                         currentFunc.getName(),
                         currentFunc.getType(),
                         currentFunc.getExpression(),
                         currentFunc.getLeftBound(),
                         currentFunc.getRightBound(),
                         currentFunc.getPointsCount(),
                         currentFunc.getPointsData()
                 );
                 foundDependencies.add(currentDto);

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
         logger.info("BFS завершён. Найдено {} зависимых функций DTO до глубины {}.", foundDependencies.size(), maxDepth);
         return foundDependencies;
     }
}