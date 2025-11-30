package servlets.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import dao.FunctionDAO;
import dao.FunctionDAOImpl;
import dto.FunctionDTO;
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/functions/*")
public class FunctionServlet extends HttpServlet {
    private FunctionDAO functionDAO = new FunctionDAOImpl();
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                String userIdParam = request.getParameter("userId");
                String namePattern = request.getParameter("name");
                String type = request.getParameter("type");
                String minLeftBound = request.getParameter("minLeftBound");
                String maxRightBound = request.getParameter("maxRightBound");
                String sortBy = request.getParameter("sortBy");
                String sortOrder = request.getParameter("sortOrder");

                UUID userId = userIdParam != null ? UUID.fromString(userIdParam) : null;
                Double minLeft = minLeftBound != null ? Double.parseDouble(minLeftBound) : null;
                Double maxRight = maxRightBound != null ? Double.parseDouble(maxRightBound) : null;

                if (userId != null || namePattern != null || type != null || minLeft != null || maxRight != null) {
                    var functions = functionDAO.findFunctionsByMultipleCriteria(userId, namePattern, type, minLeft, maxRight, sortBy, sortOrder);
                    sendSuccess(response, functions);
                } else {
                    var functions = functionDAO.findAllFunctions();
                    sendSuccess(response, functions);
                }
            } else if (pathInfo.equals("/types")) {
                String[] types = {"ARRAY_TABULATED", "LINKED_LIST_TABULATED"};
                sendSuccess(response, types);
            } else if (pathInfo.equals("/derivatives")) {
                String rootFunctionId = request.getParameter("rootFunctionId");
                if (rootFunctionId == null) {
                    sendError(response, 400, "rootFunctionId parameter required");
                    return;
                }
                var derivatives = functionDAO.findFunctionDerivatives(UUID.fromString(rootFunctionId));
                sendSuccess(response, derivatives);
            } else if (pathInfo.equals("/highest-points")) {
                int limit = getIntParameter(request, "limit", 10);
                var functions = functionDAO.findFunctionsWithHighestPointCount(limit);
                sendSuccess(response, functions);
            } else {
                String functionId = pathInfo.substring(1);
                FunctionDTO function = functionDAO.findFunctionById(UUID.fromString(functionId));
                if (function != null) {
                    sendSuccess(response, function);
                } else {
                    sendError(response, 404, "Function not found");
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
            FunctionDTO function = mapper.readValue(request.getReader(), FunctionDTO.class);
            UUID functionId = functionDAO.insertFunction(function);

            Map<String, Object> result = new HashMap<>();
            result.put("functionId", functionId);
            result.put("message", "Function created successfully");

            response.setStatus(201);
            sendSuccess(response, result);
        } catch (Exception e) {
            sendError(response, 400, "Invalid function data: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                sendError(response, 400, "Function ID required");
                return;
            }

            String functionId = pathInfo.substring(1);
            FunctionDTO function = mapper.readValue(request.getReader(), FunctionDTO.class);
            function.setId(UUID.fromString(functionId));

            functionDAO.updateFunction(function);

            Map<String, String> result = new HashMap<>();
            result.put("message", "Function updated successfully");
            sendSuccess(response, result);
        } catch (Exception e) {
            sendError(response, 400, "Invalid update data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.length() <= 1) {
                sendError(response, 400, "Function ID required");
                return;
            }

            String functionId = pathInfo.substring(1);
            functionDAO.deleteFunction(UUID.fromString(functionId));

            Map<String, String> result = new HashMap<>();
            result.put("message", "Function deleted successfully");
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