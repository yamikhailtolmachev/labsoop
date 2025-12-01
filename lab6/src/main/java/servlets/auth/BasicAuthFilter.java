package servlets.auth;

import util.ResponseUtil;
import util.AuthUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BasicAuthFilter {
    private static final Logger logger = LoggerFactory.getLogger(BasicAuthFilter.class);

    public boolean doFilter(Object request, Object response) throws Exception {
        String authHeader = ResponseUtil.getHeader(request, "Authorization");
        String requestId = java.util.UUID.randomUUID().toString();

        logger.info("Auth check - Request ID: {}", requestId);

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            logger.warn("Missing or invalid Authorization header - Request ID: {}", requestId);
            ResponseUtil.setStatus(response, 401);
            ResponseUtil.sendJsonResponse(response, "{\"error\":\"Unauthorized\"}");
            return false;
        }

        if (!AuthUtil.validateBasicAuth(authHeader)) {
            logger.warn("Invalid credentials - Request ID: {}", requestId);
            ResponseUtil.setStatus(response, 401);
            ResponseUtil.sendJsonResponse(response, "{\"error\":\"Invalid credentials\"}");
            return false;
        }

        logger.info("Authentication successful - Request ID: {}", requestId);
        return true;
    }
}