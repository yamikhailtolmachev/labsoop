package servlets.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.OperationDAO;
import dao.OperationDAOImpl;
import dto.OperationDTO;
import java.io.IOException;
import java.util.UUID;
import java.util.Map;

@WebServlet("/api/operations/*")
public class OperationServlet extends BaseApiServlet {
    private OperationDAO operationDAO = new OperationDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String pathInfo = request.getPathInfo();
        String requestId = UUID.randomUUID().toString();

        logger.info("GET {} - Request ID: {}", pathInfo != null ? pathInfo : "/", requestId);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllOperations(request, response, requestId);
            } else if (pathInfo.equals("/search")) {
                handleSearchOperations(request, response, requestId);
            } else if (pathInfo.equals("/recent")) {
                handleRecentOperations(request, response, requestId);
            } else if (pathInfo.equals("/chain")) {
                handleOperationChain(request, response, requestId);
            } else if (pathInfo.equals("/hierarchy")) {
                handleOperationHierarchy(request, response, requestId);
            } else {
                handleGetOperationById(request, response, pathInfo, requestId);
            }
        } catch (Exception e) {
            logger.error("Error processing GET request {} - ID: {}", pathInfo, requestId, e);
            sendError(response, 500, "Internal server error");
        }
    }

    private void handleGetAllOperations(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        logger.debug("Fetching all operations - Request ID: {}", requestId);
        var operations = operationDAO.findAllOperations();
        logger.info("Retrieved {} operations - Request ID: {}", operations.size(), requestId);
        sendSuccess(response, operations);
    }

    private void handleSearchOperations(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        UUID userId = getUuidParameter(request, "userId");
        String operationType = request.getParameter("type");
        UUID functionId = getUuidParameter(request, "functionId");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");

        logger.debug("Searching operations - user: {}, type: {}, function: {} - Request ID: {}",
                userId, operationType, functionId, requestId);

        var operations = operationDAO.findOperationsByMultipleCriteria(userId, operationType, functionId, sortBy, sortOrder);
        logger.info("Found {} operations matching criteria - Request ID: {}", operations.size(), requestId);
        sendSuccess(response, operations);
    }

    private void handleRecentOperations(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        UUID userId = getUuidParameter(request, "userId");
        int days = getIntParameter(request, "days", 7);

        if (userId == null) {
            logger.warn("userId parameter required for recent operations - Request ID: {}", requestId);
            sendError(response, 400, "userId parameter required");
            return;
        }

        logger.debug("Fetching recent operations for user: {}, days: {} - Request ID: {}", userId, days, requestId);
        var operations = operationDAO.findRecentOperations(userId, days);
        logger.info("Found {} recent operations for user {} - Request ID: {}", operations.size(), userId, requestId);
        sendSuccess(response, operations);
    }

    private void handleOperationChain(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        UUID functionId = getUuidParameter(request, "functionId");
        if (functionId == null) {
            logger.warn("functionId parameter required for operation chain - Request ID: {}", requestId);
            sendError(response, 400, "functionId parameter required");
            return;
        }

        logger.debug("Fetching operation chain for function: {} - Request ID: {}", functionId, requestId);
        var operations = operationDAO.findOperationChainDepthFirst(functionId);
        logger.info("Found {} operations in chain for function {} - Request ID: {}", operations.size(), functionId, requestId);
        sendSuccess(response, operations);
    }

    private void handleOperationHierarchy(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        UUID rootFunctionId = getUuidParameter(request, "rootFunctionId");
        if (rootFunctionId == null) {
            logger.warn("rootFunctionId parameter required for operation hierarchy - Request ID: {}", requestId);
            sendError(response, 400, "rootFunctionId parameter required");
            return;
        }

        logger.debug("Fetching operation hierarchy for root function: {} - Request ID: {}", rootFunctionId, requestId);
        var operations = operationDAO.findOperationsByFunctionHierarchy(rootFunctionId);
        logger.info("Found {} operations in hierarchy for root function {} - Request ID: {}", operations.size(), rootFunctionId, requestId);
        sendSuccess(response, operations);
    }

    private void handleGetOperationById(HttpServletRequest request, HttpServletResponse response, String pathInfo, String requestId) throws IOException {
        String operationId = pathInfo.substring(1);
        logger.debug("Fetching operation by ID: {} - Request ID: {}", operationId, requestId);

        OperationDTO operation = operationDAO.findOperationById(UUID.fromString(operationId));
        if (operation != null) {
            logger.info("Operation found: {} - Request ID: {}", operation.getOperationType(), requestId);
            sendSuccess(response, operation);
        } else {
            logger.warn("Operation not found: {} - Request ID: {}", operationId, requestId);
            sendError(response, 404, "Operation not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestId = UUID.randomUUID().toString();
        logger.info("POST /api/operations - Request ID: {}", requestId);

        try {
            OperationDTO operation = readRequestBody(request, OperationDTO.class);
            logger.debug("Creating operation: {} - Request ID: {}", operation.getOperationType(), requestId);

            UUID operationId = operationDAO.insertOperation(operation);
            logger.info("Operation created successfully: {} - Request ID: {}", operationId, requestId);

            response.setStatus(201);
            sendSuccess(response, "Operation created successfully", Map.of("operationId", operationId));
        } catch (Exception e) {
            logger.error("Error creating operation - Request ID: {}", requestId, e);
            sendError(response, 400, "Invalid operation data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String operationId = getPathParameter(request);
        String requestId = UUID.randomUUID().toString();
        logger.info("DELETE /api/operations/{} - Request ID: {}", operationId, requestId);

        try {
            if (operationId == null) {
                logger.warn("Operation ID required for deletion - Request ID: {}", requestId);
                sendError(response, 400, "Operation ID required");
                return;
            }

            logger.debug("Deleting operation: {} - Request ID: {}", operationId, requestId);
            operationDAO.deleteOperation(UUID.fromString(operationId));
            logger.info("Operation deleted successfully: {} - Request ID: {}", operationId, requestId);

            sendSuccess(response, "Operation deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting operation {} - Request ID: {}", operationId, requestId, e);
            sendError(response, 500, e.getMessage());
        }
    }
}