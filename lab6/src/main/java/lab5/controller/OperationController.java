package lab5.controller;

import lab5.dto.OperationResponseDTO;
import lab5.entity.OperationEntity;
import lab5.service.OperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/operations")
public class OperationController {

    private static final Logger logger = LoggerFactory.getLogger(OperationController.class);

    @Autowired
    private OperationService operationService;

    @GetMapping
    public ResponseEntity<List<OperationResponseDTO>> getAllOperations(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех операций, сортировка: {}:{}", sortField, sortDirection);
        try {
            List<OperationEntity> operations = operationService.getAllOperations(sortField, sortDirection);
            List<OperationResponseDTO> responseDtos = operations.stream()
                    .map(OperationResponseDTO::fromEntity)
                    .collect(Collectors.toList());
            logger.debug("Найдено {} операций.", responseDtos.size());
            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            logger.error("Ошибка при получении всех операций: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OperationResponseDTO>> getOperationsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение операций для пользователя с ID: {}, сортировка: {}:{}", userId, sortField, sortDirection);
        try {
            List<OperationEntity> operations = operationService.getOperationsByUserId(userId, sortField, sortDirection);
            List<OperationResponseDTO> responseDtos = operations.stream()
                    .map(OperationResponseDTO::fromEntity)
                    .collect(Collectors.toList());
            if (!responseDtos.isEmpty()) {
                logger.debug("Найдено {} операций для пользователя ID {}.", responseDtos.size(), userId);
                return ResponseEntity.ok(responseDtos);
            } else {
                logger.warn("Операции для пользователя с ID {} не найдены.", userId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении операций по ID пользователя: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationResponseDTO> getOperationById(@PathVariable Long id) {
        logger.info("Получен запрос на получение операции с ID: {}", id);
        try {
            Optional<OperationEntity> operationOpt = operationService.getOperationById(id);
            if (operationOpt.isPresent()) {
                OperationResponseDTO responseDto = OperationResponseDTO.fromEntity(operationOpt.get());
                logger.debug("Возвращена операция с ID: {}", id);
                return ResponseEntity.ok(responseDto);
            } else {
                logger.warn("Операция с ID {} не найдена.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении операции по ID: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public ResponseEntity<OperationResponseDTO> createOperation(@RequestBody lab5.dto.OperationDTO operationDTO) {
        logger.info("Получен запрос на создание операции DTO");
        try {
            OperationEntity createdOperation = operationService.createOperation(operationDTO);
            if (createdOperation != null) {
                OperationResponseDTO responseDto = OperationResponseDTO.fromEntity(createdOperation);
                logger.debug("Операция DTO создана с ID: {}", responseDto.getId());
                return ResponseEntity.status(201).body(responseDto);
            } else {
                logger.error("Ошибка при создании операции DTO: возвращено null");
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при создании операции DTO: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
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
}