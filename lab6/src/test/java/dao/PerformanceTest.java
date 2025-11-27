package dao;

import dto.*;
import database.DatabaseConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PerformanceTest {

    private static CacheDAO cacheDAO = new CacheDAOImpl();
    private static FunctionDAO functionDAO = new FunctionDAOImpl();
    private static OperationDAO operationDAO = new OperationDAOImpl();
    private static UserDAO userDAO = new UserDAOImpl();

    private static UUID testUserId;

    @BeforeAll
    static void setup() {
        testUserId = UUID.randomUUID();
        cleanupExistingData();
        createTestUser();
        generateTestData();
    }

    private static void cleanupExistingData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM computation_cache WHERE cache_key LIKE 'test_cache_%'");
                 PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM operations WHERE user_id = ?");
                 PreparedStatement stmt3 = conn.prepareStatement("DELETE FROM functions WHERE user_id = ?");
                 PreparedStatement stmt4 = conn.prepareStatement("DELETE FROM users WHERE username LIKE 'test_user%'")) {

                stmt1.executeUpdate();
                stmt2.setObject(1, testUserId);
                stmt2.executeUpdate();
                stmt3.setObject(1, testUserId);
                stmt3.executeUpdate();
                stmt4.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            System.out.println("Ошибка очистки старых данных: " + e.getMessage());
        }
    }

    private static void createTestUser() {
        try {
            UserDTO user = new UserDTO();
            user.setId(testUserId);
            user.setUsername("test_user_" + System.currentTimeMillis());
            user.setEmail("test@example.com");
            user.setPasswordHash("test_hash");
            userDAO.insertUser(user);
            System.out.println("Создан тестовый пользователь: " + testUserId);
        } catch (Exception e) {
            System.out.println("Ошибка создания пользователя: " + e.getMessage());
        }
    }

    private static void generateTestData() {
        System.out.println("Создание тестовых данных (10000 записей)...");
        long startTime = System.nanoTime();

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            insertFunctionsBatch(conn, 5000);
            List<UUID> functionIds = getFunctionIds(conn);
            insertOperationsBatch(conn, functionIds, 2500);
            insertCacheBatch(conn, functionIds, 2500);

            conn.commit();

            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;
            System.out.println("Создано 10000 записей за " + durationMs + " мс");

        } catch (Exception e) {
            System.out.println("Ошибка создания тестовых данных: " + e.getMessage());
        }
    }

    private static void insertFunctionsBatch(Connection conn, int count) throws Exception {
        String sql = "INSERT INTO functions (id, user_id, name, type, expression, left_bound, right_bound, points_count, points_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                stmt.setObject(1, UUID.randomUUID());
                stmt.setObject(2, testUserId);
                stmt.setString(3, "test_function_" + i);
                stmt.setString(4, i % 2 == 0 ? "polynomial" : "linear");
                stmt.setString(5, "x^2 + " + i);
                stmt.setDouble(6, -10.0 + (i % 5));
                stmt.setDouble(7, 10.0 + (i % 5));
                stmt.setInt(8, 100 + (i % 50));
                stmt.setString(9, "{\"data\": \"value_" + i + "\"}");
                stmt.addBatch();

                if (i % 1000 == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
        }
        System.out.println("Создано " + count + " функций");
    }

    private static List<UUID> getFunctionIds(Connection conn) throws Exception {
        List<UUID> ids = new ArrayList<>();
        String sql = "SELECT id FROM functions WHERE user_id = ? LIMIT 5000";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, testUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add((UUID) rs.getObject("id"));
            }
        }
        return ids;
    }

    private static void insertOperationsBatch(Connection conn, List<UUID> functionIds, int count) throws Exception {
        String sql = "INSERT INTO operations (id, user_id, function1_id, function2_id, result_function_id, operation_type, parameters) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < count && i < functionIds.size() - 2; i++) {
                stmt.setObject(1, UUID.randomUUID());
                stmt.setObject(2, testUserId);
                stmt.setObject(3, functionIds.get(i));
                stmt.setObject(4, functionIds.get(i + 1));
                stmt.setObject(5, functionIds.get(i + 2));
                stmt.setString(6, i % 3 == 0 ? "addition" : "multiplication");
                stmt.setString(7, "{\"index\": " + i + "}");
                stmt.addBatch();

                if (i % 1000 == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
        }
        System.out.println("Создано " + count + " операций");
    }

    private static void insertCacheBatch(Connection conn, List<UUID> functionIds, int count) throws Exception {
        String sql = "INSERT INTO computation_cache (cache_key, user_id, function_expression, left_bound, right_bound, points_count, result_function_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < count && i < functionIds.size(); i++) {
                stmt.setString(1, "test_cache_" + System.currentTimeMillis() + "_" + i);
                stmt.setObject(2, testUserId);
                stmt.setString(3, "x^2 + " + i);
                stmt.setDouble(4, -5.0);
                stmt.setDouble(5, 5.0);
                stmt.setInt(6, 50 + (i % 30));
                stmt.setObject(7, functionIds.get(i));
                stmt.addBatch();

                if (i % 1000 == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
        }
        System.out.println("Создано " + count + " кэш-записей");
    }

    @Test
    void runPerformanceTests() throws IOException {
        try {
            System.out.println("Запуск тестов производительности на таблицах с 10000 записей");

            List<PerformanceResult> results = new ArrayList<>();

            results.add(testInsertPerformance());
            results.add(testSelectPerformance());
            results.add(testUpdatePerformance());
            results.add(testDeletePerformance());
            results.add(testSearchPerformance());

            saveResultsToFile(results, "performance_results.md");
            printResults(results, "Manual JDBC Производительность (10000 записей)");

            System.out.println("Тестирование завершено успешно");

        } catch (Exception e) {
            System.out.println("Ошибка при выполнении тестов: " + e.getMessage());
        } finally {
            cleanupTestData();
        }
    }

    private PerformanceResult testInsertPerformance() {
        System.out.println("Тестирование INSERT операций");
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO functions (id, user_id, name, type, expression, left_bound, right_bound, points_count, points_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < 10000; i++) {
                    stmt.setObject(1, UUID.randomUUID());
                    stmt.setObject(2, testUserId);
                    stmt.setString(3, "perf_insert_" + i);
                    stmt.setString(4, "test");
                    stmt.setString(5, "x + " + i);
                    stmt.setDouble(6, -10.0);
                    stmt.setDouble(7, 10.0);
                    stmt.setInt(8, 50);
                    stmt.setString(9, "{}");
                    stmt.addBatch();

                    if (i % 1000 == 0) {
                        stmt.executeBatch();
                    }
                    recordsProcessed++;
                }
                stmt.executeBatch();
            }
            conn.commit();
        } catch (Exception e) {
            System.out.println("Ошибка INSERT: " + e.getMessage());
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("INSERT: " + recordsProcessed + " записей за " + durationMs + " мс");

        return new PerformanceResult("INSERT", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testSelectPerformance() {
        System.out.println("Тестирование SELECT операций");
        long startTime = System.nanoTime();
        int totalRecords = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id FROM functions WHERE user_id = ? LIMIT 10000";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, testUserId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    totalRecords++;
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка SELECT: " + e.getMessage());
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("SELECT: " + totalRecords + " записей за " + durationMs + " мс");

        return new PerformanceResult("SELECT", totalRecords, Math.max(durationMs, 1));
    }

    private PerformanceResult testUpdatePerformance() {
        System.out.println("Тестирование UPDATE операций");
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String sql = "UPDATE functions SET name = 'updated_' || name WHERE user_id = ? AND name LIKE 'test_function_%'";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, testUserId);
                recordsProcessed = stmt.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            System.out.println("Ошибка UPDATE: " + e.getMessage());
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("UPDATE: " + recordsProcessed + " записей за " + durationMs + " мс");

        return new PerformanceResult("UPDATE", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testDeletePerformance() {
        System.out.println("Тестирование DELETE операций");
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            String sql = "DELETE FROM functions WHERE user_id = ? AND name LIKE 'perf_insert_%'";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, testUserId);
                recordsProcessed = stmt.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            System.out.println("Ошибка DELETE: " + e.getMessage());
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("DELETE: " + recordsProcessed + " записей за " + durationMs + " мс");

        return new PerformanceResult("DELETE", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testSearchPerformance() {
        System.out.println("Тестирование SEARCH операций");
        long startTime = System.nanoTime();
        int totalRecords = 0;

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT COUNT(*) as cnt FROM functions WHERE user_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, testUserId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    totalRecords = rs.getInt("cnt");
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка SEARCH: " + e.getMessage());
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        System.out.println("SEARCH: " + totalRecords + " записей за " + durationMs + " мс");

        return new PerformanceResult("SEARCH", totalRecords, Math.max(durationMs, 1));
    }

    private void cleanupTestData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM computation_cache WHERE user_id = ?");
                 PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM operations WHERE user_id = ?");
                 PreparedStatement stmt3 = conn.prepareStatement("DELETE FROM functions WHERE user_id = ?");
                 PreparedStatement stmt4 = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {

                stmt1.setObject(1, testUserId);
                stmt1.executeUpdate();

                stmt2.setObject(1, testUserId);
                stmt2.executeUpdate();

                stmt3.setObject(1, testUserId);
                stmt3.executeUpdate();

                stmt4.setObject(1, testUserId);
                stmt4.executeUpdate();
            }

            conn.commit();
            System.out.println("Очистка данных завершена");
        } catch (Exception e) {
            System.out.println("Ошибка очистки: " + e.getMessage());
        }
    }

    private void saveResultsToFile(List<PerformanceResult> results, String filename) throws IOException {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("# Результаты тестирования производительности\n\n");
            writer.write("## Manual JDBC Производительность\n\n");
            writer.write("Тестирование выполнено на таблицах с 10000 записей\n\n");
            writer.write("| Операция | Обработано записей | Время (мс) | Записей/сек |\n");
            writer.write("|----------|-------------------|------------|-------------|\n");

            for (PerformanceResult result : results) {
                double recordsPerSecond = (double) result.records / result.timeMs * 1000;
                writer.write(String.format("| %s | %d | %d | %.2f |\n",
                        result.operation, result.records, result.timeMs, recordsPerSecond));
            }
        }
        System.out.println("Результаты сохранены в файл: " + filename);
    }

    private void printResults(List<PerformanceResult> results, String title) {
        System.out.println("================================================");
        System.out.println(title);
        System.out.println("================================================");
        System.out.println("| Операция | Обработано записей | Время (мс) | Записей/сек |");
        System.out.println("|----------|-------------------|------------|-------------|");

        for (PerformanceResult result : results) {
            double recordsPerSecond = (double) result.records / result.timeMs * 1000;
            System.out.printf("| %-8s | %17d | %10d | %11.2f |\n",
                    result.operation, result.records, result.timeMs, recordsPerSecond);
        }
        System.out.println("================================================");
    }

    private static class PerformanceResult {
        String operation;
        int records;
        long timeMs;

        PerformanceResult(String operation, int records, long timeMs) {
            this.operation = operation;
            this.records = records;
            this.timeMs = timeMs;
        }
    }
}