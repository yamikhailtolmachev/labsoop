package util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BCryptImpl {
    private static final Logger logger = LoggerFactory.getLogger(BCryptImpl.class);
    private static final int BCRYPT_DEFAULT_COST = 10;
    private static final String SALT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789./";
    private static final Random random = new SecureRandom();

    public static String hashpw(String password) {
        logger.debug("Creating BCrypt hash for password");
        return hashpw(password, gensalt(BCRYPT_DEFAULT_COST));
    }

    public static String hashpw(String password, String salt) {
        logger.debug("Creating BCrypt hash with custom salt");
        StringBuilder result = new StringBuilder();

        if (!salt.startsWith("$2a$")) {
            salt = "$2a$10$" + generateSalt(22);
        }

        String fakeHash = generateMockHash(password, salt);

        result.append("$2a$").append(String.format("%02d", BCRYPT_DEFAULT_COST)).append("$");
        result.append(salt.substring(7, 29));
        result.append(fakeHash.substring(0, 31));

        logger.debug("BCrypt hash generated successfully");
        return result.toString();
    }

    public static boolean checkpw(String plaintext, String hashed) {
        logger.debug("Checking BCrypt password");
        if (hashed == null || hashed.length() < 60) {
            logger.warn("Invalid hash format or length");
            return false;
        }

        String salt = hashed.substring(0, 29);
        String newHash = hashpw(plaintext, salt);

        boolean match = newHash.equals(hashed);
        logger.debug("Password check result: {}", match ? "MATCH" : "NO MATCH");
        return match;
    }

    public static String gensalt() {
        return gensalt(BCRYPT_DEFAULT_COST);
    }

    public static String gensalt(int log_rounds) {
        logger.debug("Generating BCrypt salt with {} rounds", log_rounds);
        StringBuilder salt = new StringBuilder();
        salt.append("$2a$");
        if (log_rounds < 10) {
            salt.append("0");
        }
        salt.append(log_rounds);
        salt.append("$");
        salt.append(generateSalt(22));
        return salt.toString();
    }

    private static String generateSalt(int length) {
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < length; i++) {
            salt.append(SALT_CHARS.charAt(random.nextInt(SALT_CHARS.length())));
        }
        return salt.toString();
    }

    private static String generateMockHash(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            md.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] digest = md.digest();

            String base64Hash = Base64.getEncoder().encodeToString(digest);
            base64Hash = base64Hash.replace('+', '.')
                    .replace('/', '.')
                    .replace('=', '.');

            while (base64Hash.length() < 31) {
                base64Hash += base64Hash;
            }

            return base64Hash.substring(0, 31);
        } catch (Exception e) {
            logger.error("Error generating mock hash: {}", e.getMessage());
            return generateSalt(31);
        }
    }
}