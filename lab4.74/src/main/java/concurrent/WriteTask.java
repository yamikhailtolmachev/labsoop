package concurrent;

import functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WriteTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WriteTask.class);
    private final TabulatedFunction function;
    private final double value;
    private final Object lock;

    public WriteTask(TabulatedFunction function, double value, Object lock) {
        this.function = function;
        this.value = value;
        this.lock = lock;
    }

    @Override
    public void run() {
        logger.info("Запуск WriteTask со значением " + value);

        for (int i = 0; i < function.getCount(); i++) {
            synchronized (lock) {
                function.setY(i, value);
                System.out.printf("Writing for index %d complete%n", i);
            }
        }

        logger.info("Завершение WriteTask");
    }
}