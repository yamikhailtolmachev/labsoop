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

public class ManualPerformanceTest {

    private static CacheDAO cacheDAO = new CacheDAOImpl();
    private static FunctionDAO functionDAO = new FunctionDAOImpl();
    private static OperationDAO operationDAO = new OperationDAOImpl();
    private static UserDAO userDAO = new UserDAOImpl();

    private static UUID testUserId;
    private static UUID testFunctionId;

    @BeforeAll
    static void setup() {
        testUserId = UUID.randomUUID();
        cleanupExistingData();
        createTestUser();
        generateTestData();
        testFunctionId = getTestFunctionId();
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
        System.out.println("Создание тестовых данных для поиска...");

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            insertFunctionsForSearch(conn, 2000);
            List<UUID> functionIds = getFunctionIds(conn);
            insertOperationsForSearch(conn, functionIds, 1000);
            insertCacheForSearch(conn, functionIds, 1000);

            conn.commit();
            System.out.println("Создано тестовых данных для поиска");

        } catch (Exception e) {
            System.out.println("Ошибка создания тестовых данных: " + e.getMessage());
        }
    }

    private static void insertFunctionsForSearch(Connection conn, int count) throws Exception {
        String sql = "INSERT INTO functions (id, user_id, name, type, expression, left_bound, right_bound, points_count, points_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < count; i++) {
                stmt.setObject(1, UUID.randomUUID());
                stmt.setObject(2, testUserId);
                stmt.setString(3, "search_func_" + i);
                stmt.setString(4, i % 3 == 0 ? "polynomial" : (i % 3 == 1 ? "linear" : "trigonometric"));
                stmt.setString(5, "x^" + (i % 5) + " + " + i);
                stmt.setDouble(6, -10.0 + (i % 20));
                stmt.setDouble(7, 10.0 + (i % 15));
                stmt.setInt(8, 50 + (i % 100));
                stmt.setString(9, "{\"index\": " + i + "}");
                stmt.addBatch();

                if (i % 500 == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
        }
    }

    private static void insertOperationsForSearch(Connection conn, List<UUID> functionIds, int count) throws Exception {
        String sql = "INSERT INTO operations (id, user_id, function1_id, function2_id, result_function_id, operation_type, parameters) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < count && i < functionIds.size() - 2; i++) {
                stmt.setObject(1, UUID.randomUUID());
                stmt.setObject(2, testUserId);
                stmt.setObject(3, functionIds.get(i));
                stmt.setObject(4, functionIds.get(i + 1));
                stmt.setObject(5, functionIds.get(i + 2));
                stmt.setString(6, i % 4 == 0 ? "addition" : (i % 4 == 1 ? "multiplication" : (i % 4 == 2 ? "division" : "composition")));
                stmt.setString(7, "{\"param\": " + i + "}");
                stmt.addBatch();

                if (i % 500 == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
        }
    }

    private static void insertCacheForSearch(Connection conn, List<UUID> functionIds, int count) throws Exception {
        String sql = "INSERT INTO computation_cache (cache_key, user_id, function_expression, left_bound, right_bound, points_count, result_function_id, access_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < count && i < functionIds.size(); i++) {
                stmt.setString(1, "search_cache_" + i);
                stmt.setObject(2, testUserId);
                stmt.setString(3, "x^2 + " + i);
                stmt.setDouble(4, -5.0 + (i % 10));
                stmt.setDouble(5, 5.0 + (i % 8));
                stmt.setInt(6, 30 + (i % 70));
                stmt.setObject(7, functionIds.get(i));
                stmt.setInt(8, i % 50);
                stmt.addBatch();

                if (i % 500 == 0) {
                    stmt.executeBatch();
                }
            }
            stmt.executeBatch();
        }
    }

    private static List<UUID> getFunctionIds(Connection conn) throws Exception {
        List<UUID> ids = new ArrayList<>();
        String sql = "SELECT id FROM functions WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, testUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ids.add((UUID) rs.getObject("id"));
            }
        }
        return ids;
    }

    private static UUID getTestFunctionId() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT id FROM functions WHERE user_id = ? LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setObject(1, testUserId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return (UUID) rs.getObject("id");
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка получения testFunctionId: " + e.getMessage());
        }
        return null;
    }

    @Test
    void runPerformanceTests() throws IOException {
        List<PerformanceResult> manualResults = testAllSearches();
        saveResultsToFile(manualResults);
        printResults(manualResults);
        cleanupTestData();
    }

    private List<PerformanceResult> testAllSearches() {
        List<PerformanceResult> results = new ArrayList<>();

        results.add(testSingleSearch());
        results.add(testMultipleCriteriaSearch());
        results.add(testDepthFirstSearch());
        results.add(testBreadthFirstSearch());
        results.add(testHierarchySearch());
        results.add(testSortByName());
        results.add(testSortByType());
        results.add(testSortByPoints());
        results.add(testSortByAccessCount());
        results.add(testSortByDate());

        return results;
    }

    private PerformanceResult testSingleSearch() {
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try {
            List<FunctionDTO> functions = functionDAO.findFunctionsByUserId(testUserId);
            recordsProcessed = functions.size();
        } catch (Exception e) {
            System.out.println("Ошибка single search: " + e.getMessage());
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        return new PerformanceResult("Single Search (by user)", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testMultipleCriteriaSearch() {
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try {
            List<FunctionDTO> functions = functionDAO.findFunctionsByMultipleCriteria(
                    testUserId, "search_func", "polynomial", -5.0, 15.0, "name", "ASC");
            recordsProcessed = functions.size();
        } catch (Exception e) {
            System.out.println("Ошибка multiple criteria search: " + e.getMessage());
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        return new PerformanceResult("Multiple Criteria Search", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testDepthFirstSearch() {
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try {
            if (testFunctionId != null) {
                List<FunctionDTO> functions = functionDAO.findFunctionDerivatives(testFunctionId);
                recordsProcessed = functions.size();
            }
        } catch (Exception e) {
            System.out.println("Ошибка depth-first search: " + e.getMessage());
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        return new PerformanceResult("Depth-First Search", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testBreadthFirstSearch() {
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try {
            List<FunctionDTO> functions = functionDAO.findFunctionsByLevel(testUserId, "30 days");
            recordsProcessed = functions.size();
        } catch (Exception e) {
            System.out.println("Ошибка breadth-first search: " + e.getMessage());
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        return new PerformanceResult("Breadth-First Search", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testHierarchySearch() {
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try {
            if (testFunctionId != null) {
                List<OperationDTO> operations = operationDAO.findOperationsByFunctionHierarchy(testFunctionId);
                recordsProcessed = operations.size();
            }
        } catch (Exception e) {
            System.out.println("Ошибка hierarchy search: " + e.getMessage());
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        return new PerformanceResult("Hierarchy Search", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testSortByName() {
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try {
            List<FunctionDTO> functions = functionDAO.findFunctionsByMultipleCriteria(
                    testUserId, null, null, null, null, "name", "ASC");
            recordsProcessed = functions.size();
        } catch (Exception e) {
            System.out.println("Ошибка sort by name: " + e.getMessage());
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        return new PerformanceResult("SORT Functions by name", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testSortByType() {
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try {
            List<FunctionDTO> functions = functionDAO.findFunctionsByMultipleCriteria(
                    testUserId, null, null, null, null, "type", "DESC");
            recordsProcessed = functions.size();
        } catch (Exception e) {
            System.out.println("Ошибка sort by type: " + e.getMessage());
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        return new PerformanceResult("SORT Functions by type", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testSortByPoints() {
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try {
            List<FunctionDTO> functions = functionDAO.findFunctionsByMultipleCriteria(
                    testUserId, null, null, null, null, "points_count", "DESC");
            recordsProcessed = functions.size();
        } catch (Exception e) {
            System.out.println("Ошибка sort by points: " + e.getMessage());
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        return new PerformanceResult("SORT Functions by points", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testSortByAccessCount() {
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try {
            List<CacheDTO> cacheEntries = cacheDAO.findCacheByMultipleCriteria(
                    testUserId, null, null, null, "access_count", "DESC");
            recordsProcessed = cacheEntries.size();
        } catch (Exception e) {
            System.out.println("Ошибка sort by access count: " + e.getMessage());
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        return new PerformanceResult("SORT Cache by access", recordsProcessed, Math.max(durationMs, 1));
    }

    private PerformanceResult testSortByDate() {
        long startTime = System.nanoTime();
        int recordsProcessed = 0;

        try {
            List<OperationDTO> operations = operationDAO.findOperationsByMultipleCriteria(
                    testUserId, null, null, "computed_at", "DESC");
            recordsProcessed = operations.size();
        } catch (Exception e) {
            System.out.println("Ошибка sort by date: " + e.getMessage());
        }

        long durationMs = (System.nanoTime() - startTime) / 1_000_000;
        return new PerformanceResult("SORT Operations by date", recordsProcessed, Math.max(durationMs, 1));
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
        } catch (Exception e) {
            System.out.println("Ошибка очистки: " + e.getMessage());
        }
    }

    private void saveResultsToFile(List<PerformanceResult> results) throws IOException {
        String filename = "manual_performance_results.md";
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("# Результаты тестирования производительности поиска и сортировки\n\n");
            writer.write("## Manual JDBC Поиск и Сортировка\n\n");
            writer.write("| Операция | Обработано записей | Время (мс) | Записей/сек |\n");
            writer.write("|----------|-------------------|------------|-------------|\n");

            for (PerformanceResult result : results) {
                double recordsPerSecond = (double) result.records / result.timeMs * 1000;
                writer.write(String.format("| %s | %d | %d | %.2f |\n",
                        result.operation, result.records, result.timeMs, recordsPerSecond));
            }
        }
    }

    private void printResults(List<PerformanceResult> results) {
        System.out.println("================================================");
        System.out.println("Manual JDBC Поиск и Сортировка");
        System.out.println("================================================");
        System.out.println("| Операция | Обработано записей | Время (мс) | Записей/сек |");
        System.out.println("|----------|-------------------|------------|-------------|");

        for (PerformanceResult result : results) {
            double recordsPerSecond = (double) result.records / result.timeMs * 1000;
            System.out.printf("| %-25s | %17d | %10d | %11.2f |\n",
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