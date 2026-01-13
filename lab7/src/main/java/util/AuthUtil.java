package util;
import java.util.Base64;
public class AuthUtil {
    public static boolean validateBasicAuth(String authHeader) {
        try {
            String credentials = new String(Base64.getDecoder().decode(authHeader.substring(6)));
            String[] parts = credentials.split(":");
            return parts.length == 2 && validateCredentials(parts[0], parts[1]);
        } catch (Exception e) {
            return false;
        }
    }
    private static boolean validateCredentials(String username, String password) {
        return "admin".equals(username) && "password".equals(password);
    }
}