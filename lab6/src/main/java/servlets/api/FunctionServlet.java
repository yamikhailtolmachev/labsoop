package servlets.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.FunctionDAO;
import dao.FunctionDAOImpl;
import dto.FunctionDTO;
import java.io.IOException;
import java.util.UUID;
import java.util.Map;

@WebServlet("/api/functions/*")
public class FunctionServlet extends BaseApiServlet {
    private FunctionDAO functionDAO = new FunctionDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String requestId = UUID.randomUUID().toString();

        logger.info("GET {} - Request ID: {}", pathInfo != null ? pathInfo : "/", requestId);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllFunctions(request, response, requestId);
            } else if (pathInfo.equals("/search")) {
                handleSearchFunctions(request, response, requestId);
            } else if (pathInfo.equals("/types")) {
                handleGetFunctionTypes(response, requestId);
            } else if (pathInfo.equals("/derivatives")) {
                handleGetDerivatives(request, response, requestId);
            } else if (pathInfo.equals("/highest-points")) {
                handleHighestPointFunctions(request, response, requestId);
            } else {
                handleGetFunctionById(request, response, pathInfo, requestId);
            }
        } catch (Exception e) {
            logger.error("Error processing GET request {} - ID: {}", pathInfo, requestId, e);
            sendError(response, 500, "Internal server error");
        }
    }

    private void handleGetAllFunctions(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        logger.debug("Fetching all functions - Request ID: {}", requestId);
        var functions = functionDAO.findAllFunctions();
        logger.info("Retrieved {} functions - Request ID: {}", functions.size(), requestId);
        sendSuccess(response, functions);
    }

    private void handleSearchFunctions(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        UUID userId = getUuidParameter(request, "userId");
        String namePattern = request.getParameter("name");
        String type = request.getParameter("type");
        Double minLeftBound = getDoubleParameter(request, "minLeftBound");
        Double maxRightBound = getDoubleParameter(request, "maxRightBound");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");

        logger.debug("Searching functions - user: {}, name: {}, type: {} - Request ID: {}",
                userId, namePattern, type, requestId);

        var functions = functionDAO.findFunctionsByMultipleCriteria(userId, namePattern, type,
                minLeftBound, maxRightBound, sortBy, sortOrder);
        logger.info("Found {} functions matching criteria - Request ID: {}", functions.size(), requestId);
        sendSuccess(response, functions);
    }

    private void handleGetFunctionTypes(HttpServletResponse response, String requestId) throws IOException {
        logger.debug("Fetching function types - Request ID: {}", requestId);
        String[] types = {"ARRAY_TABULATED", "LINKED_LIST_TABULATED"};
        logger.info("Returning {} function types - Request ID: {}", types.length, requestId);
        sendSuccess(response, types);
    }

    private void handleGetDerivatives(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        UUID rootFunctionId = getUuidParameter(request, "rootFunctionId");
        if (rootFunctionId == null) {
            logger.warn("rootFunctionId parameter required - Request ID: {}", requestId);
            sendError(response, 400, "rootFunctionId parameter required");
            return;
        }

        logger.debug("Fetching derivatives for function: {} - Request ID: {}", rootFunctionId, requestId);
        var derivatives = functionDAO.findFunctionDerivatives(rootFunctionId);
        logger.info("Found {} derivatives for function {} - Request ID: {}", derivatives.size(), rootFunctionId, requestId);
        sendSuccess(response, derivatives);
    }

    private void handleHighestPointFunctions(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        int limit = getIntParameter(request, "limit", 10);
        logger.debug("Fetching {} functions with highest point count - Request ID: {}", limit, requestId);

        var functions = functionDAO.findFunctionsWithHighestPointCount(limit);
        logger.info("Found {} functions with highest point count - Request ID: {}", functions.size(), requestId);
        sendSuccess(response, functions);
    }

    private void handleGetFunctionById(HttpServletRequest request, HttpServletResponse response, String pathInfo, String requestId) throws IOException {
        String functionId = pathInfo.substring(1);
        logger.debug("Fetching function by ID: {} - Request ID: {}", functionId, requestId);

        FunctionDTO function = functionDAO.findFunctionById(UUID.fromString(functionId));
        if (function != null) {
            logger.info("Function found: {} - Request ID: {}", function.getName(), requestId);
            sendSuccess(response, function);
        } else {
            logger.warn("Function not found: {} - Request ID: {}", functionId, requestId);
            sendError(response, 404, "Function not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestId = UUID.randomUUID().toString();
        logger.info("POST /api/functions - Request ID: {}", requestId);

        try {
            FunctionDTO function = readRequestBody(request, FunctionDTO.class);
            logger.debug("Creating function: {} - Request ID: {}", function.getName(), requestId);

            UUID functionId = functionDAO.insertFunction(function);
            logger.info("Function created successfully: {} - Request ID: {}", functionId, requestId);

            response.setStatus(201);
            sendSuccess(response, "Function created successfully", Map.of("functionId", functionId));
        } catch (Exception e) {
            logger.error("Error creating function - Request ID: {}", requestId, e);
            sendError(response, 400, "Invalid function data: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String functionId = getPathParameter(request);
        String requestId = UUID.randomUUID().toString();
        logger.info("PUT /api/functions/{} - Request ID: {}", functionId, requestId);

        try {
            if (functionId == null) {
                logger.warn("Function ID required for update - Request ID: {}", requestId);
                sendError(response, 400, "Function ID required");
                return;
            }

            FunctionDTO function = readRequestBody(request, FunctionDTO.class);
            function.setId(UUID.fromString(functionId));
            logger.debug("Updating function: {} - Request ID: {}", functionId, requestId);

            functionDAO.updateFunction(function);
            logger.info("Function updated successfully: {} - Request ID: {}", functionId, requestId);

            sendSuccess(response, "Function updated successfully");
        } catch (Exception e) {
            logger.error("Error updating function {} - Request ID: {}", functionId, requestId, e);
            sendError(response, 400, "Invalid update data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String functionId = getPathParameter(request);
        String requestId = UUID.randomUUID().toString();
        logger.info("DELETE /api/functions/{} - Request ID: {}", functionId, requestId);

        try {
            if (functionId == null) {
                logger.warn("Function ID required for deletion - Request ID: {}", requestId);
                sendError(response, 400, "Function ID required");
                return;
            }

            logger.debug("Deleting function: {} - Request ID: {}", functionId, requestId);
            functionDAO.deleteFunction(UUID.fromString(functionId));
            logger.info("Function deleted successfully: {} - Request ID: {}", functionId, requestId);

            sendSuccess(response, "Function deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting function {} - Request ID: {}", functionId, requestId, e);
            sendError(response, 500, e.getMessage());
        }
    }

    private Double getDoubleParameter(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (value != null) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}