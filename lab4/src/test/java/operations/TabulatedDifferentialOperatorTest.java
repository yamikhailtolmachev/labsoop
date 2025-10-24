package operations;

import functions.*;
import functions.factory.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TabulatedDifferentialOperatorTest {

    @Test
    void testDeriveWithLinearFunctionArrayFactory() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 3.0, 5.0, 7.0, 9.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());

        TabulatedFunction derivative = operator.derive(function);

        assertEquals(5, derivative.getCount());
        assertEquals(2.0, derivative.getY(0), 1e-10);
        assertEquals(2.0, derivative.getY(1), 1e-10);
        assertEquals(2.0, derivative.getY(2), 1e-10);
        assertEquals(2.0, derivative.getY(3), 1e-10);
        assertEquals(2.0, derivative.getY(4), 1e-10);
    }

    @Test
    void testDeriveWithLinearFunctionLinkedListFactory() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 3.0, 5.0, 7.0, 9.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction derivative = operator.derive(function);

        assertEquals(5, derivative.getCount());
        assertEquals(2.0, derivative.getY(0), 1e-10);
        assertEquals(2.0, derivative.getY(1), 1e-10);
        assertEquals(2.0, derivative.getY(2), 1e-10);
        assertEquals(2.0, derivative.getY(3), 1e-10);
        assertEquals(2.0, derivative.getY(4), 1e-10);

        assertTrue(derivative instanceof LinkedListTabulatedFunction);
    }

    @Test
    void testDeriveWithQuadraticFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0, 16.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.derive(function);

        assertEquals(5, derivative.getCount());
        assertEquals(1.0, derivative.getY(0), 1e-10);
        assertEquals(3.0, derivative.getY(1), 1e-10);
        assertEquals(5.0, derivative.getY(2), 1e-10);
        assertEquals(7.0, derivative.getY(3), 1e-10);
        assertEquals(7.0, derivative.getY(4), 1e-10);
    }

    @Test
    void testDeriveWithConstantFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0, 4.0};
        double[] yValues = {5.0, 5.0, 5.0, 5.0, 5.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction derivative = operator.derive(function);

        assertEquals(5, derivative.getCount());
        assertEquals(0.0, derivative.getY(0), 1e-10);
        assertEquals(0.0, derivative.getY(1), 1e-10);
        assertEquals(0.0, derivative.getY(2), 1e-10);
        assertEquals(0.0, derivative.getY(3), 1e-10);
        assertEquals(0.0, derivative.getY(4), 1e-10);
    }

    @Test
    void testDerivePreservesXValues() {
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.derive(function);

        assertEquals(-2.0, derivative.getX(0), 1e-10);
        assertEquals(-1.0, derivative.getX(1), 1e-10);
        assertEquals(0.0, derivative.getX(2), 1e-10);
        assertEquals(1.0, derivative.getX(3), 1e-10);
        assertEquals(2.0, derivative.getX(4), 1e-10);
    }

    @Test
    void testFactoryGetterAndSetter() {
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        assertTrue(operator.getFactory() instanceof ArrayTabulatedFunctionFactory);

        LinkedListTabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();
        operator.setFactory(newFactory);

        assertSame(newFactory, operator.getFactory());
    }

    @Test
    void testConstructorWithFactory() {
        LinkedListTabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(factory);

        assertSame(factory, operator.getFactory());
    }
}