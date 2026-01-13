package lab5.service;

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

import java.util.List;
import java.util.Optional;

@Service
public class OperationService {

    private static final Logger logger = LoggerFactory.getLogger(OperationService.class);

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Transactional
    public OperationEntity createOperation(OperationDTO operationDTO) {
        try {
            logger.info("=== START createOperation ===");
            logger.info("OperationDTO: type={}, userId={}, function1Id={}, function2Id={}, resultFunctionId={}",
                    operationDTO.getOperationType(), operationDTO.getUserId(),
                    operationDTO.getFunction1Id(), operationDTO.getFunction2Id(),
                    operationDTO.getResultFunctionId());

            if (operationDTO.getUserId() == null) {
                throw new RuntimeException("userId is required");
            }
            if (operationDTO.getFunction1Id() == null) {
                throw new RuntimeException("function1Id is required");
            }
            if (operationDTO.getResultFunctionId() == null) {
                throw new RuntimeException("resultFunctionId is required");
            }

            UserEntity user = userRepository.findById(operationDTO.getUserId()).orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            FunctionEntity function1 = functionRepository.findById(operationDTO.getFunction1Id()).orElseThrow(() -> new RuntimeException("Функция 1 не найдена"));

            FunctionEntity function2 = null;
            if (operationDTO.getFunction2Id() != null) {
                function2 = functionRepository.findById(operationDTO.getFunction2Id()).orElseThrow(() -> new RuntimeException("Функция 2 не найдена"));
            }

            FunctionEntity resultFunction = functionRepository.findById(operationDTO.getResultFunctionId()).orElseThrow(() -> new RuntimeException("Результирующая функция не найдена"));

            OperationEntity operation = new OperationEntity();
            operation.setUser(user);
            operation.setFunction1(function1);
            operation.setFunction2(function2);
            operation.setResultFunction(resultFunction);
            operation.setOperationType(operationDTO.getOperationType());

            if (operationDTO.getParameters() != null) {
                operation.setParameters(operationDTO.getParameters());
            } else {
                operation.setParameters("{}");
            }

            OperationEntity savedOperation = operationRepository.save(operation);
            logger.info("Operation saved with ID: {}", savedOperation.getId());
            logger.info("=== END createOperation ===");

            return savedOperation;

        } catch (Exception e) {
            logger.error("ERROR in createOperation: {}", e.getMessage(), e);
            throw new RuntimeException("Ошибка при создании операции: " + e.getMessage(), e);
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