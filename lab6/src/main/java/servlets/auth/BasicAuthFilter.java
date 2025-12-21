package servlets.auth;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.HashSet;
import dao.UserDAO;
import dao.UserDAOImpl;
import dto.UserDTO;
import util.PasswordUtil;

public class BasicAuthFilter implements Filter {
    private UserDAO userDAO;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userDAO = new UserDAOImpl();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String uri = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();

        String path = uri.replace("/math-functions-api", "");

        if (path.equals("/api/register") && method.equals("POST")) {
            chain.doFilter(request, response);
            return;
        }

        if (!path.contains("/api/")) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            String logMsg = String.format("AUTH_FAIL: No auth header for %s %s from %s",
                    method, path, httpRequest.getRemoteAddr());
            System.out.println(logMsg);

            httpResponse.setStatus(401);
            httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"Math Functions API\"");
            sendJsonResponse(httpResponse, "{\"error\":\"Unauthorized\"}");
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length()).trim();
        String credentials;

        try {
            credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            String logMsg = String.format("AUTH_FAIL: Invalid base64 for %s %s from %s",
                    method, path, httpRequest.getRemoteAddr());
            System.out.println(logMsg);

            httpResponse.setStatus(401);
            sendJsonResponse(httpResponse, "{\"error\":\"Invalid credentials encoding\"}");
            return;
        }

        String[] parts = credentials.split(":", 2);
        if (parts.length != 2) {
            String logMsg = String.format("AUTH_FAIL: Malformed credentials for %s %s from %s",
                    method, path, httpRequest.getRemoteAddr());
            System.out.println(logMsg);

            httpResponse.setStatus(401);
            sendJsonResponse(httpResponse, "{\"error\":\"Invalid credentials\"}");
            return;
        }

        String username = parts[0];
        String password = parts[1];

        String attemptLog = String.format("AUTH_ATTEMPT: User '%s' trying %s %s from %s",
                username, method, path, httpRequest.getRemoteAddr());
        System.out.println(attemptLog);

        UserDTO user = userDAO.findUserByUsername(username);

        if (user == null) {
            String logMsg = String.format("AUTH_FAIL: User '%s' not found for %s %s",
                    username, method, path);
            System.out.println(logMsg);

            httpResponse.setStatus(401);
            sendJsonResponse(httpResponse, "{\"error\":\"Invalid credentials\"}");
            return;
        }

        boolean passwordValid = PasswordUtil.verifyPassword(password, user.getPasswordHash());

        if (passwordValid) {
            Set<String> roles = new HashSet<>(user.getRoles());

            if (checkAccess(path, method, roles)) {
                String successLog = String.format("AUTH_SUCCESS: User '%s' authenticated for %s %s",
                        username, method, path);
                System.out.println(successLog);

                httpRequest.setAttribute("user", user);
                chain.doFilter(request, response);
            } else {
                String failLog = String.format("ACCESS_DENIED: User '%s' (roles: %s) denied for %s %s",
                        username, roles, method, path);
                System.out.println(failLog);

                httpResponse.setStatus(403);
                sendJsonResponse(httpResponse, "{\"error\":\"Forbidden - insufficient permissions\"}");
            }
        } else {
            String failLog = String.format("AUTH_FAIL: Invalid password for user '%s' for %s %s",
                    username, method, path);
            System.out.println(failLog);

            httpResponse.setStatus(401);
            sendJsonResponse(httpResponse, "{\"error\":\"Invalid credentials\"}");
        }
    }

    private boolean checkAccess(String path, String method, Set<String> roles) {
        if (roles.contains("ADMIN")) {
            return true;
        }

        if (path.startsWith("/api/users")) {
            if (path.matches("/api/users/.*") && !path.contains("/search") && !path.contains("/recent")) {
                return method.equals("GET") && roles.contains("USER");
            }
            return method.equals("GET") && (roles.contains("USER") || roles.contains("API_USER"));
        }

        if (path.startsWith("/api/functions")) {
            if (method.equals("POST") || method.equals("PUT") || method.equals("DELETE")) {
                return roles.contains("USER");
            }
            return roles.contains("USER") || roles.contains("API_USER");
        }

        if (path.startsWith("/api/operations") || path.startsWith("/api/cache")) {
            return method.equals("GET") && (roles.contains("USER") || roles.contains("API_USER"));
        }

        if (path.startsWith("/api/admin")) {
            return false;
        }

        return false;
    }

    @Override
    public void destroy() {}

    private void sendJsonResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}