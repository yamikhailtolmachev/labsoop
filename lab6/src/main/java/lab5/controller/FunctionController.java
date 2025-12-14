package lab5.controller;

import lab5.dto.FunctionDTO;
import lab5.entity.FunctionEntity;
import lab5.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/functions")
public class FunctionController {

    private static final Logger logger = LoggerFactory.getLogger(FunctionController.class);

    @Autowired
    private lab5.service.SearchService searchService;

    @GetMapping("/{id}")
    public ResponseEntity<FunctionDTO> getFunctionById(@PathVariable Long id) {
        logger.info("Получен запрос на получение функции с ID: {}", id);
        Optional<FunctionDTO> functionOpt = searchService.findFunctionById(id);
        if (functionOpt.isPresent()) {
            logger.debug("Возвращена функция DTO с ID: {}", id);
            return ResponseEntity.ok(functionOpt.get());
        } else {
            logger.warn("Функция с ID {} не найдена.", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<FunctionDTO>> getAllFunctions(
                                                              @RequestParam(required = false, defaultValue = "id") String sortField,
                                                              @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех функций, сортировка: {}:{}", sortField, sortDirection);
        Sort.Direction dir = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(dir, sortField);
        List<FunctionDTO> functions = searchService.findAllFunctionsSorted(sortField, sortDirection);
        logger.debug("Найдено {} функций DTO.", functions.size());
        return ResponseEntity.ok(functions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FunctionDTO>> getFunctionsByUserId(
                                                                   @PathVariable Long userId,
                                                                   @RequestParam(required = false, defaultValue = "id") String sortField,
                                                                   @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение функций для пользователя с ID: {}, сортировка: {}:{}", userId, sortField, sortDirection);
        Sort.Direction dir = Sort.Direction.fromString(sortDirection);
        Sort sort = Sort.by(dir, sortField);
        List<FunctionDTO> functions = searchService.findFunctionsByUserIdSorted(userId, sortField, sortDirection); // <-- Вызываем метод сервиса, возвращающий List<DTO>
        if (!functions.isEmpty()) {
            logger.debug("Найдено {} функций DTO для пользователя ID {}.", functions.size(), userId);
            return ResponseEntity.ok(functions);
        } else {
            logger.warn("Функции для пользователя с ID {} не найдены.", userId);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<FunctionDTO> createFunction(@RequestBody FunctionDTO functionDTO) {
        logger.info("Получен запрос на создание функции DTO: name='{}', userId='{}'", functionDTO.getName(), functionDTO.getUserId());
        try {
            FunctionDTO createdFunction = searchService.createFunction(functionDTO);
            logger.info("Функция DTO создана с ID: {}", createdFunction.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdFunction);
        } catch (Exception e) {
            logger.error("Ошибка при создании функции DTO: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunctionDTO> updateFunction(@PathVariable Long id, @RequestBody FunctionDTO functionDetails) {
        logger.info("Получен запрос на обновление функции DTO с ID: {}", id);
        try {
            FunctionDTO updatedFunction = searchService.updateFunction(id, functionDetails);
            if (updatedFunction != null) {
                logger.info("Функция DTO с ID {} обновлена.", id);
                return ResponseEntity.ok(updatedFunction);
            } else {
                logger.warn("Функция DTO с ID {} не найдена для обновления.", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Ошибка при обновлении функции DTO: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        logger.info("Получен запрос на удаление функции с ID: {}", id);
        boolean deleted = searchService.deleteFunctionById(id);
        if (deleted) {
            logger.info("Функция с ID {} удалена.", id);
            return ResponseEntity.noContent().build();
        } else {
            logger.warn("Функция с ID {} не найдена для удаления.", id);
            return ResponseEntity.notFound().build();
        }
    }
}