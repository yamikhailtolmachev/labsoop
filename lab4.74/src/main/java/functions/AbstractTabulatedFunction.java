package functions;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractTabulatedFunction implements TabulatedFunction {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTabulatedFunction.class);

    protected abstract int floorIndexOfX(double x);
    protected abstract double extrapolateLeft(double x);
    protected abstract double extrapolateRight(double x);
    protected abstract double interpolate(double x, int floorIndex);

    protected double interpolate(double x, double leftX, double rightX, double leftY, double rightY) {
        if (leftX == rightX) {
            return leftY;
        }
        return leftY + (rightY - leftY) / (rightX - leftX) * (x - leftX);
    }

    public static void checkLengthIsTheSame(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length) {
            logger.error("Разная длина массивов: xValues = " + xValues.length + ", yValues = " + yValues.length);
            throw new DifferentLengthOfArraysException("Arrays have different lengths");
        }
        logger.info("Проверка длины массивов пройдена: " + xValues.length);
    }

    public static void checkSorted(double[] xValues) {
        for (int i = 1; i < xValues.length; i++) {
            if (xValues[i] <= xValues[i - 1]) {
                logger.error("Массив не отсортирован на индексе " + i + ": " + xValues[i] + " <= " + xValues[i - 1]);
                throw new ArrayIsNotSortedException("Array is not sorted");
            }
        }
        logger.info("Проверка сортировки массива пройдена");
    }

    @Override
    public double apply(double x) {
        logger.info("Вычисление функции в точке x = " + x);

        if (x < leftBound()) {
            logger.info("Экстраполяция слева для x = " + x);
            return extrapolateLeft(x);
        } else if (x > rightBound()) {
            logger.info("Экстраполяция справа для x = " + x);
            return extrapolateRight(x);
        } else {
            int index = indexOfX(x);
            if (index != -1) {
                logger.info("Точное совпадение найдено на индексе " + index);
                return getY(index);
            } else {
                logger.info("Интерполяция для x = " + x);
                int floorIndex = floorIndexOfX(x);
                return interpolate(x, floorIndex);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getClass().getSimpleName())
                .append(" size = ")
                .append(getCount())
                .append("\n");

        for (Point point : this) {
            sb.append("[")
                    .append(point.x)
                    .append("; ")
                    .append(point.y)
                    .append("]\n");
        }

        logger.info("Преобразование функции в строку: " + getCount() + " точек");
        return sb.toString();
    }
}