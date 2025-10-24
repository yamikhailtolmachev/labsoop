package concurrent;

import functions.TabulatedFunction;

public class MultiplyingTask implements Runnable {
    private final TabulatedFunction function;

    public MultiplyingTask(TabulatedFunction function) {
        this.function = function;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < function.getCount(); i++) {
                double currentY = function.getY(i);
                function.setY(i, currentY * 2);
            }

            System.out.println("Поток " + Thread.currentThread().getName() + " завершил выполнение задачи");

        } catch (Exception e) {
            System.err.println("Ошибка в потоке " + Thread.currentThread().getName() + ": " + e.getMessage());
        }
    }
}