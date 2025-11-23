package lab5;

import lab5.Application;
import lab5.entity.UserEntity;
import lab5.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = Application.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://localhost:5432/framework_performance_test_db",
        "spring.datasource.username=user09",
        "spring.datasource.password=093306",
        "spring.datasource.driver-class-name=org.postgresql.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
        "spring.jpa.show-sql=false"
})
class FrameworkPerformanceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void performanceTestFramework() throws IOException {
        int numberOfRecords = 10_000;
        String prefix = "perf_test_framework_user_";
        List<UserEntity> usersToSave = new ArrayList<>();

        for (int i = 0; i < numberOfRecords; i++) {
            UserEntity user = new UserEntity(prefix + i, "fw_email" + i + "@example.com", "fw_hash" + i);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            usersToSave.add(user);
        }

        long startTimeInsert = System.nanoTime();
        userRepository.saveAll(usersToSave);
        long endTimeInsert = System.nanoTime();
        long durationInsert = (endTimeInsert - startTimeInsert) / 1_000_000;

        System.out.println("Framework - Time taken to insert " + numberOfRecords + " records: " + durationInsert + " ms");

        long startTimeFindOne = System.nanoTime();
        Optional<UserEntity> foundUser = userRepository.findByUsername(prefix + 0);
        long endTimeFindOne = System.nanoTime();
        long durationFindOne = (endTimeFindOne - startTimeFindOne) / 1_000_000;

        System.out.println("Framework - Time taken to find one record by username: " + durationFindOne + " ms");

        long startTimeFindAll = System.nanoTime();
        List<UserEntity> allUsers = userRepository.findAll();
        long endTimeFindAll = System.nanoTime();
        long durationFindAll = (endTimeFindAll - startTimeFindAll) / 1_000_000;

        System.out.println("Framework - Time taken to find all " + numberOfRecords + " records: " + durationFindAll + " ms");

        long startTimeDelete = System.nanoTime();
        userRepository.deleteAll(usersToSave);
        long endTimeDelete = System.nanoTime();
        long durationDelete = (endTimeDelete - startTimeDelete) / 1_000_000;

        System.out.println("Framework - Time taken to delete " + numberOfRecords + " records: " + durationDelete + " ms");

        assertTrue(durationInsert > 0);
        assertTrue(durationFindOne > 0);
        assertTrue(durationFindAll > 0);
        assertTrue(durationDelete > 0);

        List<PerformanceResult> results = new ArrayList<>();
        results.add(new PerformanceResult("INSERT", numberOfRecords, durationInsert, "Пакетная вставка"));
        results.add(new PerformanceResult("SELECT (поиск по имени)", 1, durationFindOne, "Поиск одной записи"));
        results.add(new PerformanceResult("SELECT (все записи)", numberOfRecords, durationFindAll, "Получение всех записей"));
        results.add(new PerformanceResult("DELETE", numberOfRecords, durationDelete, "Удаление всех записей"));

        long totalDuration = durationInsert + durationFindOne + durationFindAll + durationDelete;
        double averageDuration = (double) totalDuration / 4;
        double insertSpeed = (double) numberOfRecords / durationInsert * 1000;
        double deleteSpeed = (double) numberOfRecords / durationDelete * 1000;

        saveResultsToFile(results, "Framework", totalDuration, averageDuration, insertSpeed, deleteSpeed);
        printResults(results, "Framework", totalDuration, averageDuration, insertSpeed, deleteSpeed);
    }

    private void saveResultsToFile(List<PerformanceResult> results, String approach, long totalDuration, double averageDuration, double insertSpeed, double deleteSpeed) throws IOException {
        String filename = "performance_results.md";
        try (java.io.FileWriter writer = new java.io.FileWriter(filename)) {
            writer.write("# Результаты производительности для 10,000 записей\n\n");
            writer.write("| Операция | Количество записей | Время (мс) | Примечания |\n");
            writer.write("|----------|-------------------|------------|-------------|\n");

            for (PerformanceResult result : results) {
                writer.write(String.format("| %s | %d | %d | %s |\n",
                        result.operation, result.records, result.timeMs, result.notes));
            }

            writer.write("\n## Общая статистика:\n\n");
            writer.write("- Общее время операций: " + totalDuration + " мс\n");
            writer.write("- Среднее время на операцию: " + String.format("%.2f", averageDuration) + " мс\n");
            writer.write("- Скорость вставки: " + String.format("%.2f", insertSpeed) + " записей/сек\n");
            writer.write("- Скорость удаления: " + String.format("%.2f", deleteSpeed) + " записей/сек\n");
        }
    }

    private void printResults(List<PerformanceResult> results, String approach, long totalDuration, double averageDuration, double insertSpeed, double deleteSpeed) {
        System.out.println("\n# Результаты производительности: " + approach + "\n");
        System.out.println("## Результаты производительности для 10,000 записей\n");
        System.out.println("| Операция | Количество записей | Время (мс) | Примечания |");
        System.out.println("|----------|-------------------|------------|-------------|");

        for (PerformanceResult result : results) {
            System.out.printf("| %s | %d | %d | %s |\n",
                    result.operation, result.records, result.timeMs, result.notes);
        }

        System.out.println("\n## Общая статистика:\n");
        System.out.println("- Общее время операций: " + totalDuration + " мс");
        System.out.println("- Среднее время на операцию: " + String.format("%.2f", averageDuration) + " мс");
        System.out.println("- Скорость вставки: " + String.format("%.2f", insertSpeed) + " записей/сек");
        System.out.println("- Скорость удаления: " + String.format("%.2f", deleteSpeed) + " записей/сек");
    }

    private static class PerformanceResult {
        String operation;
        int records;
        long timeMs;
        String notes;

        PerformanceResult(String operation, int records, long timeMs, String notes) {
            this.operation = operation;
            this.records = records;
            this.timeMs = timeMs;
            this.notes = notes;
        }
    }
}