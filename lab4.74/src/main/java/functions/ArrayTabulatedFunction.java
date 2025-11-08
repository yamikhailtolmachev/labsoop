package functions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;
import java.io.Serializable;

public class ArrayTabulatedFunction extends AbstractTabulatedFunction implements Removable, Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ArrayTabulatedFunction.class);
    private static final long serialVersionUID = 1L;

    private double[] xValues;
    private double[] yValues;
    private int count;

    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        logger.info("Создание ArrayTabulatedFunction из массивов, размер: {}", xValues.length);
        checkLengthIsTheSame(xValues, yValues);
        if (xValues.length < 2) {
            logger.error("Попытка создания функции с менее чем 2 точками: {}", xValues.length);
            throw new IllegalArgumentException("At least 2 points are required");
        }
        checkSorted(xValues);

        this.count = xValues.length;
        this.xValues = Arrays.copyOf(xValues, count);
        this.yValues = Arrays.copyOf(yValues, count);
        logger.debug("ArrayTabulatedFunction успешно создана с {} точками", count);
    }

    public ArrayTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {
        logger.info("Создание ArrayTabulatedFunction из функции, интервал: [{}, {}], точек: {}", xFrom, xTo, count);
        if (count < 2) {
            logger.error("Недостаточное количество точек: {}", count);
            throw new IllegalArgumentException("Count must be at least 2");
        }

        this.count = count;
        this.xValues = new double[count];
        this.yValues = new double[count];

        if (xFrom > xTo) {
            logger.debug("Интервал reversed, меняем местами: {} -> {}", xFrom, xTo);
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        if (xFrom == xTo) {
            double value = source.apply(xFrom);
            Arrays.fill(xValues, xFrom);
            Arrays.fill(yValues, value);
            logger.debug("Создана постоянная функция со значением: {}", value);
        } else {
            double step = (xTo - xFrom) / (count - 1);
            for (int i = 0; i < count; i++) {
                xValues[i] = xFrom + i * step;
                yValues[i] = source.apply(xValues[i]);
            }
            logger.debug("Функция дискретизирована с шагом: {}", step);
        }
        logger.info("ArrayTabulatedFunction создана успешно");
    }

    public void insert(double x, double y) {
        logger.debug("Вставка точки: x={}, y={}", x, y);
        int existingIndex = indexOfX(x);
        if (existingIndex != -1) {
            double oldY = getY(existingIndex);
            logger.debug("Точка с x={} уже существует, обновляем y с {} на {}", x, oldY, y);
            yValues[existingIndex] = y;
            return;
        }

        double[] newXValues = new double[count + 1];
        double[] newYValues = new double[count + 1];

        int insertIndex = 0;
        while (insertIndex < count && xValues[insertIndex] < x) {
            insertIndex++;
        }

        System.arraycopy(xValues, 0, newXValues, 0, insertIndex);
        System.arraycopy(yValues, 0, newYValues, 0, insertIndex);

        newXValues[insertIndex] = x;
        newYValues[insertIndex] = y;

        System.arraycopy(xValues, insertIndex, newXValues, insertIndex + 1, count - insertIndex);
        System.arraycopy(yValues, insertIndex, newYValues, insertIndex + 1, count - insertIndex);

        xValues = newXValues;
        yValues = newYValues;
        count++;
        logger.info("Точка вставлена, новый размер: {}", count);
    }

    @Override
    public void remove(int index) {
        logger.warn("Удаление точки с индексом: {}", index);
        if (index < 0 || index >= count) {
            logger.error("Некорректный индекс: {}, размер: {}", index, count);
            throw new IllegalArgumentException("Index: " + index + ", Size: " + count);
        }
        if (count <= 2) {
            logger.error("Попытка удаления при минимальном количестве точек: {}", count);
            throw new IllegalStateException("Cannot remove element - minimum 2 points required");
        }

        double[] newXValues = new double[count - 1];
        double[] newYValues = new double[count - 1];

        System.arraycopy(xValues, 0, newXValues, 0, index);
        System.arraycopy(yValues, 0, newYValues, 0, index);

        System.arraycopy(xValues, index + 1, newXValues, index, count - index - 1);
        System.arraycopy(yValues, index + 1, newYValues, index, count - index - 1);

        this.xValues = newXValues;
        this.yValues = newYValues;
        this.count--;
        logger.info("Точка удалена, новый размер: {}", count);
    }

    @Override
    public int getCount() {
        logger.trace("Получение количества точек: {}", count);
        return count;
    }

    @Override
    public double getX(int index) {
        if (index < 0 || index >= count) {
            logger.error("Индекс вне границ: {}, размер: {}", index, count);
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        double result = xValues[index];
        logger.trace("Получение X[{}] = {}", index, result);
        return result;
    }

    @Override
    public double getY(int index) {
        if (index < 0 || index >= count) {
            logger.error("Индекс вне границ: {}, размер: {}", index, count);
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        double result = yValues[index];
        logger.trace("Получение Y[{}] = {}", index, result);
        return result;
    }

    @Override
    public void setY(int index, double value) {
        if (index < 0 || index >= count) {
            logger.error("Индекс вне границ: {}, размер: {}", index, count);
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        double oldValue = yValues[index];
        yValues[index] = value;
        logger.debug("Изменение Y[{}]: {} -> {}", index, oldValue, value);
    }

    @Override
    public int indexOfX(double x) {
        for (int i = 0; i < count; i++) {
            if (Math.abs(xValues[i] - x) < 1e-12) {
                logger.trace("Найден индекс X={}: {}", x, i);
                return i;
            }
        }
        logger.trace("X={} не найден", x);
        return -1;
    }

    @Override
    public int indexOfY(double y) {
        for (int i = 0; i < count; i++) {
            if (Math.abs(yValues[i] - y) < 1e-12) {
                logger.trace("Найден индекс Y={}: {}", y, i);
                return i;
            }
        }
        logger.trace("Y={} не найден", y);
        return -1;
    }

    @Override
    public double leftBound() {
        double result = xValues[0];
        logger.trace("Левая граница: {}", result);
        return result;
    }

    @Override
    public double rightBound() {
        double result = xValues[count - 1];
        logger.trace("Правая граница: {}", result);
        return result;
    }

    @Override
    protected int floorIndexOfX(double x) {
        if (x < xValues[0]) {
            logger.error("x={} меньше левой границы: {}", x, xValues[0]);
            throw new IllegalArgumentException("x is less than left bound: " + x);
        }
        if (x > xValues[count - 1]) {
            logger.trace("x={} больше правой границы, возвращаем последний индекс", x);
            return count - 1;
        }

        for (int i = 1; i < count; i++) {
            if (x < xValues[i]) {
                logger.trace("floorIndexOfX({}) = {}", x, i - 1);
                return i - 1;
            }
        }
        logger.trace("floorIndexOfX({}) = {}", x, count - 1);
        return count - 1;
    }

    @Override
    protected double extrapolateLeft(double x) {
        double result = interpolate(x, xValues[0], xValues[1], yValues[0], yValues[1]);
        logger.debug("Экстраполяция слева: f({}) = {}", x, result);
        return result;
    }

    @Override
    protected double extrapolateRight(double x) {
        double result = interpolate(x, xValues[count - 2], xValues[count - 1], yValues[count - 2], yValues[count - 1]);
        logger.debug("Экстраполяция справа: f({}) = {}", x, result);
        return result;
    }

    @Override
    protected double interpolate(double x, int floorIndex) {
        if (floorIndex == count - 1) {
            floorIndex--;
        }

        double leftX = xValues[floorIndex];
        double rightX = xValues[floorIndex + 1];

        if (x < leftX || x > rightX) {
            logger.error("x={} вне интервала интерполяции [{}, {}]", x, leftX, rightX);
            throw new InterpolationException("x = " + x + " is outside interpolation interval [" +
                    leftX + ", " + rightX + "]");
        }

        double leftY = yValues[floorIndex];
        double rightY = yValues[floorIndex + 1];
        double result = interpolate(x, leftX, rightX, leftY, rightY);

        logger.debug("Интерполяция: f({}) = {} на интервале [{}, {}]", x, result, leftX, rightX);
        return result;
    }

    @Override
    public Iterator<Point> iterator() {
        logger.trace("Создание итератора для ArrayTabulatedFunction");
        return new Iterator<Point>() {
            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < count;
            }

            @Override
            public Point next() {
                if (!hasNext()) {
                    logger.error("Нет следующего элемента в итераторе");
                    throw new NoSuchElementException("No more elements in the array");
                }
                Point point = new Point(xValues[i], yValues[i]);
                i++;
                logger.trace("Итератор вернул точку: ({}, {})", point.x, point.y);
                return point;
            }
        };
    }

    @Override
    public double apply(double x) {
        logger.debug("Вычисление функции в точке: {}", x);
        double result = super.apply(x);
        logger.debug("Результат apply({}) = {}", x, result);
        return result;
    }
}