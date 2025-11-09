package operations;

import functions.Point;
import functions.TabulatedFunction;
import functions.factory.TabulatedFunctionFactory;
import functions.factory.ArrayTabulatedFunctionFactory;
import concurrent.SynchronizedTabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {
    private static final Logger logger = LoggerFactory.getLogger(TabulatedDifferentialOperator.class);
    private TabulatedFunctionFactory factory;

    public TabulatedDifferentialOperator() {
        this.factory = new ArrayTabulatedFunctionFactory();
    }

    public TabulatedDifferentialOperator(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    public TabulatedFunctionFactory getFactory() {
        return factory;
    }

    public void setFactory(TabulatedFunctionFactory factory) {
        this.factory = factory;
    }

    @Override
    public TabulatedFunction derive(TabulatedFunction function) {
        logger.info("Начало вычисления производной для функции с " + function.getCount() + " точками");

        Point[] points = TabulatedFunctionOperationService.asPoints(function);
        int pointCount = points.length;

        double[] xValues = new double[pointCount];
        double[] yValues = new double[pointCount];

        for (int i = 0; i < pointCount; i++) {
            xValues[i] = points[i].x;
        }

        for (int i = 0; i < pointCount - 1; i++) {
            double deltaX = points[i + 1].x - points[i].x;
            double deltaY = points[i + 1].y - points[i].y;
            yValues[i] = deltaY / deltaX;
        }

        yValues[pointCount - 1] = yValues[pointCount - 2];
        logger.info("Вычисление производной завершено для " + pointCount + " точек");

        return factory.create(xValues, yValues);
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
        logger.info("Начало синхронного вычисления производной");
        SynchronizedTabulatedFunction syncFunction = (function instanceof SynchronizedTabulatedFunction)
                ? (SynchronizedTabulatedFunction) function
                : new SynchronizedTabulatedFunction(function);

        return syncFunction.doSynchronously(new SynchronizedTabulatedFunction.Operation<TabulatedFunction>() {
            @Override
            public TabulatedFunction apply(SynchronizedTabulatedFunction func) {
                return derive(func);
            }
        });
    }
}