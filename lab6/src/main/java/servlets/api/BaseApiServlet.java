package servlets.api;
import util.ResponseUtil;
import util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public abstract class BaseApiServlet {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String getPathParameter(Object request) {
        String pathInfo = ResponseUtil.getPathInfo(request);
        if (pathInfo == null || pathInfo.equals("/")) return null;
        return pathInfo.substring(1);
    }

    protected void sendSuccess(Object response, String message) throws Exception {
        ResponseUtil.sendJsonResponse(response, JsonUtil.success(message));
    }

    protected void sendSuccess(Object response, String message, Object data) throws Exception {
        ResponseUtil.sendJsonResponse(response, JsonUtil.success(message, data));
    }

    protected void sendError(Object response, int status, String message) throws Exception {
        ResponseUtil.setStatus(response, status);
        ResponseUtil.sendJsonResponse(response, JsonUtil.error(status, message));
    }

    protected String readRequestBody(Object request) throws Exception {
        return ResponseUtil.readRequestBody(request);
    }

    protected String getCurrentUserId() {
        return "test-user-id";
    }

    public abstract void doGet(Object request, Object response) throws Exception;
    public abstract void doPost(Object request, Object response) throws Exception;
    public abstract void doPut(Object request, Object response) throws Exception;
    public abstract void doDelete(Object request, Object response) throws Exception;
}