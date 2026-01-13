package functions.factory;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionFactoryTest {

    @Test
    void testCreateReturnsLinkedListTabulatedFunction() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        assertTrue(function instanceof LinkedListTabulatedFunction);
    }

    @Test
    void testCreateDoesNotReturnArrayFunction() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        assertFalse(function instanceof ArrayTabulatedFunction);
    }

    @Test
    void testCreatedFunctionIsNotNull() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        assertNotNull(function);
    }

    @Test
    void testCreatedFunctionHasCorrectTypeName() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        TabulatedFunction function = factory.create(xValues, yValues);

        assertEquals("LinkedListTabulatedFunction", function.getClass().getSimpleName());
    }
}