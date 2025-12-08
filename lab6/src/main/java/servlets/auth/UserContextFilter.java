package servlets.auth;

import dto.UserDTO;
import dao.UserDAO;
import dao.UserDAOImpl;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@WebFilter("/api/*")
public class UserContextFilter implements Filter {
    private final UserDAO userDAO = new UserDAOImpl();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String authHeader = req.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            try {
                String base64Credentials = authHeader.substring("Basic ".length());
                String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
                String[] parts = credentials.split(":", 2);

                if (parts.length == 2) {
                    String username = parts[0];
                    UserDTO user = userDAO.findUserByUsername(username);
                    if (user != null) {
                        req.setAttribute("user", user);
                    }
                }
            } catch (Exception e) {
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void destroy() {}
}