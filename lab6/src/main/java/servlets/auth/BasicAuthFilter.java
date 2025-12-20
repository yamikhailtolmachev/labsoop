package servlets.auth;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@WebFilter(filterName = "BasicAuthFilter", urlPatterns = {"/*"})
public class BasicAuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("=== BASIC AUTH FILTER INITIALIZED ===");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        System.out.println("=== FILTER EXECUTED ===");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Выводим ВСЕ заголовки для отладки
        java.util.Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println("Header: " + headerName + " = " + httpRequest.getHeader(headerName));
        }

        System.out.println("URI: " + httpRequest.getRequestURI());
        System.out.println("Method: " + httpRequest.getMethod());

        String authHeader = httpRequest.getHeader("Authorization");
        System.out.println("Auth Header: " + (authHeader != null ? "Present" : "Missing"));

        String uri = httpRequest.getRequestURI();
        if (!uri.contains("/api/")) {
            System.out.println("Not an API request - skipping auth");
            chain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            System.out.println("Missing Basic Auth header");
            httpResponse.setStatus(401);
            httpResponse.setHeader("WWW-Authenticate", "Basic realm=\"Math Functions API\"");
            sendJsonResponse(httpResponse, "{\"error\":\"Unauthorized\"}");
            return;
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        String[] parts = credentials.split(":", 2);

        if (parts.length != 2) {
            System.out.println("Invalid credentials format");
            httpResponse.setStatus(401);
            sendJsonResponse(httpResponse, "{\"error\":\"Invalid credentials\"}");
            return;
        }

        String username = parts[0];
        String password = parts[1];

        System.out.println("Auth attempt for user: " + username);

        if ("testuser_api".equals(username) && "testpassword".equals(password)) {
            System.out.println("Authentication SUCCESS for: " + username);
            System.out.println("Calling chain.doFilter()...");
            chain.doFilter(request, response);
            System.out.println("=== FILTER COMPLETED SUCCESSFULLY ===");
        } else {
            System.out.println("Authentication FAILED for: " + username);
            httpResponse.setStatus(401);
            sendJsonResponse(httpResponse, "{\"error\":\"Invalid credentials\"}");
        }
    }

    @Override
    public void destroy() {
        System.out.println("=== FILTER DESTROYED ===");
    }

    private void sendJsonResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}