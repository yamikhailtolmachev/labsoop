package lab5.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.stream.IntStream;

@Service
public class OperationService {

    private static final Logger logger = LoggerFactory.getLogger(OperationService.class);

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public OperationEntity createOperation(OperationDTO operationDTO) {
        try {
            logger.info("=== START createOperation ===");
            logger.info("OperationDTO: type={}, userId={}, function1Id={}, function2Id={}",
                    operationDTO.getOperationType(), operationDTO.getUserId(),
                    operationDTO.getFunction1Id(), operationDTO.getFunction2Id());

            if (operationDTO.getUserId() == null) {
                throw new RuntimeException("userId is required");
            }
            if (operationDTO.getFunction1Id() == null) {
                throw new RuntimeException("function1Id is required");
            }
            if (operationDTO.getFunction2Id() == null) {
                throw new RuntimeException("function2Id is required");
            }

            UserEntity user = userRepository.findById(operationDTO.getUserId())
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            FunctionEntity f1 = functionRepository.findById(operationDTO.getFunction1Id())
                    .orElseThrow(() -> new RuntimeException("Функция 1 не найдена"));

            FunctionEntity f2 = functionRepository.findById(operationDTO.getFunction2Id())
                    .orElseThrow(() -> new RuntimeException("Функция 2 не найдена"));

            if (!f1.getUser().getId().equals(user.getId()) || !f2.getUser().getId().equals(user.getId())) {
                throw new RuntimeException("Функции должны принадлежать текущему пользователю");
            }

            Map<String, Object> data1 = parsePointsData(f1.getPointsData());
            Map<String, Object> data2 = parsePointsData(f2.getPointsData());

            @SuppressWarnings("unchecked")
            List<Double> x1List = (List<Double>) data1.get("x");
            @SuppressWarnings("unchecked")
            List<Double> y1List = (List<Double>) data1.get("y");
            @SuppressWarnings("unchecked")
            List<Double> x2List = (List<Double>) data2.get("x");
            @SuppressWarnings("unchecked")
            List<Double> y2List = (List<Double>) data2.get("y");

            if (x1List.size() != x2List.size()) {
                throw new RuntimeException("Функции должны иметь одинаковое количество точек");
            }

            for (int i = 0; i < x1List.size(); i++) {
                if (Math.abs(x1List.get(i) - x2List.get(i)) > 1e-9) {
                    throw new RuntimeException("Значения X должны совпадать");
                }
            }

            List<Double> yResult;
            switch (operationDTO.getOperationType()) {
                case "ADD":
                    yResult = IntStream.range(0, y1List.size())
                            .mapToObj(i -> y1List.get(i) + y2List.get(i))
                            .collect(Collectors.toList());
                    break;
                case "SUBTRACT":
                    yResult = IntStream.range(0, y1List.size())
                            .mapToObj(i -> y1List.get(i) - y2List.get(i))
                            .collect(Collectors.toList());
                    break;
                case "MULTIPLY":
                    yResult = IntStream.range(0, y1List.size())
                            .mapToObj(i -> y1List.get(i) * y2List.get(i))
                            .collect(Collectors.toList());
                    break;
                case "DIVIDE":
                    yResult = IntStream.range(0, y1List.size())
                            .mapToObj(i -> {
                                double y2 = y2List.get(i);
                                if (Math.abs(y2) < 1e-12) {
                                    return Double.NaN;
                                }
                                return y1List.get(i) / y2;
                            })
                            .collect(Collectors.toList());
                    break;
                default:
                    throw new IllegalArgumentException("Неизвестная операция: " + operationDTO.getOperationType());
            }

            Map<String, Object> resultData = Map.of("x", x1List, "y", yResult);
            String resultJson;
            try {
                resultJson = objectMapper.writeValueAsString(resultData);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка сериализации результата", e);
            }

            FunctionEntity resultFunc = new FunctionEntity();
            resultFunc.setUser(user);
            resultFunc.setName("Результат " + operationDTO.getOperationType());
            resultFunc.setType("OPERATION_RESULT");
            resultFunc.setExpression("Операция " + operationDTO.getOperationType());
            resultFunc.setLeftBound(f1.getLeftBound());
            resultFunc.setRightBound(f1.getRightBound());
            resultFunc.setPointsCount(f1.getPointsCount());
            resultFunc.setPointsData(resultJson);
            resultFunc.setCreatedAt(LocalDateTime.now());
            resultFunc.setUpdatedAt(LocalDateTime.now());
            FunctionEntity savedResult = functionRepository.save(resultFunc);

            OperationEntity op = new OperationEntity();
            op.setUser(user);
            op.setFunction1(f1);
            op.setFunction2(f2);
            op.setResultFunction(savedResult);
            op.setOperationType(operationDTO.getOperationType());
            op.setComputedAt(LocalDateTime.now());
            op.setUpdatedAt(LocalDateTime.now());

            OperationEntity savedOp = operationRepository.save(op);
            logger.info("Operation saved with ID: {}", savedOp.getId());
            logger.info("=== END createOperation ===");

            return savedOp;

        } catch (Exception e) {
            logger.error("ERROR in createOperation: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при создании операции: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parsePointsData(String pointsData) {
        if (pointsData == null || pointsData.trim().isEmpty() || "{}".equals(pointsData)) {
            return Map.of("x", new ArrayList<Double>(), "y", new ArrayList<Double>());
        }
        try {
            return objectMapper.readValue(pointsData, Map.class);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга pointsData", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<OperationEntity> getOperationById(Long id) {
        logger.info("Getting operation by ID: {}", id);
        return operationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<OperationEntity> getAllOperations(String sortField, String sortDirection) {
        logger.info("Getting all operations, sort: {} {}", sortField, sortDirection);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return operationRepository.findAll(sort);
    }

    @Transactional(readOnly = true)
    public List<OperationEntity> getOperationsByUserId(Long userId, String sortField, String sortDirection) {
        logger.info("Getting operations for user ID: {}, sort: {} {}", userId, sortField, sortDirection);
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        return operationRepository.findByUser_Id(userId, sort);
    }

    @Transactional
    public boolean deleteOperation(Long id) {
        logger.info("Deleting operation ID: {}", id);
        if (operationRepository.existsById(id)) {
            operationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}