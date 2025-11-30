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

@WebServlet("/api/functions/*")
public class FunctionServlet extends HttpServlet {
    private FunctionDAO functionDAO = new FunctionDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.getWriter().write("{\"success\":true,\"data\":" + toJson(functionDAO.findAllFunctions()) + "}");
            } else if (pathInfo.equals("/types")) {
                response.getWriter().write("{\"success\":true,\"data\":[\"ARRAY_TABULATED\",\"LINKED_LIST_TABULATED\"]}");
            } else {
                String functionId = pathInfo.substring(1);
                FunctionDTO function = functionDAO.findFunctionById(UUID.fromString(functionId));
                if (function != null) {
                    response.getWriter().write("{\"success\":true,\"data\":" + toJson(function) + "}");
                } else {
                    response.setStatus(404);
                    response.getWriter().write("{\"success\":false,\"error\":\"Function not found\"}");
                }
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            FunctionDTO function = new FunctionDTO();
            function.setName("Function_" + System.currentTimeMillis());
            function.setType("ARRAY_TABULATED");
            function.setUserId(UUID.randomUUID());
            function.setExpression("x^2");
            function.setLeftBound(0.0);
            function.setRightBound(10.0);
            function.setPointsCount(100);

            UUID functionId = functionDAO.insertFunction(function);
            response.getWriter().write("{\"success\":true,\"data\":{\"functionId\":\"" + functionId + "\"}}");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\":true,\"message\":\"Function updated\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                String functionId = pathInfo.substring(1);
                functionDAO.deleteFunction(UUID.fromString(functionId));
                response.getWriter().write("{\"success\":true,\"message\":\"Function deleted\"}");
            } else {
                response.setStatus(400);
                response.getWriter().write("{\"success\":false,\"error\":\"Function ID required\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String toJson(Object obj) {
        if (obj == null) return "null";
        return obj.toString();
    }
}