package concurrent;

import functions.*;
import functions.ConstantFunction;

public class ReadWriteTaskExecutor {
    public static void main(String[] args) {
        ConstantFunction constantFunction = new ConstantFunction(-1);

        TabulatedFunction tabulatedFunction = new LinkedListTabulatedFunction(
                constantFunction, 1, 1000, 1000
        );

        ReadTask readTask = new ReadTask(tabulatedFunction);
        WriteTask writeTask = new WriteTask(tabulatedFunction, 0.5);

        Thread readThread = new Thread(readTask);
        Thread writeThread = new Thread(writeTask);

        readThread.start();
        writeThread.start();

        try {
            readThread.join();
            writeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}