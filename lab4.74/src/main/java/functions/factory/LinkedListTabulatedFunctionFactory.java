package functions.factory;

import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkedListTabulatedFunctionFactory implements TabulatedFunctionFactory {
    private static final Logger logger = LoggerFactory.getLogger(LinkedListTabulatedFunctionFactory.class);

    @Override
    public TabulatedFunction create(double[] xValues, double[] yValues) {
        logger.info("Создание LinkedListTabulatedFunction через фабрику с " + xValues.length + " точками");
        return new LinkedListTabulatedFunction(xValues, yValues);
    }
}