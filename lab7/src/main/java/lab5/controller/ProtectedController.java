package lab5.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedController.class);

    @GetMapping("/admin-only")
    public ResponseEntity<String> adminOnlyEndpoint() {
        logger.info("Доступ к эндпоинту /admin-only");
        return ResponseEntity.ok("Это защищённый эндпоинт только для администраторов.");
    }

    @GetMapping("/user-or-admin")
    public ResponseEntity<String> userOrAdminEndpoint() {
        logger.info("Доступ к эндпоинту /user-or-admin");
        return ResponseEntity.ok("Это защищённый эндпоинт для пользователей и администраторов.");
    }
}