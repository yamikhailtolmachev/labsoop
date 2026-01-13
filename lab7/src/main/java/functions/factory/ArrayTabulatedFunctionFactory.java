package functions.factory;

import functions.ArrayTabulatedFunction;
import functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArrayTabulatedFunctionFactory implements TabulatedFunctionFactory {
    private static final Logger logger = LoggerFactory.getLogger(ArrayTabulatedFunctionFactory.class);

    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        logger.info("Создание ArrayTabulatedFunction через фабрику с " + xValues.length + " точками");
        return new ArrayTabulatedFunction(xValues, yValues);
    }
}