package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private static final String LOG_DIR = "logs";
    private static final String AUTH_LOG_FILE = LOG_DIR + "/auth.log";
    private static final String ACCESS_LOG_FILE = LOG_DIR + "/access.log";
    private static final String ERROR_LOG_FILE = LOG_DIR + "/error.log";
    private static final String APP_LOG_FILE = LOG_DIR + "/application.log";

    static {
        new File(LOG_DIR).mkdirs();
    }

    public static void logAuth(String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [AUTH] %s", timestamp, message);

        System.out.println(logMessage);
        writeToFile(AUTH_LOG_FILE, logMessage);
        writeToFile(APP_LOG_FILE, logMessage);
    }

    public static void logAccess(String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [ACCESS] %s", timestamp, message);

        System.out.println(logMessage);
        writeToFile(ACCESS_LOG_FILE, logMessage);
        writeToFile(APP_LOG_FILE, logMessage);
    }

    public static void logError(String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [ERROR] %s", timestamp, message);

        System.err.println(logMessage);
        writeToFile(ERROR_LOG_FILE, logMessage);
        writeToFile(APP_LOG_FILE, logMessage);
    }

    public static void logError(String message, Throwable throwable) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [ERROR] %s: %s", timestamp, message, throwable.getMessage());

        System.err.println(logMessage);
        throwable.printStackTrace();
        writeToFile(ERROR_LOG_FILE, logMessage);
        writeToFile(APP_LOG_FILE, logMessage);
    }

    public static void info(String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [INFO] %s", timestamp, message);

        System.out.println(logMessage);
        writeToFile(APP_LOG_FILE, logMessage);
    }

    public static void warn(String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [WARN] %s", timestamp, message);

        System.out.println(logMessage);
        writeToFile(APP_LOG_FILE, logMessage);
    }

    public static void error(String message) {
        logError(message);
    }

    public static void error(String message, Throwable throwable) {
        logError(message, throwable);
    }

    public static void debug(String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logMessage = String.format("[%s] [DEBUG] %s", timestamp, message);

        System.out.println(logMessage);
        writeToFile(APP_LOG_FILE, logMessage);
    }

    private static void writeToFile(String filename, String message) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println(message);
        } catch (IOException e) {
            System.err.println("Failed to write to log file " + filename + ": " + e.getMessage());
        }
    }
}