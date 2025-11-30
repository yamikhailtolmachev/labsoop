package servlets.auth;
import util.ResponseUtil;
import util.AuthUtil;
public class BasicAuthFilter {
    public boolean doFilter(Object request, Object response) throws Exception {
        String authHeader = ResponseUtil.getHeader(request, "Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            ResponseUtil.setStatus(response, 401);
            ResponseUtil.sendJsonResponse(response, "{\"error\":\"Unauthorized\"}");
            return false;
        }
        if (!AuthUtil.validateBasicAuth(authHeader)) {
            ResponseUtil.setStatus(response, 401);
            ResponseUtil.sendJsonResponse(response, "{\"error\":\"Invalid credentials\"}");
            return false;
        }
        return true;
    }
}