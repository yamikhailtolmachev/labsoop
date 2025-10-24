package concurrent;

import functions.UnitFunction;
import functions.TabulatedFunction;
import functions.LinkedListTabulatedFunction;
import java.util.ArrayList;
import java.util.List;

public class MultiplyingTaskExecutor {
    public static void main(String[] args) {
        try {
            UnitFunction unitFunction = new UnitFunction();
            TabulatedFunction function = new LinkedListTabulatedFunction(unitFunction, 1, 1000, 1000);

            List<Thread> threads = new ArrayList<>();
            int threadCount = 10;

            for (int i = 0; i < threadCount; i++) {
                MultiplyingTask task = new MultiplyingTask(function);
                Thread thread = new Thread(task, "MultiplyingThread-" + i);
                threads.add(thread);
            }

            System.out.println("Запуск " + threadCount + " потоков умножения...");
            for (Thread thread : threads) {
                thread.start();
            }

            Thread.sleep(2000);

            double expectedValue = 1024.0;
            System.out.println("Ожидаемое значение: " + expectedValue);

            for (int i = 0; i < Math.min(5, function.getCount()); i++) {
                double actual = function.getY(i);
                System.out.printf("Точка %d: Y=%.1f %s%n",
                        i, actual,
                        Math.abs(actual - expectedValue) < 0.001 ? "✓" : "✗");
            }

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
}