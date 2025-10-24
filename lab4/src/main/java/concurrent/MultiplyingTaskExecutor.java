package concurrent;

import functions.UnitFunction;
import functions.TabulatedFunction;
import functions.LinkedListTabulatedFunction;
import java.util.ArrayList;
import java.util.List;

public class MultiplyingTaskExecutor {
    public static void main(String[] args) throws InterruptedException {
        UnitFunction unitFunction = new UnitFunction();
        TabulatedFunction function = new LinkedListTabulatedFunction(unitFunction, 1, 1000, 1000);

        List<Thread> threads = new ArrayList<>();
        int threadCount = 10;

        for (int i = 0; i < threadCount; i++) {
            MultiplyingTask task = new MultiplyingTask(function);
            Thread thread = new Thread(task);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(2000);

        System.out.println(function);
    }
}