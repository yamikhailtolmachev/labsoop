package concurrent;

import functions.TabulatedFunction;
import functions.Point;
import operations.TabulatedFunctionOperationService;
import java.util.Iterator;

public class SynchronizedTabulatedFunction implements TabulatedFunction {
    private final TabulatedFunction function;
    private final Object lock;

    public SynchronizedTabulatedFunction(TabulatedFunction function) {
        this.function = function;
        this.lock = this;
    }

    @Override
    public int getCount() {
        synchronized (lock) {
            return function.getCount();
        }
    }

    @Override
    public double getX(int index) {
        synchronized (lock) {
            return function.getX(index);
        }
    }

    @Override
    public double getY(int index) {
        synchronized (lock) {
            return function.getY(index);
        }
    }

    @Override
    public void setY(int index, double value) {
        synchronized (lock) {
            function.setY(index, value);
        }
    }

    @Override
    public int indexOfX(double x) {
        synchronized (lock) {
            return function.indexOfX(x);
        }
    }

    @Override
    public int indexOfY(double y) {
        synchronized (lock) {
            return function.indexOfY(y);
        }
    }

    @Override
    public double leftBound() {
        synchronized (lock) {
            return function.leftBound();
        }
    }

    @Override
    public double rightBound() {
        synchronized (lock) {
            return function.rightBound();
        }
    }

    @Override
    public double apply(double x) {
        synchronized (lock) {
            return function.apply(x);
        }
    }

    @Override
    public Iterator<Point> iterator() {
        synchronized (lock) {
            Point[] pointsCopy = TabulatedFunctionOperationService.asPoints(function);

            return new Iterator<Point>() {
                private int currentIndex = 0;
                private final Point[] copy = pointsCopy;

                @Override
                public boolean hasNext() {
                    return currentIndex < copy.length;
                }

                @Override
                public Point next() {
                    return copy[currentIndex++];
                }
            };
        }
    }
}