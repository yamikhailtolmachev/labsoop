package servlets.api;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class BaseApiServlet extends HttpServlet {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ObjectMapper mapper = new ObjectMapper();

    protected String getPathParameter(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) return null;
        return pathInfo.substring(1);
    }

    protected void sendSuccess(HttpServletResponse response, String message) throws IOException {
        sendJsonResponse(response, success(message));
    }

    protected void sendSuccess(HttpServletResponse response, String message, Object data) throws IOException {
        sendJsonResponse(response, success(message, data));
    }

    protected void sendSuccess(HttpServletResponse response, Object data) throws IOException {
        sendJsonResponse(response, success(data));
    }

    protected void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        sendJsonResponse(response, error(status, message));
    }

    protected String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    protected <T> T readRequestBody(HttpServletRequest request, Class<T> valueType) throws IOException {
        return mapper.readValue(request.getReader(), valueType);
    }

    protected String getCurrentUserId() {
        return "test-user-id";
    }

    protected int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
        String value = request.getParameter(paramName);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    protected UUID getUuidParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value != null) {
            try {
                return UUID.fromString(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }

    private void sendJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        mapper.writeValue(response.getWriter(), data);
    }

    private Map<String, Object> success(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        return result;
    }

    private Map<String, Object> success(String message, Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", message);
        result.put("data", data);
        return result;
    }

    private Map<String, Object> success(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        return result;
    }

    private Map<String, Object> error(int status, String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", message);
        result.put("status", status);
        return result;
    }
}