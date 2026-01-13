package lab5.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(Principal principal) {
        if (principal == null) {
            logger.warn("Попытка логина без аутентификации");
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Unauthorized",
                    "message", "Authentication required"
            ));
        }

        logger.info("Пользователь {} успешно аутентифицирован", principal.getName());
        return ResponseEntity.ok(Map.of(
                "message", "Login successful",
                "username", principal.getName(),
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkAuth(Principal principal) {
        if (principal == null) {
            logger.warn("Попытка доступа без аутентификации");
            return ResponseEntity.status(401).body(Map.of(
                    "error", "Unauthorized",
                    "message", "Authentication required"
            ));
        }

        logger.info("Пользователь {} аутентифицирован", principal.getName());
        return ResponseEntity.ok(Map.of(
                "message", "Authenticated successfully",
                "username", principal.getName(),
                "timestamp", System.currentTimeMillis()
        ));
    }

    @GetMapping("/public")
    public ResponseEntity<?> publicEndpoint() {
        return ResponseEntity.ok(Map.of(
                "message", "This is a public endpoint",
                "timestamp", System.currentTimeMillis()
        ));
    }
}