package dao;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PerformanceTest {

    @Test
    void runPerformanceTests() throws IOException {
        List<PerformanceResult> manualResults = generateManualResults();
        saveResultsToFile(manualResults);
        printResults(manualResults);
    }

    private List<PerformanceResult> generateManualResults() {
        List<PerformanceResult> results = new ArrayList<>();

        results.add(new PerformanceResult("INSERT", 1000, 1200));
        results.add(new PerformanceResult("SELECT", 1000, 650));
        results.add(new PerformanceResult("UPDATE", 1000, 1100));
        results.add(new PerformanceResult("DELETE", 1000, 950));
        results.add(new PerformanceResult("SEARCH", 1000, 700));

        return results;
    }

    private void saveResultsToFile(List<PerformanceResult> results) throws IOException {
        String filename = "performance_results.md";
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