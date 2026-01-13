package concurrent;

import functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiplyingTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MultiplyingTask.class);
    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        logger.info("Запуск MultiplyingTask в потоке " + Thread.currentThread().getName());

        for (int i = 0; i < function.getCount(); i++) {
            synchronized (function) {
                double currentY = function.getY(i);
                function.setY(i, currentY * 2);
            }
        }

        logger.info("Поток " + Thread.currentThread().getName() + " завершил задачу");
        System.out.println("Thread " + Thread.currentThread().getName() + " completed the task");
    }
}