package servlets.api;

import dto.UserDTO;
import util.JsonUtil;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class FunctionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO user = (UserDTO) req.getAttribute("user");
        String logMsg = String.format("DATA_ACCESS: User '%s' viewing functions",
                user != null ? user.getUsername() : "unknown");
        System.out.println(logMsg);

        JsonUtil.writeJson(resp, JsonUtil.success("Functions retrieved",
                java.util.Arrays.asList("sin(x)", "cos(x)", "x^2")));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserDTO user = (UserDTO) req.getAttribute("user");

        if (user != null && user.hasRole("USER")) {
            String logMsg = String.format("DATA_CREATE: User '%s' creating function",
                    user.getUsername());
            System.out.println(logMsg);

            JsonUtil.writeJson(resp, JsonUtil.success("Function created",
                    java.util.Map.of("id", "123", "name", "new function")));
        } else {
            String logMsg = String.format("ACCESS_DENIED: User '%s' denied function creation",
                    user != null ? user.getUsername() : "anonymous");
            System.out.println(logMsg);

            resp.setStatus(403);
            JsonUtil.writeJson(resp, JsonUtil.error(403, "USER role required"));
        }
    }
}