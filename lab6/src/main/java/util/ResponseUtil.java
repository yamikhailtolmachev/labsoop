package util;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.stream.Collectors;
public class ResponseUtil {
    public static void sendJsonResponse(Object response, String json) throws IOException {
        try {
            response.getClass().getMethod("setContentType", String.class).invoke(response, "application/json");
            response.getClass().getMethod("setCharacterEncoding", String.class).invoke(response, "UTF-8");
            PrintWriter writer = (PrintWriter) response.getClass().getMethod("getWriter").invoke(response);
            writer.write(json);
            writer.flush();
        } catch (Exception e) {
            throw new IOException("Failed to send JSON response", e);
        }
    }
    public static void setStatus(Object response, int status) {
        try {
            response.getClass().getMethod("setStatus", int.class).invoke(response, status);
        } catch (Exception e) {}
    }
    public static String readRequestBody(Object request) throws IOException {
        try {
            BufferedReader reader = (BufferedReader) request.getClass().getMethod("getReader").invoke(request);
            return reader.lines().collect(Collectors.joining());
        } catch (Exception e) {
            throw new IOException("Failed to read request body", e);
        }
    }
    public static String getPathInfo(Object request) {
        try {
            return (String) request.getClass().getMethod("getPathInfo").invoke(request);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getHeader(Object request, String headerName) {
        try {
            return (String) request.getClass().getMethod("getHeader", String.class).invoke(request, headerName);
        } catch (Exception e) {
            return null;
        }
    }
}