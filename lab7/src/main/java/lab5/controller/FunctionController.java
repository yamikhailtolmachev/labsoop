package lab5.controller;

import lab5.dto.FunctionDTO;
import lab5.entity.UserEntity;
import lab5.service.SearchService;
import lab5.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/functions")
public class FunctionController {

    private static final Logger logger = LoggerFactory.getLogger(FunctionController.class);

    @Autowired
    private SearchService searchService;

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getFunctionById(@PathVariable Long id, Authentication authentication) {
        logger.info("Получен запрос на получение функции с ID: {}", id);
        try {
            Optional<FunctionDTO> functionOpt = searchService.findFunctionById(id);
            if (functionOpt.isPresent()) {
                FunctionDTO function = functionOpt.get();

                String currentUsername = authentication.getName();
                Optional<UserEntity> currentUserOpt = userService.getUserByUsername(currentUsername);

                if (currentUserOpt.isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Текущий пользователь не найден");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }

                UserEntity currentUser = currentUserOpt.get();
                boolean isAdmin = currentUser.getRole().getName().equals("ADMIN");
                boolean isOwnFunction = function.getUserId().equals(currentUser.getId());

                if (!isAdmin && !isOwnFunction) {
                    logger.warn("Пользователь {} пытается получить доступ к функции пользователя {}",
                            currentUsername, function.getUserId());
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Доступ запрещен. Вы можете просматривать только свои функции");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                }

                logger.debug("Возвращена функция DTO с ID: {}", id);
                return ResponseEntity.ok(function);
            } else {
                logger.warn("Функция с ID {} не найдена.", id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Функция с ID " + id + " не найдена");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Ошибка при получении функции с ID {}: {}", id, e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllFunctions(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {
        logger.info("Получен запрос на получение всех функций, сортировка: {}:{}", sortField, sortDirection);
        try {
            List<FunctionDTO> functions = searchService.findAllFunctionsSorted(sortField, sortDirection);
            logger.debug("Найдено {} функций DTO.", functions.size());
            return ResponseEntity.ok(functions);
        } catch (Exception e) {
            logger.error("Ошибка при получении всех функций: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/user/{userId}")
    @Transactional(readOnly = true)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getFunctionsByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection,
            Authentication authentication) {
        logger.info("Получен запрос на получение функций для пользователя с ID: {}, сортировка: {}:{}",
                userId, sortField, sortDirection);
        try {
            String currentUsername = authentication.getName();
            Optional<UserEntity> currentUserOpt = userService.getUserByUsername(currentUsername);

            if (currentUserOpt.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Текущий пользователь не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            UserEntity currentUser = currentUserOpt.get();
            boolean isAdmin = "ADMIN".equals(currentUser.getRole().getName());
            boolean isOwnFunctions = currentUser.getId().equals(userId);

            if (!isAdmin && !isOwnFunctions) {
                logger.warn("Пользователь {} пытается получить функции пользователя {}", currentUsername, userId);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Доступ запрещен. Вы можете просматривать только свои функции");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            Optional<UserEntity> targetUserOpt = userService.getUserById(userId);
            if (targetUserOpt.isEmpty()) {
                logger.warn("Пользователь с ID {} не найден.", userId);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Пользователь с ID " + userId + " не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            List<FunctionDTO> functions = searchService.findFunctionsByUserIdSorted(userId, sortField, sortDirection);
            logger.debug("Найдено {} функций DTO для пользователя ID {}.", functions.size(), userId);
            return ResponseEntity.ok(functions);

        } catch (Exception e) {
            logger.error("Ошибка при получении функций для пользователя ID {}: {}", userId, e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> createFunction(@RequestBody FunctionDTO functionDTO, Authentication authentication) {
        logger.info("Получен запрос на создание функции DTO");
        try {
            String currentUsername = authentication.getName();
            Optional<UserEntity> currentUserOpt = userService.getUserByUsername(currentUsername);

            if (currentUserOpt.isEmpty()) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Текущий пользователь не найден");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            UserEntity currentUser = currentUserOpt.get();
            boolean isAdmin = currentUser.getRole().getName().equals("ADMIN");

            if (!isAdmin && !currentUser.getId().equals(functionDTO.getUserId())) {
                logger.warn("Пользователь {} пытается создать функцию для другого пользователя {}",
                        currentUsername, functionDTO.getUserId());
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Вы можете создавать функции только для себя");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }

            FunctionDTO createdFunction = searchService.createFunction(functionDTO);
            if (createdFunction != null) {
                logger.debug("Функция DTO создана с ID: {}", createdFunction.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(createdFunction);
            } else {
                logger.error("Ошибка при создании функции DTO: возвращено null");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Не удалось создать функцию");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Ошибка при создании функции DTO: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateFunction(@PathVariable Long id, @RequestBody FunctionDTO functionDetails,
                                            Authentication authentication) {
        logger.info("Получен запрос на обновление функции DTO с ID: {}", id);
        try {
            Optional<FunctionDTO> existingFunctionOpt = searchService.findFunctionById(id);
            if (existingFunctionOpt.isPresent()) {
                FunctionDTO existingFunction = existingFunctionOpt.get();

                String currentUsername = authentication.getName();
                Optional<UserEntity> currentUserOpt = userService.getUserByUsername(currentUsername);

                if (currentUserOpt.isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Текущий пользователь не найден");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }

                UserEntity currentUser = currentUserOpt.get();
                boolean isAdmin = currentUser.getRole().getName().equals("ADMIN");
                boolean isOwnFunction = existingFunction.getUserId().equals(currentUser.getId());

                if (!isAdmin && !isOwnFunction) {
                    logger.warn("Пользователь {} пытается обновить функцию пользователя {}",
                            currentUsername, existingFunction.getUserId());
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Доступ запрещен. Вы можете обновлять только свои функции");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                }

                FunctionDTO updatedFunction = searchService.updateFunction(id, functionDetails);
                if (updatedFunction != null) {
                    logger.info("Функция DTO с ID {} обновлена.", id);
                    return ResponseEntity.ok(updatedFunction);
                } else {
                    logger.warn("Функция DTO с ID {} не найдена для обновления.", id);
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Функция с ID " + id + " не найдена");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }
            } else {
                logger.warn("Функция DTO с ID {} не найдена.", id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Функция с ID " + id + " не найдена");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Ошибка при обновлении функции DTO: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> deleteFunction(@PathVariable Long id, Authentication authentication) {
        logger.info("Получен запрос на удаление функции с ID: {}", id);
        try {
            Optional<FunctionDTO> functionOpt = searchService.findFunctionById(id);
            if (functionOpt.isPresent()) {
                FunctionDTO function = functionOpt.get();

                String currentUsername = authentication.getName();
                Optional<UserEntity> currentUserOpt = userService.getUserByUsername(currentUsername);

                if (currentUserOpt.isEmpty()) {
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Текущий пользователь не найден");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }

                UserEntity currentUser = currentUserOpt.get();
                boolean isAdmin = currentUser.getRole().getName().equals("ADMIN");
                boolean isOwnFunction = function.getUserId().equals(currentUser.getId());

                if (!isAdmin && !isOwnFunction) {
                    logger.warn("Пользователь {} пытается удалить функцию пользователя {}",
                            currentUsername, function.getUserId());
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Доступ запрещен. Вы можете удалять только свои функции");
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
                }

                boolean deleted = searchService.deleteFunctionById(id);
                if (deleted) {
                    logger.info("Функция с ID {} удалена.", id);
                    return ResponseEntity.noContent().build();
                } else {
                    logger.warn("Функция с ID {} не найдена для удаления.", id);
                    Map<String, String> errorResponse = new HashMap<>();
                    errorResponse.put("error", "Функция с ID " + id + " не найдена");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                }
            } else {
                logger.warn("Функция с ID {} не найдена.", id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Функция с ID " + id + " не найдена");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            logger.error("Ошибка при удалении функции: {}", e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Внутренняя ошибка сервера");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}