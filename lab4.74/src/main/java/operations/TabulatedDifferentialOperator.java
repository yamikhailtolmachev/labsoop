package operations;

import functions.Point;
import functions.TabulatedFunction;
import functions.factory.TabulatedFunctionFactory;
import functions.factory.ArrayTabulatedFunctionFactory;
import concurrent.SynchronizedTabulatedFunction;

public class TabulatedDifferentialOperator implements DifferentialOperator<TabulatedFunction> {
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

        return factory.create(xValues, yValues);
    }

    public TabulatedFunction deriveSynchronously(TabulatedFunction function) {
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