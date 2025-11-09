package operations;

import functions.TabulatedFunction;
import functions.Point;
import functions.factory.TabulatedFunctionFactory;
import functions.factory.ArrayTabulatedFunctionFactory;
import exceptions.InconsistentFunctionsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabulatedFunctionOperationService {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedFunctionOperationService.class);
    private TabulatedFunctionFactory factory;

    public TabulatedFunctionOperationService() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedFunctionOperationService(TabulatedFunctionFactory factory) {
        if (factory == null) {
            logger.error("Фабрика не может быть null");
            throw new IllegalArgumentException("Factory cannot be null");
        }
        this.factory = factory;
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        if (factory == null) {
            logger.error("Фабрика не может быть null");
            throw new IllegalArgumentException("Factory cannot be null");
        }
        this.factory = factory;
    }

    public static Point[] asPoints(TabulatedFunction tabulatedFunction) {
        int count = tabulatedFunction.getCount();
        Point[] points = new Point[count];

        int i = 0;
        for (Point point : tabulatedFunction) {
            points[i++] = point;
        }
        return points;
    }

    private interface BiOperation {
        double apply(double u, double v);
    }

    private TabulatedFunction doOperation(TabulatedFunction a, TabulatedFunction b, BiOperation operation) {
        if (a.getCount() != b.getCount()) {
            logger.error("Несовпадение количества точек: a = " + a.getCount() + ", b = " + b.getCount());
            throw new InconsistentFunctionsException("Functions have different number of points");
        }

        Point[] pointsA = asPoints(a);
        Point[] pointsB = asPoints(b);

        int count = a.getCount();
        double[] xValues = new double[count];
        double[] yValues = new double[count];

        for (int i = 0; i < count; i++) {
            double delta = Math.abs(pointsA[i].x - pointsB[i].x);
            double max = Math.max(Math.abs(pointsA[i].x), Math.abs(pointsB[i].x));

            if (delta > 1e-10 && delta > 1e-10 * max) {
                logger.error("Несовпадение значений x на индексе " + i + ": " + pointsA[i].x + " vs " + pointsB[i].x);
                throw new InconsistentFunctionsException(
                        String.format("X values don't match at index %d: %.10f vs %.10f", i, pointsA[i].x, pointsB[i].x));
            }

            xValues[i] = pointsA[i].x;
            yValues[i] = operation.apply(pointsA[i].y, pointsB[i].y);
        }

        logger.info("Операция завершена успешно для " + count + " точек");
        return factory.create(xValues, yValues);
    }

    public TabulatedFunction add(TabulatedFunction a, TabulatedFunction b) {
        logger.info("Сложение двух табличных функций");
        return doOperation(a, b, (u, v) -> u + v);
    }

    public TabulatedFunction subtract(TabulatedFunction a, TabulatedFunction b) {
        logger.info("Вычитание табличных функций");
        return doOperation(a, b, (u, v) -> u - v);
    }

    public TabulatedFunction multiply(TabulatedFunction a, TabulatedFunction b) {
        logger.info("Умножение табличных функций");
        return doOperation(a, b, (u, v) -> u * v);
    }

    public TabulatedFunction divide(TabulatedFunction a, TabulatedFunction b) {
        logger.info("Деление табличных функций");
        return doOperation(a, b, (u, v) -> {
            if (Math.abs(v) < 1e-10) {
                logger.error("Обнаружено деление на ноль");
                throw new ArithmeticException("Division by zero");
            }
            return u / v;
        });
    }
}