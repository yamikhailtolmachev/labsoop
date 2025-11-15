package concurrent;

import functions.*;
import functions.ConstantFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReadWriteTaskExecutor {
    private static final Logger logger = LoggerFactory.getLogger(ReadWriteTaskExecutor.class);

    public static void main(String[] args) {
        logger.info("Запуск ReadWriteTaskExecutor");

        ConstantFunction constantFunction = new ConstantFunction(-1);

        TabulatedFunction tabulatedFunction = new LinkedListTabulatedFunction(
                constantFunction, 1, 1000, 1000
        );

        Object lock = new Object();

        ReadTask readTask = new ReadTask(tabulatedFunction, lock);
        WriteTask writeTask = new WriteTask(tabulatedFunction, 0.5, lock);

        Thread readThread = new Thread(readTask);
        Thread writeThread = new Thread(writeTask);

        logger.info("Запуск потоков чтения и записи");

        readThread.start();
        writeThread.start();

        try {
            readThread.join();
            writeThread.join();
            logger.info("Оба потока завершили работу");
        } catch (InterruptedException e) {
            logger.error("Прерывание потоков", e);
            e.printStackTrace();
        }

        logger.info("Завершение ReadWriteTaskExecutor");
    }
}