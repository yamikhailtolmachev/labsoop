package lab5.controller;

import lab5.entity.OperationEntity;
import lab5.service.SearchService;
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
    private SearchService searchService;

    @GetMapping("/{id}")
    public ResponseEntity<OperationEntity> getOperationById(@PathVariable Long id) {
        logger.info("Получен запрос на получение операции с ID: {}", id);
        Optional<OperationEntity> operationOpt = searchService.findOperationById(id);
        if (operationOpt.isPresent()) {
            logger.debug("Возвращена операция с ID: {}", id);
            return new ResponseEntity<>(operationOpt.get(), HttpStatus.OK);
        } else {
            logger.warn("Операция с ID {} не найдена.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<OperationEntity>> getAllOperations(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех операций, сортировка: {}:{}", sortField, sortDirection);
        List<OperationEntity> operations = searchService.findAllOperationsSorted(sortField, sortDirection);
        logger.debug("Возвращено {} операций.", operations.size());
        return new ResponseEntity<>(operations, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<OperationEntity> createOperation(@RequestBody OperationEntity operation) {
        logger.info("Получен запрос на создание операции типа: {}", operation.getOperationType());
        OperationEntity createdOperation = searchService.createOperation(operation);
        logger.info("Операция создана с ID: {}", createdOperation.getId());
        return new ResponseEntity<>(createdOperation, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OperationEntity> updateOperation(@PathVariable Long id, @RequestBody OperationEntity operationDetails) {
        logger.info("Получен запрос на обновление операции с ID: {}", id);
        OperationEntity updatedOperation = searchService.updateOperation(id, operationDetails);
        if (updatedOperation != null) {
            logger.info("Операция с ID {} обновлена.", id);
            return new ResponseEntity<>(updatedOperation, HttpStatus.OK);
        } else {
            logger.warn("Операция с ID {} не найдена для обновления.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOperation(@PathVariable Long id) {
        logger.info("Получен запрос на удаление операции с ID: {}", id);
        boolean deleted = searchService.deleteOperationById(id);
        if (deleted) {
            logger.info("Операция с ID {} удалена.", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("Операция с ID {} не найдена для удаления.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OperationEntity>> getOperationsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение операций для пользователя с ID: {}, сортировка: {}:{}", userId, sortField, sortDirection);
        List<OperationEntity> operations = searchService.findOperationsByUserIdSorted(userId, sortField, sortDirection);
        if (!operations.isEmpty()) {
            logger.debug("Найдено {} операций для пользователя ID {}.", operations.size(), userId);
            return new ResponseEntity<>(operations, HttpStatus.OK);
        } else {
            logger.warn("Операции для пользователя с ID {} не найдены.", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}