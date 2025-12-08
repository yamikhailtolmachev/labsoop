package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PasswordUtil {
    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);

    public static String hashPassword(String plainPassword) {
        logger.debug("Hashing password");
        return BCryptImpl.hashpw(plainPassword);
    }

    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        logger.debug("Verifying password");
        return BCryptImpl.checkpw(plainPassword, hashedPassword);
    }
}