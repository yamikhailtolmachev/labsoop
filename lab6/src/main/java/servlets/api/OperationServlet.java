package servlets.api;

import dto.UserDTO;
import util.JsonUtil;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OperationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO user = (UserDTO) req.getAttribute("user");
        String logMsg = String.format("DATA_ACCESS: User '%s' viewing operations",
                user != null ? user.getUsername() : "unknown");
        System.out.println(logMsg);

        JsonUtil.writeJson(resp, JsonUtil.success("Operations retrieved",
                java.util.Arrays.asList("add", "subtract", "multiply")));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO user = (UserDTO) req.getAttribute("user");

        if (user != null && user.hasRole("ADMIN")) {
            String logMsg = String.format("ADMIN_OPERATION: User '%s' creating operation",
                    user.getUsername());
            System.out.println(logMsg);

            JsonUtil.writeJson(resp, JsonUtil.success("Operation created",
                    java.util.Map.of("id", "456", "type", "new operation")));
        } else {
            String logMsg = String.format("ACCESS_DENIED: User '%s' denied operation creation",
                    user != null ? user.getUsername() : "anonymous");
            System.out.println(logMsg);

            resp.setStatus(403);
            JsonUtil.writeJson(resp, JsonUtil.error(403, "ADMIN role required"));
        }
    }
}