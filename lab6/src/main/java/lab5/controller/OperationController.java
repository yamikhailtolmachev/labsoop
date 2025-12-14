package lab5.controller;

import lab5.dto.OperationDTO;
import lab5.entity.OperationEntity;
import lab5.service.OperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/operations")
public class OperationController {

    private static final Logger logger = LoggerFactory.getLogger(OperationController.class);

    @Autowired
    private OperationService operationService;

    @GetMapping("/{id}")
    public ResponseEntity<OperationEntity> getOperationById(@PathVariable Long id) {
        logger.info("Получен запрос на получение операции с ID: {}", id);
        Optional<OperationEntity> operationOpt = operationService.getOperationById(id);
        if (operationOpt.isPresent()) {
            logger.debug("Возвращена операция с ID: {}", id);
            return ResponseEntity.ok(operationOpt.get());
        } else {
            logger.warn("Операция с ID {} не найдена.", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OperationEntity>> getAllOperations(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех операций, сортировка: {}:{}", sortField, sortDirection);
        List<OperationEntity> operations = operationService.getAllOperations(sortField, sortDirection);
        logger.debug("Возвращено {} операций.", operations.size());
        return ResponseEntity.ok(operations);
    }

    @PostMapping
    public ResponseEntity<OperationEntity> createOperation(@RequestBody OperationDTO operationDTO) {
        logger.info("Получен запрос на создание операции типа: {}", operationDTO.getOperationType());
        try {
            OperationEntity createdOperation = operationService.createOperation(operationDTO);
            logger.info("Операция создана с ID: {}", createdOperation.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOperation);
        } catch (Exception e) {
            logger.error("Ошибка при создании операции: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationEntity> updateOperation(@PathVariable Long id, @RequestBody OperationEntity operationDetails) {
        logger.info("Получен запрос на обновление операции с ID: {}", id);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOperation(@PathVariable Long id) {
        logger.info("Получен запрос на удаление операции с ID: {}", id);
        boolean deleted = operationService.deleteOperation(id);
        if (deleted) {
            logger.info("Операция с ID {} удалена.", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Операция с ID {} не найдена для удаления.", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OperationEntity>> getOperationsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение операций для пользователя с ID: {}, сортировка: {}:{}",
                userId, sortField, sortDirection);
        List<OperationEntity> operations = operationService.getOperationsByUserId(userId, sortField, sortDirection);
        if (!operations.isEmpty()) {
            logger.debug("Найдено {} операций для пользователя ID {}.", operations.size(), userId);
            return ResponseEntity.ok(operations);
        } else {
            logger.warn("Операции для пользователя с ID {} не найдены.", userId);
            return ResponseEntity.notFound().build();
        }
    }
}