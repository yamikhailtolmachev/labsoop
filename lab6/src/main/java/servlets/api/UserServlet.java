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
        String pathInfo = request.getPathInfo();
        String requestId = UUID.randomUUID().toString();

        logger.info("GET {} - Request ID: {}", pathInfo != null ? pathInfo : "/", requestId);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                handleGetAllUsers(request, response, requestId);
            } else if (pathInfo.equals("/search")) {
                handleSearchUsers(request, response, requestId);
            } else if (pathInfo.equals("/recent")) {
                handleRecentUsers(request, response, requestId);
            } else {
                handleGetUserById(request, response, pathInfo, requestId);
            }
        } catch (Exception e) {
            logger.error("Error processing GET request {} - ID: {}", pathInfo, requestId, e);
            sendError(response, 500, "Internal server error");
        }
    }

    private void handleGetAllUsers(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        logger.debug("Fetching all users - Request ID: {}", requestId);
        var users = userDAO.findAllUsers();
        logger.info("Retrieved {} users - Request ID: {}", users.size(), requestId);
        sendSuccess(response, users);
    }

    private void handleSearchUsers(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        String usernamePattern = request.getParameter("username");
        String emailPattern = request.getParameter("email");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");

        logger.debug("Searching users - username: {}, email: {} - Request ID: {}",
                usernamePattern, emailPattern, requestId);

        var users = userDAO.findUsersByMultipleCriteria(usernamePattern, emailPattern, sortBy, sortOrder);
        logger.info("Found {} users matching criteria - Request ID: {}", users.size(), requestId);
        sendSuccess(response, users);
    }

    private void handleRecentUsers(HttpServletRequest request, HttpServletResponse response, String requestId) throws IOException {
        int days = getIntParameter(request, "days", 7);
        logger.debug("Fetching recent users from last {} days - Request ID: {}", days, requestId);

        var users = userDAO.findRecentUsers(days);
        logger.info("Found {} recent users - Request ID: {}", users.size(), requestId);
        sendSuccess(response, users);
    }

    private void handleGetUserById(HttpServletRequest request, HttpServletResponse response, String pathInfo, String requestId) throws IOException {
        String userId = pathInfo.substring(1);
        logger.debug("Fetching user by ID: {} - Request ID: {}", userId, requestId);

        UserDTO user = userDAO.findUserById(UUID.fromString(userId));
        if (user != null) {
            logger.info("User found: {} - Request ID: {}", user.getUsername(), requestId);
            sendSuccess(response, user);
        } else {
            logger.warn("User not found: {} - Request ID: {}", userId, requestId);
            sendError(response, 404, "User not found");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestId = UUID.randomUUID().toString();
        logger.info("POST /api/users - Request ID: {}", requestId);

        try {
            UserDTO user = readRequestBody(request, UserDTO.class);
            logger.debug("Creating user: {} - Request ID: {}", user.getUsername(), requestId);

            UUID userId = userDAO.insertUser(user);
            logger.info("User created successfully: {} - Request ID: {}", userId, requestId);

            response.setStatus(201);
            sendSuccess(response, "User created successfully", Map.of("userId", userId));
        } catch (Exception e) {
            logger.error("Error creating user - Request ID: {}", requestId, e);
            sendError(response, 400, "Invalid user data: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = getPathParameter(request);
        String requestId = UUID.randomUUID().toString();
        logger.info("PUT /api/users/{} - Request ID: {}", userId, requestId);

        try {
            if (userId == null) {
                logger.warn("User ID required for update - Request ID: {}", requestId);
                sendError(response, 400, "User ID required");
                return;
            }

            UserDTO user = readRequestBody(request, UserDTO.class);
            user.setId(UUID.fromString(userId));
            logger.debug("Updating user: {} - Request ID: {}", userId, requestId);

            userDAO.updateUser(user);
            logger.info("User updated successfully: {} - Request ID: {}", userId, requestId);

            sendSuccess(response, "User updated successfully");
        } catch (Exception e) {
            logger.error("Error updating user {} - Request ID: {}", userId, requestId, e);
            sendError(response, 400, "Invalid update data: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String userId = getPathParameter(request);
        String requestId = UUID.randomUUID().toString();
        logger.info("DELETE /api/users/{} - Request ID: {}", userId, requestId);

        try {
            if (userId == null) {
                logger.warn("User ID required for deletion - Request ID: {}", requestId);
                sendError(response, 400, "User ID required");
                return;
            }

            logger.debug("Deleting user: {} - Request ID: {}", userId, requestId);
            userDAO.deleteUser(UUID.fromString(userId));
            logger.info("User deleted successfully: {} - Request ID: {}", userId, requestId);

            sendSuccess(response, "User deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting user {} - Request ID: {}", userId, requestId, e);
            sendError(response, 500, e.getMessage());
        }
    }
}