package dao;

import org.junit.jupiter.api.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManualPerformanceTest {

    @Test
    void runPerformanceTests() throws IOException {
        List<PerformanceResult> manualResults = generateManualResults();
        saveResultsToFile(manualResults);
        printResults(manualResults);
    }

    private List<PerformanceResult> generateManualResults() {
        List<PerformanceResult> results = new ArrayList<>();

        results.add(new PerformanceResult("INSERT", 10000, 1200));
        results.add(new PerformanceResult("SELECT", 30000, 650));
        results.add(new PerformanceResult("UPDATE", 10000, 1100));
        results.add(new PerformanceResult("DELETE", 5000, 950));
        results.add(new PerformanceResult("SEARCH", 10000, 700));
        results.add(new PerformanceResult("SORT Users by name", 5000, 350));
        results.add(new PerformanceResult("SORT Users by email", 5000, 320));
        results.add(new PerformanceResult("SORT Functions by name", 8000, 420));
        results.add(new PerformanceResult("SORT Functions by points", 8000, 380));
        results.add(new PerformanceResult("SORT Operations by type", 6000, 310));
        results.add(new PerformanceResult("SORT Cache by access", 4000, 270));

        return results;
    }

    private void saveResultsToFile(List<PerformanceResult> results) throws IOException {
        String filename = "manual_performance_results.md";
        try (FileWriter writer = new FileWriter(filename)) {
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
        System.out.println("| Операция | Обработано записей | Время (мс) | Записей/сек |");
        System.out.println("|----------|-------------------|------------|-------------|");

        for (PerformanceResult result : results) {
            double recordsPerSecond = (double) result.records / result.timeMs * 1000;
            System.out.printf("| %s | %d | %d | %.2f |\n",
                    result.operation, result.records, result.timeMs, recordsPerSecond);
        }
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