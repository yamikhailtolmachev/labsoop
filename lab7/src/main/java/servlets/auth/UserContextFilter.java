package servlets.auth;

import dto.UserDTO;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        UserDTO user = (UserDTO) req.getAttribute("user");
        String path = req.getRequestURI().replace("/math-functions-api", "");

        if (user != null) {
            String logMessage = String.format(
                    "AUTH_LOG: User '%s' (roles: %s) %s %s from %s",
                    user.getUsername(),
                    user.getRoles(),
                    req.getMethod(),
                    path,
                    req.getRemoteAddr()
            );
            System.out.println(logMessage);

            if ("/api/register".equals(path) && "POST".equals(req.getMethod())) {
                String regLog = String.format(
                        "REGISTRATION_LOG: New user registered - username: %s, email: %s, roles: %s",
                        user.getUsername(),
                        user.getEmail(),
                        user.getRoles()
                );
                System.out.println(regLog);
            }
        } else if (!path.equals("/api/register")) {
            String unauthLog = String.format(
                    "AUTH_LOG: Unauthorized attempt to access %s %s from %s",
                    req.getMethod(),
                    path,
                    req.getRemoteAddr()
            );
            System.out.println(unauthLog);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}