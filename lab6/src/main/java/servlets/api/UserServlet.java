package servlets.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import dao.UserDAO;
import dao.UserDAOImpl;
import dto.UserDTO;
import java.io.IOException;
import java.util.UUID;
import java.util.Map;

@WebServlet("/api/users/*")
public class UserServlet extends BaseApiServlet {
    private UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                String usernamePattern = request.getParameter("username");
                String emailPattern = request.getParameter("email");
                String sortBy = request.getParameter("sortBy");
                String sortOrder = request.getParameter("sortOrder");

                if (usernamePattern != null || emailPattern != null) {
                    var users = userDAO.findUsersByMultipleCriteria(usernamePattern, emailPattern, sortBy, sortOrder);
                    sendSuccess(response, users);
                } else {
                    var users = userDAO.findAllUsers();
                    sendSuccess(response, users);
                }
            } else if (pathInfo.equals("/recent")) {
                int days = getIntParameter(request, "days", 7);
                var users = userDAO.findRecentUsers(days);
                sendSuccess(response, users);
            } else {
                String userId = getPathParameter(request);
                UserDTO user = userDAO.findUserById(UUID.fromString(userId));
                if (user != null) {
                    sendSuccess(response, user);
                } else {
                    sendError(response, 404, "User not found");
                }
            }
        } catch (Exception e) {
            logger.error("Error in GET /api/users", e);
            sendError(response, 500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            UserDTO user = readRequestBody(request, UserDTO.class);
            UUID userId = userDAO.insertUser(user);

            response.setStatus(201);
            sendSuccess(response, "User created successfully", Map.of("userId", userId));
        } catch (Exception e) {
            logger.error("Error in POST /api/users", e);
            sendError(response, 400, "Invalid user data: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String userId = getPathParameter(request);
            if (userId == null) {
                sendError(response, 400, "User ID required");
                return;
            }

            UserDTO user = readRequestBody(request, UserDTO.class);
            user.setId(UUID.fromString(userId));
            userDAO.updateUser(user);

            sendSuccess(response, "User updated successfully");
        } catch (Exception e) {
            logger.error("Error in PUT /api/users", e);
            sendError(response, 400, "Invalid update data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String userId = getPathParameter(request);
            if (userId == null) {
                sendError(response, 400, "User ID required");
                return;
            }

            userDAO.deleteUser(UUID.fromString(userId));
            sendSuccess(response, "User deleted successfully");
        } catch (Exception e) {
            logger.error("Error in DELETE /api/users", e);
            sendError(response, 500, e.getMessage());
        }
    }
}