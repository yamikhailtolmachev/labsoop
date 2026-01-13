package lab5;

import lab5.entity.ComputationCacheEntity;
import lab5.entity.FunctionEntity;
import lab5.entity.OperationEntity;
import lab5.entity.UserEntity;
import lab5.repository.ComputationCacheRepository;
import lab5.repository.FunctionRepository;
import lab5.repository.OperationRepository;
import lab5.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class FrameworkPerformanceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private OperationRepository operationRepository;

    @Autowired
    private ComputationCacheRepository cacheRepository;

    @Test
    void performanceTestFramework() throws IOException {
        int n = 10_000;
        String prefix = "perf_";
        List<UserEntity> users = new ArrayList<>();
        List<FunctionEntity> funcs = new ArrayList<>();
        List<OperationEntity> ops = new ArrayList<>();
        List<ComputationCacheEntity> caches = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            UserEntity u = new UserEntity(prefix + i, "u_" + i + "@test.com", "hash");
            u.setCreatedAt(LocalDateTime.now());
            u.setUpdatedAt(LocalDateTime.now());
            users.add(u);

            FunctionEntity f = new FunctionEntity(u, "func_" + i, "BASIC", "x^2", 0.0, 10.0, i % 100 + 1, "{}");
            f.setCreatedAt(LocalDateTime.now());
            f.setUpdatedAt(LocalDateTime.now());
            funcs.add(f);

            if (i < n / 2) {
                OperationEntity op = new OperationEntity(u, f, null, f, "ADD", "{}");
                op.setComputedAt(LocalDateTime.now());
                op.setUpdatedAt(LocalDateTime.now());
                ops.add(op);

                String pointsData = "{}";
                ComputationCacheEntity c = new ComputationCacheEntity("key_" + i, u, "x^2", 0.0, 10.0, i % 100 + 1, pointsData, f);
                c.setComputedAt(LocalDateTime.now());
                c.setUpdatedAt(LocalDateTime.now());
                c.setAccessCount(i % 100);
                caches.add(c);
            }
        }

        long t0 = System.nanoTime();
        userRepository.saveAll(users);
        functionRepository.saveAll(funcs);
        operationRepository.saveAll(ops);
        cacheRepository.saveAll(caches);
        long tInsert = (System.nanoTime() - t0) / 1_000_000;

        long t1 = System.nanoTime();
        userRepository.findByUsername(prefix + "5000").orElse(null);
        long tSearchUsername = (System.nanoTime() - t1) / 1_000_000;

        long t2 = System.nanoTime();
        userRepository.findByEmail("u_5000@test.com").orElse(null);
        long tSearchEmail = (System.nanoTime() - t2) / 1_000_000;

        Sort sortUserByName = Sort.by("username");
        long t3 = System.nanoTime();
        userRepository.findAll(sortUserByName);
        long tSortUserByName = (System.nanoTime() - t3) / 1_000_000;

        Sort sortUserByEmail = Sort.by("email");
        long t4 = System.nanoTime();
        userRepository.findAll(sortUserByEmail);
        long tSortUserByEmail = (System.nanoTime() - t4) / 1_000_000;

        Sort sortFuncByName = Sort.by("name");
        long t5 = System.nanoTime();
        functionRepository.findAll(sortFuncByName);
        long tSortFuncByName = (System.nanoTime() - t5) / 1_000_000;

        Sort sortFuncByPoints = Sort.by("pointsCount");
        long t6 = System.nanoTime();
        functionRepository.findAll(sortFuncByPoints);
        long tSortFuncByPoints = (System.nanoTime() - t6) / 1_000_000;

        Sort sortOpByType = Sort.by("operationType");
        long t7 = System.nanoTime();
        operationRepository.findAll(sortOpByType);
        long tSortOpByType = (System.nanoTime() - t7) / 1_000_000;

        Sort sortCacheByAccess = Sort.by("accessCount");
        long t8 = System.nanoTime();
        cacheRepository.findAll(sortCacheByAccess);
        long tSortCacheByAccess = (System.nanoTime() - t8) / 1_000_000;

        long t9 = System.nanoTime();
        cacheRepository.deleteAll(caches);
        operationRepository.deleteAll(ops);
        functionRepository.deleteAll(funcs);
        userRepository.deleteAll(users);
        long tDelete = (System.nanoTime() - t9) / 1_000_000;

        assertTrue(tInsert > 0);
        assertTrue(tSearchUsername > 0);
        assertTrue(tSearchEmail > 0);
        assertTrue(tSortUserByName > 0);
        assertTrue(tSortUserByEmail > 0);
        assertTrue(tSortFuncByName > 0);
        assertTrue(tSortFuncByPoints > 0);
        assertTrue(tSortOpByType > 0);
        assertTrue(tSortCacheByAccess > 0);
        assertTrue(tDelete > 0);

        List<PerformanceRecord> records = new ArrayList<>();
        records.add(new PerformanceRecord("INSERT", "All", n * 4, tInsert, "Batch Save", "Пакетная вставка всех сущностей"));

        records.add(new PerformanceRecord("SEARCH (Users by username)", "User", 1, tSearchUsername, "FindByUsername", "Поиск по уникальному имени"));
        records.add(new PerformanceRecord("SEARCH (Users by email)", "User", 1, tSearchEmail, "FindByEmail", "Поиск по email"));

        records.add(new PerformanceRecord("SORT (Users by name)", "User", n, tSortUserByName, "DB Sort", "Сортировка по username"));
        records.add(new PerformanceRecord("SORT (Users by email)", "User", n, tSortUserByEmail, "DB Sort", "Сортировка по email"));
        records.add(new PerformanceRecord("SORT (Functions by name)", "Function", n, tSortFuncByName, "DB Sort", "Сортировка по name"));
        records.add(new PerformanceRecord("SORT (Functions by points)", "Function", n, tSortFuncByPoints, "DB Sort", "Сортировка по points_count"));
        records.add(new PerformanceRecord("SORT (Operations by type)", "Operation", n / 2, tSortOpByType, "DB Sort", "Сортировка по operation_type"));
        records.add(new PerformanceRecord("SORT (Cache by access)", "ComputationCache", n / 2, tSortCacheByAccess, "DB Sort", "Сортировка по access_count"));

        records.add(new PerformanceRecord("DELETE", "All", n * 4, tDelete, "Delete All", "Массовое удаление всех сущностей"));

        saveResultsToFile(records);
    }

    private void saveResultsToFile(List<PerformanceRecord> results) throws IOException {
        String file = "Framework_Performance_Test.md";
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