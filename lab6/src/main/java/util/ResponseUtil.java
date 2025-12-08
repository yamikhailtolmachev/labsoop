package util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

public class ResponseUtil {

    public static String getHeader(Object request, String headerName) {
        try {
            if (request instanceof HttpServletRequest) {
                return ((HttpServletRequest) request).getHeader(headerName);
            } else {
                Method getHeader = request.getClass().getMethod("getHeader", String.class);
                return (String) getHeader.invoke(request, headerName);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void setStatus(Object response, int status) {
        try {
            if (response instanceof HttpServletResponse) {
                ((HttpServletResponse) response).setStatus(status);
            } else {
                Method setStatus = response.getClass().getMethod("setStatus", int.class);
                setStatus.invoke(response, status);
            }
        } catch (Exception e) {
        }
    }

    public static void sendJsonResponse(Object response, String json) throws Exception {
        JsonUtil.sendJsonResponse(response, json);
    }

    public static String getRequestUri(Object request) {
        try {
            if (request instanceof HttpServletRequest) {
                return ((HttpServletRequest) request).getRequestURI();
            } else {
                Method getRequestUri = request.getClass().getMethod("getRequestURI");
                return (String) getRequestUri.invoke(request);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMethod(Object request) {
        try {
            if (request instanceof HttpServletRequest) {
                return ((HttpServletRequest) request).getMethod();
            } else {
                Method getMethod = request.getClass().getMethod("getMethod");
                return (String) getMethod.invoke(request);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static void setHeader(Object response, String name, String value) {
        try {
            if (response instanceof HttpServletResponse) {
                ((HttpServletResponse) response).setHeader(name, value);
            } else {
                Method setHeader = response.getClass().getMethod("setHeader", String.class, String.class);
                setHeader.invoke(response, name, value);
            }
        } catch (Exception e) {
        }
    }

    public static void setAttribute(Object request, String name, Object value) {
        try {
            if (request instanceof HttpServletRequest) {
                ((HttpServletRequest) request).setAttribute(name, value);
            } else {
                Method setAttribute = request.getClass().getMethod("setAttribute", String.class, Object.class);
                setAttribute.invoke(request, name, value);
            }
        } catch (Exception e) {
        }
    }
}