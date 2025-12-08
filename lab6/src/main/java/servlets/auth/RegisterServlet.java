package servlets.auth;

import dto.UserDTO;
import dao.UserDAO;
import dao.UserDAOImpl;
import util.PasswordUtil;
import util.JsonUtil;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@WebServlet("/api/register")
public class RegisterServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            logger.info("Registration attempt for username: {}, email: {}", username, email);

            if (username == null || email == null || password == null) {
                logger.warn("Missing required parameters for registration");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, JsonUtil.error(400, "Missing required parameters"));
                return;
            }

            if (userDAO.findUserByUsername(username) != null) {
                logger.warn("Username already exists: {}", username);
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                JsonUtil.writeJson(resp, JsonUtil.error(409, "Username already exists"));
                return;
            }

            logger.debug("Hashing password for user: {}", username);
            String hashedPassword = PasswordUtil.hashPassword(password);

            UserDTO user = new UserDTO();
            user.setId(UUID.randomUUID());
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(hashedPassword);
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            Set<String> roles = new HashSet<>();
            roles.add("USER");
            user.setRoles(roles);

            logger.info("Creating user: {} with roles: {}", username, roles);
            userDAO.insertUser(user);

            logger.debug("User created successfully with ID: {}", user.getId());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            String responseJson = JsonUtil.success("User registered successfully",
                    new java.util.HashMap<String, String>() {{
                        put("userId", user.getId().toString());
                    }}
            );
            JsonUtil.writeJson(resp, responseJson);
            logger.info("Registration completed successfully for user: {}", username);

        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, JsonUtil.error(400, e.getMessage()));
        }
    }
}