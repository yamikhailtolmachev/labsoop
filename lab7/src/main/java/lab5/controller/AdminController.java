package lab5.controller;

import lab5.dto.UserDTO;
import lab5.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsersForAdmin(
            @RequestParam(required = false, defaultValue = "id") String sortField,
            @RequestParam(required = false, defaultValue = "asc") String sortDirection) {

        logger.info("Получен запрос на получение всех пользователей (админ), сортировка: {}:{}",
                sortField, sortDirection);

        try {
            List<UserDTO> users = userService.getAllUsersForAdmin(sortField, sortDirection);
            logger.debug("Найдено {} пользователей для админа.", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Ошибка при получении пользователей для админа: {}", e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}