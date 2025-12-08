package servlets.api;

import dto.UserDTO;
import dao.UserDAO;
import dao.UserDAOImpl;
import util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/admin/users")
public class AdminServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AdminServlet.class);
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO currentUser = (UserDTO) req.getAttribute("user");

        if (currentUser == null) {
            logger.warn("Unauthenticated access attempt to admin endpoint");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            JsonUtil.writeJson(resp, JsonUtil.error(403, "Authentication required"));
            return;
        }

        logger.info("Admin endpoint accessed by user: {}", currentUser.getUsername());

        if (!currentUser.getRoles().contains("ADMIN")) {
            logger.warn("Unauthorized access attempt to admin endpoint by non-admin user: {}",
                    currentUser.getUsername());
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            JsonUtil.writeJson(resp, JsonUtil.error(403, "Admin access required"));
            return;
        }

        logger.debug("Retrieving all users for admin: {}", currentUser.getUsername());
        var users = userDAO.findAllUsers();

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("users", users);

        logger.info("Admin request completed successfully, returned {} users", users.size());
        JsonUtil.writeJson(resp, JsonUtil.success("Users retrieved successfully", responseData));
    }
}