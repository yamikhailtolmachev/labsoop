package lab5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewmanReportProcessor {

    public static void main(String[] args) {
        String reportFile = "newman_report.json";
        String outputFile = "API_Performance_Report.md";

        List<RequestTiming> timings = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(reportFile))) {
            String line;
            String currentName = "Unknown Request";

            Pattern namePattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
            Pattern timePattern = Pattern.compile("\"responseTime\"\\s*:\\s*(\\d+)");

            while ((line = reader.readLine()) != null) {
                Matcher nameMatcher = namePattern.matcher(line);
                if (nameMatcher.find()) {
                    currentName = nameMatcher.group(1);
                }

                Matcher timeMatcher = timePattern.matcher(line);
                if (timeMatcher.find()) {
                    long responseTime = Long.parseLong(timeMatcher.group(1));
                    timings.add(new RequestTiming(currentName, responseTime));
                    currentName = "Unknown Request";
                }
            }

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла " + reportFile + ": " + e.getMessage());
            return;
        }

        if (timings.isEmpty()) {
            System.out.println("Не удалось извлечь время выполнения ни для одного запроса из " + reportFile);
            return;
        }

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write("# Отчет о производительности API (Newman)\n\n");
            writer.write("| Название запроса | Время выполнения (мс) |\n");
            writer.write("|------------------|------------------------|\n");

            long totalTime = 0;
            for (RequestTiming timing : timings) {
                writer.write(String.format("| %s | %d |\n", timing.name, timing.timeMs));
                totalTime += timing.timeMs;
            }

            writer.write("\n");
            writer.write(String.format("**Общее время выполнения всех запросов:** %d мс\n", totalTime));
            writer.write(String.format("**Количество запросов:** %d\n", timings.size()));

        } catch (IOException e) {
            System.err.println("Ошибка при записи файла " + outputFile + ": " + e.getMessage());
            return;
        }

        System.out.println("Отчет успешно создан: " + outputFile);
    }

    static class RequestTiming {
        String name;
        long timeMs;

        RequestTiming(String name, long timeMs) {
            this.name = name;
            this.timeMs = timeMs;
        }
    }
}