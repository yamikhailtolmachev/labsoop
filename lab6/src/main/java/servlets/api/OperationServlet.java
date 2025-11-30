package servlets.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import dao.OperationDAO;
import dao.OperationDAOImpl;
import dto.OperationDTO;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/operations/*")
public class OperationServlet extends HttpServlet {
    private OperationDAO operationDAO = new OperationDAOImpl();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                String userIdParam = request.getParameter("userId");
                String operationType = request.getParameter("type");
                String functionIdParam = request.getParameter("functionId");
                String sortBy = request.getParameter("sortBy");
                String sortOrder = request.getParameter("sortOrder");

                UUID userId = userIdParam != null ? UUID.fromString(userIdParam) : null;
                UUID functionId = functionIdParam != null ? UUID.fromString(functionIdParam) : null;

                if (userId != null || operationType != null || functionId != null) {
                    var operations = operationDAO.findOperationsByMultipleCriteria(userId, operationType, functionId, sortBy, sortOrder);
                    sendSuccess(response, operations);
                } else {
                    var operations = operationDAO.findAllOperations();
                    sendSuccess(response, operations);
                }
            } else if (pathInfo.equals("/recent")) {
                String userIdParam = request.getParameter("userId");
                int days = getIntParameter(request, "days", 7);

                if (userIdParam == null) {
                    sendError(response, 400, "userId parameter required");
                    return;
                }

                var operations = operationDAO.findRecentOperations(UUID.fromString(userIdParam), days);
                sendSuccess(response, operations);
            } else if (pathInfo.equals("/chain")) {
                String functionId = request.getParameter("functionId");
                if (functionId == null) {
                    sendError(response, 400, "functionId parameter required");
                    return;
                }
                var operations = operationDAO.findOperationChainDepthFirst(UUID.fromString(functionId));
                sendSuccess(response, operations);
            } else if (pathInfo.equals("/hierarchy")) {
                String rootFunctionId = request.getParameter("rootFunctionId");
                if (rootFunctionId == null) {
                    sendError(response, 400, "rootFunctionId parameter required");
                    return;
                }
                var operations = operationDAO.findOperationsByFunctionHierarchy(UUID.fromString(rootFunctionId));
                sendSuccess(response, operations);
            } else {
                String operationId = pathInfo.substring(1);
                OperationDTO operation = operationDAO.findOperationById(UUID.fromString(operationId));
                if (operation != null) {
                    sendSuccess(response, operation);
                } else {
                    sendError(response, 404, "Operation not found");
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
            OperationDTO operation = mapper.readValue(request.getReader(), OperationDTO.class);
            UUID operationId = operationDAO.insertOperation(operation);

            Map<String, Object> result = new HashMap<>();
            result.put("operationId", operationId);
            result.put("message", "Operation created successfully");

            response.setStatus(201);
            sendSuccess(response, result);
        } catch (Exception e) {
            sendError(response, 400, "Invalid operation data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                sendError(response, 400, "Operation ID required");
                return;
            }

            String operationId = pathInfo.substring(1);
            operationDAO.deleteOperation(UUID.fromString(operationId));

            Map<String, String> result = new HashMap<>();
            result.put("message", "Operation deleted successfully");
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