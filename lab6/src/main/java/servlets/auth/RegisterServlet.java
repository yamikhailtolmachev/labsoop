package servlets.auth;

import dto.UserDTO;
import dao.UserDAO;
import dao.UserDAOImpl;
import util.PasswordUtil;
import util.JsonUtil;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

public class RegisterServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("=== REGISTER SERVLET START ===");

        try {
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            System.out.println("Parameters received:");
            System.out.println("  username: " + username);
            System.out.println("  email: " + email);
            System.out.println("  password: " + (password != null ? "[PROVIDED]" : "null"));

            if (username == null || username.trim().isEmpty()) {
                System.out.println("ERROR: Username is null or empty");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, JsonUtil.error(400, "Username is required"));
                return;
            }

            if (email == null || email.trim().isEmpty()) {
                System.out.println("ERROR: Email is null or empty");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, JsonUtil.error(400, "Email is required"));
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                System.out.println("ERROR: Password is null or empty");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                JsonUtil.writeJson(resp, JsonUtil.error(400, "Password is required"));
                return;
            }

            username = username.trim();
            email = email.trim();
            password = password.trim();

            System.out.println("Checking if user exists: " + username);
            UserDTO existingUser = userDAO.findUserByUsername(username);
            if (existingUser != null) {
                System.out.println("ERROR: User already exists: " + username);
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                JsonUtil.writeJson(resp, JsonUtil.error(409, "Username already exists"));
                return;
            }

            System.out.println("Hashing password...");
            String hashedPassword = PasswordUtil.hashPassword(password);
            System.out.println("Hashed password: " + hashedPassword);

            UserDTO user = new UserDTO();
            UUID userId = UUID.randomUUID();
            System.out.println("Generated UUID: " + userId);
            user.setId(userId);
            user.setUsername(username);
            user.setEmail(email);
            user.setPasswordHash(hashedPassword);
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            List<String> roles = new ArrayList<>();
            roles.add("USER");

            if (username.toLowerCase().contains("admin")) {
                roles.add("ADMIN");
                System.out.println("Added ADMIN role for username containing 'admin'");
            }

            if (username.toLowerCase().contains("api")) {
                roles.add("API_USER");
                System.out.println("Added API_USER role for username containing 'api'");
            }

            user.setRoles(roles);
            System.out.println("User roles set to: " + roles);

            System.out.println("Inserting user into database...");
            UUID insertedId = userDAO.insertUser(user);

            if (insertedId != null) {
                System.out.println("SUCCESS: User inserted with ID: " + insertedId);
                resp.setStatus(HttpServletResponse.SC_CREATED);

                java.util.Map<String, String> data = new java.util.HashMap<>();
                data.put("userId", insertedId.toString());
                data.put("username", username);
                data.put("roles", String.join(",", roles));

                String responseJson = JsonUtil.success("User registered successfully", data);
                System.out.println("Sending response: " + responseJson);
                JsonUtil.writeJson(resp, responseJson);
            } else {
                System.out.println("ERROR: insertUser returned null");
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                JsonUtil.writeJson(resp, JsonUtil.error(500, "Failed to create user in database"));
            }

        } catch (Exception e) {
            System.out.println("EXCEPTION in RegisterServlet:");
            e.printStackTrace(System.out);
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            JsonUtil.writeJson(resp, JsonUtil.error(400, "Registration failed: " + e.getMessage()));
        }

        System.out.println("=== REGISTER SERVLET END ===");
    }
}