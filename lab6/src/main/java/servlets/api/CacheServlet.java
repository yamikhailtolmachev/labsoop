package servlets.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.CacheDAO;
import dao.CacheDAOImpl;
import dto.CacheDTO;
import dto.UserDTO;
import java.io.IOException;
import java.util.UUID;
import java.util.Map;

public class CacheServlet extends BaseApiServlet {
    private CacheDAO cacheDAO = new CacheDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDTO user = (UserDTO) request.getAttribute("user");
        String pathInfo = request.getPathInfo();
        String requestId = UUID.randomUUID().toString();

        if (user != null) {
            logger.info("CACHE_ACCESS: User '{}' (roles: {}) accessing GET {} - Request ID: {}",
                    user.getUsername(), user.getRoles(), pathInfo != null ? pathInfo : "/", requestId);
        } else {
            logger.warn("CACHE_ACCESS: Unauthenticated access attempt to GET {} - Request ID: {}",
                    pathInfo != null ? pathInfo : "/", requestId);
        }

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllCache(request, response, requestId);
            } else if (pathInfo.equals("/search")) {
                handleSearchCache(request, response, requestId);
            } else if (pathInfo.equals("/most-accessed")) {
                handleMostAccessedCache(request, response, requestId);
            } else if (pathInfo.equals("/recent")) {
                handleRecentCache(request, response, requestId);
            } else {
                handleGetCacheByKey(request, response, pathInfo, requestId);
            }
        } catch (Exception e) {
            logger.error("Error processing GET request {} - ID: {}", pathInfo, requestId, e);
            sendError(response, 500, "Internal server error");
        }
    }

    private void handleGetAllCache(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        UserDTO user = (UserDTO) request.getAttribute("user");
        if (user == null) {
            logger.warn("CACHE_DENIED: Unauthenticated access to all cache - Request ID: {}", requestId);
            sendError(response, 401, "Authentication required");
            return;
        }

        logger.debug("Fetching all cache entries - Request ID: {}", requestId);
        var cacheEntries = cacheDAO.findAllCache();
        logger.info("Retrieved {} cache entries - Request ID: {}", cacheEntries.size(), requestId);
        sendSuccess(response, cacheEntries);
    }

    private void handleSearchCache(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        UserDTO user = (UserDTO) request.getAttribute("user");
        if (user == null) {
            logger.warn("CACHE_DENIED: Unauthenticated access to cache search - Request ID: {}", requestId);
            sendError(response, 401, "Authentication required");
            return;
        }

        UUID userId = getUuidParameter(request, "userId");
        String expressionPattern = request.getParameter("expression");
        String minPointsStr = request.getParameter("minPoints");
        String maxPointsStr = request.getParameter("maxPoints");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");

        Integer minPoints = null;
        Integer maxPoints = null;

        if (minPointsStr != null) {
            try {
                minPoints = Integer.parseInt(minPointsStr);
            } catch (NumberFormatException e) {
                logger.warn("Invalid minPoints parameter: {} - Request ID: {}", minPointsStr, requestId);
            }
        }

        if (maxPointsStr != null) {
            try {
                maxPoints = Integer.parseInt(maxPointsStr);
            } catch (NumberFormatException e) {
                logger.warn("Invalid maxPoints parameter: {} - Request ID: {}", maxPointsStr, requestId);
            }
        }

        logger.debug("Searching cache - user: {}, expression: {}, points: {}-{} - Request ID: {}",
                userId, expressionPattern, minPoints, maxPoints, requestId);

        var cacheEntries = cacheDAO.findCacheByMultipleCriteria(userId, expressionPattern, minPoints, maxPoints, sortBy, sortOrder);
        logger.info("Found {} cache entries matching criteria - Request ID: {}", cacheEntries.size(), requestId);
        sendSuccess(response, cacheEntries);
    }

    private void handleMostAccessedCache(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        UserDTO user = (UserDTO) request.getAttribute("user");
        if (user == null) {
            logger.warn("CACHE_DENIED: Unauthenticated access to most accessed cache - Request ID: {}", requestId);
            sendError(response, 401, "Authentication required");
            return;
        }

        int limit = getIntParameter(request, "limit", 10);
        logger.debug("Fetching {} most accessed cache entries - Request ID: {}", limit, requestId);

        var cacheEntries = cacheDAO.findMostAccessedCache(limit);
        logger.info("Found {} most accessed cache entries - Request ID: {}", cacheEntries.size(), requestId);
        sendSuccess(response, cacheEntries);
    }

    private void handleRecentCache(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        UserDTO user = (UserDTO) request.getAttribute("user");
        if (user == null) {
            logger.warn("CACHE_DENIED: Unauthenticated access to recent cache - Request ID: {}", requestId);
            sendError(response, 401, "Authentication required");
            return;
        }

        UUID userId = getUuidParameter(request, "userId");
        int days = getIntParameter(request, "days", 7);

        if (userId == null) {
            logger.warn("userId parameter required for recent cache - Request ID: {}", requestId);
            sendError(response, 400, "userId parameter required");
            return;
        }

        logger.debug("Fetching recent cache for user: {}, days: {} - Request ID: {}", userId, days, requestId);
        var cacheEntries = cacheDAO.findRecentCache(userId, days);
        logger.info("Found {} recent cache entries for user {} - Request ID: {}", cacheEntries.size(), userId, requestId);
        sendSuccess(response, cacheEntries);
    }

    private void handleGetCacheByKey(HttpServletRequest request, HttpServletResponse response, String pathInfo, String requestId) throws IOException {
        UserDTO user = (UserDTO) request.getAttribute("user");
        if (user == null) {
            logger.warn("CACHE_DENIED: Unauthenticated access to cache key {} - Request ID: {}", pathInfo, requestId);
            sendError(response, 401, "Authentication required");
            return;
        }

        String cacheKey = pathInfo.substring(1);
        logger.debug("Fetching cache by key: {} - Request ID: {}", cacheKey, requestId);

        CacheDTO cache = cacheDAO.findCacheByKey(cacheKey);
        if (cache != null) {
            cacheDAO.updateCacheAccess(cacheKey);
            logger.info("Cache entry found and access updated: {} - Request ID: {}", cacheKey, requestId);
            sendSuccess(response, cache);
        } else {
            logger.warn("Cache entry not found: {} - Request ID: {}", cacheKey, requestId);
            sendError(response, 404, "Cache entry not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDTO user = (UserDTO) request.getAttribute("user");
        String requestId = UUID.randomUUID().toString();

        if (user == null) {
            logger.warn("CACHE_DENIED: Unauthenticated POST to /api/cache - Request ID: {}", requestId);
            sendError(response, 401, "Authentication required");
            return;
        }

        if (!user.hasRole("USER") && !user.hasRole("ADMIN")) {
            logger.warn("CACHE_DENIED: User '{}' (roles: {}) lacks USER role for POST - Request ID: {}",
                    user.getUsername(), user.getRoles(), requestId);
            sendError(response, 403, "USER role required");
            return;
        }

        logger.info("CACHE_CREATE: User '{}' creating cache entry - Request ID: {}", user.getUsername(), requestId);

        try {
            CacheDTO cache = readRequestBody(request, CacheDTO.class);
            logger.debug("Creating cache entry: {} - Request ID: {}", cache.getCacheKey(), requestId);

            cacheDAO.insertCache(cache);
            logger.info("Cache entry created successfully: {} by user '{}' - Request ID: {}",
                    cache.getCacheKey(), user.getUsername(), requestId);

            response.setStatus(201);
            sendSuccess(response, "Cache entry created successfully");
        } catch (Exception e) {
            logger.error("Error creating cache entry - Request ID: {}", requestId, e);
            sendError(response, 400, "Invalid cache data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        UserDTO user = (UserDTO) request.getAttribute("user");
        String cacheKey = getPathParameter(request);
        String requestId = UUID.randomUUID().toString();

        if (user == null) {
            logger.warn("CACHE_DENIED: Unauthenticated DELETE to /api/cache/{} - Request ID: {}", cacheKey, requestId);
            sendError(response, 401, "Authentication required");
            return;
        }

        if (!user.hasRole("USER") && !user.hasRole("ADMIN")) {
            logger.warn("CACHE_DENIED: User '{}' (roles: {}) lacks USER role for DELETE - Request ID: {}",
                    user.getUsername(), user.getRoles(), requestId);
            sendError(response, 403, "USER role required");
            return;
        }

        logger.info("CACHE_DELETE: User '{}' deleting cache entry {} - Request ID: {}",
                user.getUsername(), cacheKey, requestId);

        try {
            if (cacheKey == null) {
                logger.warn("Cache key required for deletion - Request ID: {}", requestId);
                sendError(response, 400, "Cache key required");
                return;
            }

            logger.debug("Deleting cache entry: {} - Request ID: {}", cacheKey, requestId);
            cacheDAO.deleteCache(cacheKey);
            logger.info("Cache entry deleted successfully: {} by user '{}' - Request ID: {}",
                    cacheKey, user.getUsername(), requestId);

            sendSuccess(response, "Cache entry deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting cache entry {} - Request ID: {}", cacheKey, requestId, e);
            sendError(response, 500, e.getMessage());
        }
    }
}