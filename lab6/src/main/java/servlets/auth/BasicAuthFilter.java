package servlets.auth;

import dto.UserDTO;
import dao.UserDAO;
import dao.UserDAOImpl;
import util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class BasicAuthFilter {
    private static final Logger logger = LoggerFactory.getLogger(BasicAuthFilter.class);
    private final UserDAO userDAO = new UserDAOImpl();

    public boolean doFilter(Object request, Object response) throws Exception {
        String authHeader = getHeader(request, "Authorization");
        String requestId = java.util.UUID.randomUUID().toString();

        logger.info("Auth check - Request ID: {}", requestId);

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            logger.warn("Missing or invalid Authorization header - Request ID: {}", requestId);
            setStatus(response, 401);
            setHeader(response, "WWW-Authenticate", "Basic realm=\"Math Functions API\"");
            sendJsonResponse(response, "{\"error\":\"Unauthorized\"}");
            return false;
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        String[] parts = credentials.split(":", 2);

        if (parts.length != 2) {
            logger.warn("Invalid credentials format - Request ID: {}", requestId);
            setStatus(response, 401);
            sendJsonResponse(response, "{\"error\":\"Invalid credentials format\"}");
            return false;
        }

        String username = parts[0];
        logger.debug("Checking authentication for user: {} - Request ID: {}", username, requestId);

        UserDTO user = userDAO.findUserByUsername(username);
        if (user == null) {
            logger.warn("User not found: {} - Request ID: {}", username, requestId);
            setStatus(response, 401);
            sendJsonResponse(response, "{\"error\":\"Invalid username or password\"}");
            return false;
        }

        logger.debug("Verifying password for user: {} - Request ID: {}", username, requestId);
        if (!PasswordUtil.verifyPassword(parts[1], user.getPasswordHash())) {
            logger.warn("Invalid password for user: {} - Request ID: {}", username, requestId);
            setStatus(response, 401);
            sendJsonResponse(response, "{\"error\":\"Invalid username or password\"}");
            return false;
        }

        logger.info("User {} authenticated successfully, roles: {} - Request ID: {}",
                username, user.getRoles(), requestId);

        String requestUri = getRequestUri(request);
        if (requestUri != null && requestUri.startsWith("/api/admin/")) {
            Set<String> roles = user.getRoles();
            if (roles == null || !roles.contains("ADMIN")) {
                logger.warn("Access denied for non-admin user: {} to admin endpoint: {} - Request ID: {}",
                        username, requestUri, requestId);
                setStatus(response, 403);
                sendJsonResponse(response, "{\"error\":\"Access denied. Admin role required.\"}");
                return false;
            }
            logger.debug("Admin access granted for user: {} - Request ID: {}", username, requestId);
        }

        setAttribute(request, "user", user);
        logger.info("Authentication successful for user: {} with roles: {} - Request ID: {}",
                username, user.getRoles(), requestId);
        return true;
    }

    private String getHeader(Object request, String headerName) {
        try {
            if (request instanceof javax.servlet.http.HttpServletRequest) {
                return ((javax.servlet.http.HttpServletRequest) request).getHeader(headerName);
            }
        } catch (Exception e) {
            logger.error("Error getting header: {}", e.getMessage());
        }
        return null;
    }

    private void setStatus(Object response, int status) {
        try {
            if (response instanceof javax.servlet.http.HttpServletResponse) {
                ((javax.servlet.http.HttpServletResponse) response).setStatus(status);
            }
        } catch (Exception e) {
            logger.error("Error setting status: {}", e.getMessage());
        }
    }

    private void setHeader(Object response, String name, String value) {
        try {
            if (response instanceof javax.servlet.http.HttpServletResponse) {
                ((javax.servlet.http.HttpServletResponse) response).setHeader(name, value);
            }
        } catch (Exception e) {
            logger.error("Error setting header: {}", e.getMessage());
        }
    }

    private void sendJsonResponse(Object response, String json) throws Exception {
        if (response instanceof javax.servlet.http.HttpServletResponse) {
            javax.servlet.http.HttpServletResponse httpResponse =
                    (javax.servlet.http.HttpServletResponse) response;
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            java.io.PrintWriter writer = httpResponse.getWriter();
            writer.write(json);
            writer.flush();
        }
    }

    private String getRequestUri(Object request) {
        try {
            if (request instanceof javax.servlet.http.HttpServletRequest) {
                return ((javax.servlet.http.HttpServletRequest) request).getRequestURI();
            }
        } catch (Exception e) {
            logger.error("Error getting request URI: {}", e.getMessage());
        }
        return null;
    }

    private void setAttribute(Object request, String name, Object value) {
        try {
            if (request instanceof javax.servlet.http.HttpServletRequest) {
                ((javax.servlet.http.HttpServletRequest) request).setAttribute(name, value);
            }
        } catch (Exception e) {
            logger.error("Error setting attribute: {}", e.getMessage());
        }
    }
}