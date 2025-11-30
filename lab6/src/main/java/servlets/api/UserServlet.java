package servlets.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
package servlets.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import dao.UserDAO;
import dao.UserDAOImpl;
import dto.UserDTO;
import java.util.UUID;

@WebServlet("/api/users/*")
public class UserServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                response.getWriter().write("{\"success\":true,\"data\":" + toJson(userDAO.findAllUsers()) + "}");
            } else {
                String userId = pathInfo.substring(1);
                UserDTO user = userDAO.findUserById(UUID.fromString(userId));
                if (user != null) {
                    response.getWriter().write("{\"success\":true,\"data\":" + toJson(user) + "}");
                } else {
                    response.setStatus(404);
                    response.getWriter().write("{\"success\":false,\"error\":\"User not found\"}");
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
            UserDTO user = new UserDTO();
            user.setUsername("user_" + System.currentTimeMillis());
            user.setEmail("user@example.com");
            user.setPasswordHash("temp_hash");

            UUID userId = userDAO.insertUser(user);
            response.getWriter().write("{\"success\":true,\"data\":{\"userId\":\"" + userId + "\"}}");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"success\":true,\"message\":\"User updated\"}");
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                String userId = pathInfo.substring(1);
                userDAO.deleteUser(UUID.fromString(userId));
                response.getWriter().write("{\"success\":true,\"message\":\"User deleted\"}");
            } else {
                response.setStatus(400);
                response.getWriter().write("{\"success\":false,\"error\":\"User ID required\"}");
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