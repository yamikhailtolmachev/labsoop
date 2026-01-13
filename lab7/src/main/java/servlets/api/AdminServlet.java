package servlets.api;

import dto.UserDTO;
import dao.UserDAO;
import dao.UserDAOImpl;
import util.JsonUtil;
import util.LogUtil;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class AdminServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UserDTO currentUser = (UserDTO) req.getAttribute("user");

            if (currentUser == null || !currentUser.getRoles().contains("ADMIN")) {
                LogUtil.warn("Unauthorized access attempt to admin endpoint");
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                JsonUtil.writeJson(resp, JsonUtil.error(403, "Admin access required"));
                return;
            }

            String action = req.getParameter("action");

            if ("list".equals(action) || action == null) {
                listUsers(req, resp);
            } else if ("get".equals(action)) {
                getUser(req, resp);
            } else if ("update".equals(action)) {
                updateUser(req, resp);
            } else if ("delete".equals(action)) {
                deleteUser(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, JsonUtil.error(400, "Invalid action"));
            }

        } catch (Exception e) {
            LogUtil.error("Admin operation failed", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            JsonUtil.writeJson(resp, JsonUtil.error(500, "Internal server error"));
        }
    }

    private void listUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        java.util.List<UserDTO> users = userDAO.findAllUsers();
        java.util.List<java.util.Map<String, Object>> userList = new java.util.ArrayList<>();

        for (UserDTO user : users) {
            java.util.Map<String, Object> userMap = new java.util.HashMap<>();
            userMap.put("id", user.getId().toString());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("roles", user.getRoles());
            userMap.put("createdAt", user.getCreatedAt());
            userList.add(userMap);
        }

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("success", true);
        response.put("count", userList.size());
        response.put("users", userList);

        JsonUtil.writeJson(resp, JsonUtil.toJson(response));
        LogUtil.info("Admin listed " + userList.size() + " users");
    }

    private void getUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userId = req.getParameter("userId");

        if (userId == null || userId.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, JsonUtil.error(400, "Missing userId parameter"));
            return;
        }

        try {
            UUID id = UUID.fromString(userId);
            UserDTO user = userDAO.findUserById(id);

            if (user == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.writeJson(resp, JsonUtil.error(404, "User not found"));
                return;
            }

            java.util.Map<String, Object> userMap = new java.util.HashMap<>();
            userMap.put("id", user.getId().toString());
            userMap.put("username", user.getUsername());
            userMap.put("email", user.getEmail());
            userMap.put("roles", user.getRoles());
            userMap.put("createdAt", user.getCreatedAt());
            userMap.put("updatedAt", user.getUpdatedAt());

            JsonUtil.writeJson(resp, JsonUtil.success("User retrieved", userMap));
            LogUtil.info("Admin retrieved user: " + user.getUsername());

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, JsonUtil.error(400, "Invalid user ID format"));
        }
    }

    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userId = req.getParameter("userId");
        String rolesParam = req.getParameter("roles");

        if (userId == null || userId.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, JsonUtil.error(400, "Missing userId parameter"));
            return;
        }

        try {
            UUID id = UUID.fromString(userId);
            UserDTO user = userDAO.findUserById(id);

            if (user == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.writeJson(resp, JsonUtil.error(404, "User not found"));
                return;
            }

            if (rolesParam != null && !rolesParam.isEmpty()) {
                String[] rolesArray = rolesParam.split(",");
                java.util.List<String> roles = new java.util.ArrayList<>();

                for (String role : rolesArray) {
                    String trimmedRole = role.trim();
                    if (!trimmedRole.isEmpty()) {
                        roles.add(trimmedRole);
                    }
                }

                user.setRoles(roles);

                if (userDAO.updateUser(user)) {
                    JsonUtil.writeJson(resp, JsonUtil.success("User roles updated",
                            new java.util.HashMap<String, Object>() {{
                                put("userId", userId);
                                put("roles", roles);
                            }}
                    ));
                    LogUtil.info("Admin updated roles for user: " + user.getUsername() + " to: " + roles);
                } else {
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    JsonUtil.writeJson(resp, JsonUtil.error(500, "Failed to update user"));
                }
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, JsonUtil.error(400, "No roles provided for update"));
            }

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, JsonUtil.error(400, "Invalid user ID format"));
        }
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String userId = req.getParameter("userId");

        if (userId == null || userId.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, JsonUtil.error(400, "Missing userId parameter"));
            return;
        }

        try {
            UUID id = UUID.fromString(userId);
            UserDTO currentUser = (UserDTO) req.getAttribute("user");

            if (currentUser != null && currentUser.getId().equals(id)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, JsonUtil.error(400, "Cannot delete your own account"));
                return;
            }

            UserDTO user = userDAO.findUserById(id);

            if (user == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                JsonUtil.writeJson(resp, JsonUtil.error(404, "User not found"));
                return;
            }

            if (userDAO.deleteUser(id)) {
                JsonUtil.writeJson(resp, JsonUtil.success("User deleted successfully",
                        new java.util.HashMap<String, String>() {{
                            put("userId", userId);
                            put("username", user.getUsername());
                        }}
                ));
                LogUtil.info("Admin deleted user: " + user.getUsername());
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JsonUtil.writeJson(resp, JsonUtil.error(500, "Failed to delete user"));
            }

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, JsonUtil.error(400, "Invalid user ID format"));
        }
    }
}