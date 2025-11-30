package servlets.api;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import dao.CacheDAO;
import dao.CacheDAOImpl;
import java.util.UUID;

@WebServlet("/api/cache/*")
public class CacheServlet extends HttpServlet {
    private CacheDAO cacheDAO = new CacheDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            response.getWriter().write("{\"success\":true,\"data\":" + toJson(cacheDAO.findAllCache()) + "}");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();
            if (pathInfo != null && pathInfo.length() > 1) {
                String cacheKey = pathInfo.substring(1);
                cacheDAO.deleteCache(cacheKey);
                response.getWriter().write("{\"success\":true,\"message\":\"Cache deleted: " + cacheKey + "\"}");
            } else {
                response.setStatus(400);
                response.getWriter().write("{\"success\":false,\"error\":\"Cache key required\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"success\":false,\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private String toJson(Object obj) {
        if (obj == null) return "null";
        return obj.toString();
    }
}