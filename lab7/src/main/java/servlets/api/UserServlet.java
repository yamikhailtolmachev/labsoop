package servlets.api;

import dto.UserDTO;
import dao.UserDAO;
import dao.UserDAOImpl;
import util.JsonUtil;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class UserServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO currentUser = (UserDTO) req.getAttribute("user");
        if (currentUser != null) {
            String logMsg = String.format("USER_OPERATION: User '%s' viewing users list/details",
                    currentUser.getUsername());
            System.out.println(logMsg);
        }

        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            listUsers(req, resp);
        } else {
            getUserById(pathInfo.substring(1), req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO currentUser = (UserDTO) req.getAttribute("user");
        if (currentUser != null && currentUser.hasRole("ADMIN")) {
            String logMsg = String.format("ADMIN_OPERATION: User '%s' creating new user",
                    currentUser.getUsername());
            System.out.println(logMsg);
            createUser(req, resp);
        } else {
            String logMsg = String.format("ACCESS_DENIED: User '%s' denied user creation",
                    currentUser != null ? currentUser.getUsername() : "anonymous");
            System.out.println(logMsg);
            resp.setStatus(403);
            JsonUtil.writeJson(resp, JsonUtil.error(403, "Admin access required"));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO currentUser = (UserDTO) req.getAttribute("user");
        if (currentUser != null && currentUser.hasRole("ADMIN")) {
            String logMsg = String.format("ADMIN_OPERATION: User '%s' updating user",
                    currentUser.getUsername());
            System.out.println(logMsg);
            updateUser(req, resp);
        } else {
            resp.setStatus(403);
            JsonUtil.writeJson(resp, JsonUtil.error(403, "Admin access required"));
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO currentUser = (UserDTO) req.getAttribute("user");
        if (currentUser != null && currentUser.hasRole("ADMIN")) {
            String logMsg = String.format("ADMIN_OPERATION: User '%s' deleting user",
                    currentUser.getUsername());
            System.out.println(logMsg);
            deleteUser(req, resp);
        } else {
            resp.setStatus(403);
            JsonUtil.writeJson(resp, JsonUtil.error(403, "Admin access required"));
        }
    }

    private void listUsers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var users = userDAO.findAllUsers();
        JsonUtil.writeJson(resp, JsonUtil.success("Users retrieved", users));
    }

    private void getUserById(String id, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            UUID userId = UUID.fromString(id);
            UserDTO user = userDAO.findUserById(userId);
            if (user != null) {
                JsonUtil.writeJson(resp, JsonUtil.success("User retrieved", user));
            } else {
                resp.setStatus(404);
                JsonUtil.writeJson(resp, JsonUtil.error(404, "User not found"));
            }
        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            JsonUtil.writeJson(resp, JsonUtil.error(400, "Invalid user ID format"));
        }
    }

    private void createUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(501);
        JsonUtil.writeJson(resp, JsonUtil.error(501, "Not implemented"));
    }

    private void updateUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(501);
        JsonUtil.writeJson(resp, JsonUtil.error(501, "Not implemented"));
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setStatus(501);
        JsonUtil.writeJson(resp, JsonUtil.error(501, "Not implemented"));
    }
}