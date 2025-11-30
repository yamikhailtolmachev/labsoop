package servlets.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import dao.CacheDAO;
import dao.CacheDAOImpl;
import dto.CacheDTO;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/cache/*")
public class CacheServlet extends HttpServlet {
    private CacheDAO cacheDAO = new CacheDAOImpl();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                String userIdParam = request.getParameter("userId");
                String expressionPattern = request.getParameter("expression");
                String minPoints = request.getParameter("minPoints");
                String maxPoints = request.getParameter("maxPoints");
                String sortBy = request.getParameter("sortBy");
                String sortOrder = request.getParameter("sortOrder");

                UUID userId = userIdParam != null ? UUID.fromString(userIdParam) : null;
                Integer minPts = minPoints != null ? Integer.parseInt(minPoints) : null;
                Integer maxPts = maxPoints != null ? Integer.parseInt(maxPoints) : null;

                if (userId != null || expressionPattern != null || minPts != null || maxPts != null) {
                    var cacheEntries = cacheDAO.findCacheByMultipleCriteria(userId, expressionPattern, minPts, maxPts, sortBy, sortOrder);
                    sendSuccess(response, cacheEntries);
                } else {
                    var cacheEntries = cacheDAO.findAllCache();
                    sendSuccess(response, cacheEntries);
                }
            } else if (pathInfo.equals("/most-accessed")) {
                int limit = getIntParameter(request, "limit", 10);
                var cacheEntries = cacheDAO.findMostAccessedCache(limit);
                sendSuccess(response, cacheEntries);
            } else if (pathInfo.equals("/recent")) {
                String userIdParam = request.getParameter("userId");
                int days = getIntParameter(request, "days", 7);

                if (userIdParam == null) {
                    sendError(response, 400, "userId parameter required");
                    return;
                }

                var cacheEntries = cacheDAO.findRecentCache(UUID.fromString(userIdParam), days);
                sendSuccess(response, cacheEntries);
            } else {
                String cacheKey = pathInfo.substring(1);
                CacheDTO cache = cacheDAO.findCacheByKey(cacheKey);
                if (cache != null) {
                    cacheDAO.updateCacheAccess(cacheKey);
                    sendSuccess(response, cache);
                } else {
                    sendError(response, 404, "Cache entry not found");
                }
            }
        } catch (Exception e) {
            sendError(response, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            CacheDTO cache = mapper.readValue(request.getReader(), CacheDTO.class);
            cacheDAO.insertCache(cache);

            Map<String, String> result = new HashMap<>();
            result.put("message", "Cache entry created successfully");

            response.setStatus(201);
            sendSuccess(response, result);
        } catch (Exception e) {
            sendError(response, 400, "Invalid cache data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                sendError(response, 400, "Cache key required");
                return;
            }

            String cacheKey = pathInfo.substring(1);
            cacheDAO.deleteCache(cacheKey);

            Map<String, String> result = new HashMap<>();
            result.put("message", "Cache entry deleted successfully");
            sendSuccess(response, result);
        } catch (Exception e) {
            sendError(response, 500, e.getMessage());
        }
    }

    private void sendSuccess(HttpServletResponse response, Object data) throws IOException {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", data);
        mapper.writeValue(response.getWriter(), result);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("error", message);
        mapper.writeValue(response.getWriter(), result);
    }

    private int getIntParameter(HttpServletRequest request, String paramName, int defaultValue) {
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
}