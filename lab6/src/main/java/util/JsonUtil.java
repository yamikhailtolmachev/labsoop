package util;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonUtil {
    public static String toJson(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof String) return "\"" + escapeJson((String) obj) + "\"";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof UUID) return "\"" + obj.toString() + "\"";
        if (obj instanceof Map) return mapToJson((Map<?, ?>) obj);
        if (obj instanceof List) return listToJson((List<?>) obj);
        return "\"" + escapeJson(obj.toString()) + "\"";
    }

    private static String mapToJson(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJson(entry.getKey().toString())).append("\":");
            sb.append(toJson(entry.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private static String listToJson(List<?> list) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            if (!first) sb.append(",");
            sb.append(toJson(item));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    public static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public static String success(String message) {
        return "{\"success\":true,\"message\":\"" + escapeJson(message) + "\"}";
    }

    public static String success(String message, Object data) {
        return "{\"success\":true,\"message\":\"" + escapeJson(message) + "\",\"data\":" + toJson(data) + "}";
    }

    public static String error(int status, String message) {
        return "{\"success\":false,\"error\":" + status + ",\"message\":\"" + escapeJson(message) + "\"}";
    }

    public static void writeJson(HttpServletResponse response, Object object) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write(toJson(object));
        writer.flush();
    }

    public static void sendJsonResponse(Object response, String json) throws Exception {
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            PrintWriter writer = httpResponse.getWriter();
            writer.write(json);
            writer.flush();
        } else {
            throw new IllegalArgumentException("Unsupported response type");
        }
    }
}