package lab5.controller;

import lab5.entity.FunctionEntity;
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
@RequestMapping("/api/functions")
public class FunctionController {

    private static final Logger logger = LoggerFactory.getLogger(FunctionController.class);

    @Autowired
    private SearchService searchService;

    @GetMapping("/{id}")
    public ResponseEntity<FunctionEntity> getFunctionById(@PathVariable Long id) {
        logger.info("Получен запрос на получение функции с ID: {}", id);
        Optional<FunctionEntity> functionOpt = searchService.findFunctionById(id);
        if (functionOpt.isPresent()) {
            logger.debug("Возвращена функция с ID: {}", id);
            return new ResponseEntity<>(functionOpt.get(), HttpStatus.OK);
        } else {
            logger.warn("Функция с ID {} не найдена.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<FunctionEntity>> getAllFunctions(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех функций, сортировка: {}:{}", sortField, sortDirection);
        List<FunctionEntity> functions = searchService.findAllFunctionsSorted(sortField, sortDirection);
        logger.debug("Возвращено {} функций.", functions.size());
        return new ResponseEntity<>(functions, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<FunctionEntity> createFunction(@RequestBody FunctionEntity function) {
        logger.info("Получен запрос на создание функции: {}", function.getName());
        FunctionEntity createdFunction = searchService.createFunction(function);
        logger.info("Функция создана с ID: {}", createdFunction.getId());
        return new ResponseEntity<>(createdFunction, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunctionEntity> updateFunction(@PathVariable Long id, @RequestBody FunctionEntity functionDetails) {
        logger.info("Получен запрос на обновление функции с ID: {}", id);
        FunctionEntity updatedFunction = searchService.updateFunction(id, functionDetails);
        if (updatedFunction != null) {
            logger.info("Функция с ID {} обновлена.", id);
            return new ResponseEntity<>(updatedFunction, HttpStatus.OK);
        } else {
            logger.warn("Функция с ID {} не найдена для обновления.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunction(@PathVariable Long id) {
        logger.info("Получен запрос на удаление функции с ID: {}", id);
        boolean deleted = searchService.deleteFunctionById(id);
        if (deleted) {
            logger.info("Функция с ID {} удалена.", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logger.warn("Функция с ID {} не найдена для удаления.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FunctionEntity>> getFunctionsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение функций для пользователя с ID: {}, сортировка: {}:{}", userId, sortField, sortDirection);
        List<FunctionEntity> functions = searchService.findFunctionsByUserIdSorted(userId, sortField, sortDirection);
        if (!functions.isEmpty()) {
            logger.debug("Найдено {} функций для пользователя ID {}.", functions.size(), userId);
            return new ResponseEntity<>(functions, HttpStatus.OK);
        } else {
            logger.warn("Функции для пользователя с ID {} не найдены.", userId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}