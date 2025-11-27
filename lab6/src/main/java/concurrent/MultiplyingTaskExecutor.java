package concurrent;

import functions.UnitFunction;
import functions.TabulatedFunction;
import functions.LinkedListTabulatedFunction;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiplyingTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(MultiplyingTaskExecutor.class);

    public static void main(String[] args) throws InterruptedException {
        logger.info("Запуск MultiplyingTaskExecutor");

        UnitFunction unitFunction = new UnitFunction();
        TabulatedFunction function = new LinkedListTabulatedFunction(unitFunction, 1, 1000, 1000);

        List<Thread> threads = new ArrayList<>();
        int threadCount = 10;

        logger.info("Создание " + threadCount + " потоков для умножения");

        for (int i = 0; i < threadCount; i++) {
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(2000);

        logger.info("Вывод результата функции");
        System.out.println(function);

        logger.info("Завершение MultiplyingTaskExecutor");
    }
}