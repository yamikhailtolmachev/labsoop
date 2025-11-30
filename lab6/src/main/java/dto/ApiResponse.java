package dto;
import java.util.HashMap;
import java.util.Map;
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
    private Map<String, Object> metadata;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.metadata = new HashMap<>();
    }

    public ApiResponse(boolean success, String message, Object data) {
        this(success, message);
        this.data = data;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public void addMetadata(String key, Object value) {
        this.metadata.put(key, value);
    }
}