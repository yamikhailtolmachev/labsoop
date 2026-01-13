package lab5;

import lab5.entity.UserEntity;
import lab5.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class PerformanceTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void performanceTest() throws IOException {
        int numberOfRecords = 10_000;
        String prefix = "perf_user_";
        List<UserEntity> usersToSave = new ArrayList<>();

        for (int i = 0; i < numberOfRecords; i++) {
            UserEntity user = new UserEntity(prefix + i, "user_" + i + "@example.com", "hash");
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            usersToSave.add(user);
        }

        long startTimeInsert = System.nanoTime();
        userRepository.saveAll(usersToSave);
        long endTimeInsert = System.nanoTime();
        long durationInsert = (endTimeInsert - startTimeInsert) / 1_000_000;

        long startTimeFindOne = System.nanoTime();
        Optional<UserEntity> foundUser = userRepository.findByUsername(prefix + 0);
        long endTimeFindOne = System.nanoTime();
        long durationFindOne = (endTimeFindOne - startTimeFindOne) / 1_000_000;

        long startTimeFindAll = System.nanoTime();
        List<UserEntity> allUsers = userRepository.findAll();
        long endTimeFindAll = System.nanoTime();
        long durationFindAll = (endTimeFindAll - startTimeFindAll) / 1_000_000;

        long startTimeDelete = System.nanoTime();
        userRepository.deleteAll(usersToSave);
        long endTimeDelete = System.nanoTime();
        long durationDelete = (endTimeDelete - startTimeDelete) / 1_000_000;

        assertTrue(durationInsert > 0);
        assertTrue(durationFindOne > 0);
        assertTrue(durationFindAll > 0);
        assertTrue(durationDelete > 0);

        List<PerformanceRecord> results = new ArrayList<>();
        results.add(new PerformanceRecord("INSERT", "User", numberOfRecords, durationInsert, "Batch Save", "Пакетная вставка"));
        results.add(new PerformanceRecord("SELECT (поиск по имени)", "User", 1, durationFindOne, "FindByUsername", "Поиск одной записи"));
        results.add(new PerformanceRecord("SELECT (все записи)", "User", numberOfRecords, durationFindAll, "FindAll", "Полная выборка"));
        results.add(new PerformanceRecord("DELETE", "User", numberOfRecords, durationDelete, "Delete All", "Массовое удаление"));

        saveResultsToFile(results);
    }

    private void saveResultsToFile(List<PerformanceRecord> results) throws IOException {
        String file = "Performance_Test.md";
        try (FileWriter w = new FileWriter(file)) {
            w.write("# Результаты производительности для 10,000 записей\n\n");

            w.write("| Операция | Тип данных | Кол-во записей | Время (мс) | Алгоритм/Подход | Примечания |\n");
            w.write("|----------|------------|----------------|-------------|------------------|-------------|\n");

            for (PerformanceRecord r : results) {
                w.write(String.format(
                        "| %s | %s | %d | %d | `%s` | %s |\n",
                        r.operation, r.dataType, r.count, r.timeMs, r.algorithm, r.notes
                ));
            }
        }
    }

    private static class PerformanceRecord {
        String operation;
        String dataType;
        int count;
        long timeMs;
        String algorithm;
        String notes;

        PerformanceRecord(String op, String type, int cnt, long ms, String alg, String note) {
            this.operation = op;
            this.dataType = type;
            this.count = cnt;
            this.timeMs = ms;
            this.algorithm = alg;
            this.notes = note;
        }
    }
}