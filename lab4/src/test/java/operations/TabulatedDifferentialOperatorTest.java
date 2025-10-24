package operations;

import functions.*;
import functions.factory.*;
import org.junit.jupiter.api.Test;
import concurrent.SynchronizedTabulatedFunction;

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

    @Test
    void testDeriveSynchronouslyWithRegularFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        assertEquals(4, derivative.getCount());
        assertEquals(1.0, derivative.getY(0), 1e-10);
        assertEquals(3.0, derivative.getY(1), 1e-10);
        assertEquals(5.0, derivative.getY(2), 1e-10);
        assertEquals(5.0, derivative.getY(3), 1e-10);
    }

    @Test
    void testDeriveSynchronouslyWithSynchronizedFunction() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {2.0, 4.0, 6.0};

        ArrayTabulatedFunction baseFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.deriveSynchronously(syncFunction);

        assertEquals(3, derivative.getCount());
        assertEquals(2.0, derivative.getY(0), 1e-10);
        assertEquals(2.0, derivative.getY(1), 1e-10);
        assertEquals(2.0, derivative.getY(2), 1e-10);
    }

    @Test
    void testDeriveSynchronouslyEqualsDerive() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 4.0, 8.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivativeSync = operator.deriveSynchronously(function);
        TabulatedFunction derivativeRegular = operator.derive(function);

        assertEquals(derivativeRegular.getCount(), derivativeSync.getCount());
        for (int i = 0; i < derivativeRegular.getCount(); i++) {
            assertEquals(derivativeRegular.getX(i), derivativeSync.getX(i), 1e-10,
                    "X values should be equal at index " + i);
            assertEquals(derivativeRegular.getY(i), derivativeSync.getY(i), 1e-10,
                    "Y values should be equal at index " + i);
        }
    }

    @Test
    void testDeriveSynchronouslyWithLinkedListFactory() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {1.0, 1.0, 1.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        assertTrue(derivative instanceof LinkedListTabulatedFunction);
        assertEquals(3, derivative.getCount());
        assertEquals(0.0, derivative.getY(0), 1e-10);
        assertEquals(0.0, derivative.getY(1), 1e-10);
        assertEquals(0.0, derivative.getY(2), 1e-10);
    }

    @Test
    void testDeriveSynchronouslyPreservesXValues() {
        double[] xValues = {-2.0, 0.0, 2.0};
        double[] yValues = {4.0, 0.0, 4.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        assertEquals(-2.0, derivative.getX(0), 1e-10);
        assertEquals(0.0, derivative.getX(1), 1e-10);
        assertEquals(2.0, derivative.getX(2), 1e-10);
    }

    @Test
    void testDeriveSynchronouslyWithSinglePointFunction() {
        double[] xValues = {1.0};
        double[] yValues = {5.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        assertThrows(IllegalArgumentException.class, () -> {
            operator.deriveSynchronously(function);
        });
    }

    @Test
    void testDeriveSynchronouslyWithLinearFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 2.0, 4.0, 6.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);
        TabulatedDifferentialOperator operator = new TabulatedDifferentialOperator();

        TabulatedFunction derivative = operator.deriveSynchronously(function);

        assertEquals(4, derivative.getCount());
        assertEquals(2.0, derivative.getY(0), 1e-10);
        assertEquals(2.0, derivative.getY(1), 1e-10);
        assertEquals(2.0, derivative.getY(2), 1e-10);
        assertEquals(2.0, derivative.getY(3), 1e-10);
    }

    @Test
    void testDeriveSynchronouslyWithDifferentFactoryTypes() {
        double[] xValues = {0.0, 1.0, 2.0};
        double[] yValues = {0.0, 1.0, 4.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        TabulatedDifferentialOperator operatorArray = new TabulatedDifferentialOperator(new ArrayTabulatedFunctionFactory());
        TabulatedFunction derivativeArray = operatorArray.deriveSynchronously(function);
        assertTrue(derivativeArray instanceof ArrayTabulatedFunction);

        TabulatedDifferentialOperator operatorLinkedList = new TabulatedDifferentialOperator(new LinkedListTabulatedFunctionFactory());
        TabulatedFunction derivativeLinkedList = operatorLinkedList.deriveSynchronously(function);
        assertTrue(derivativeLinkedList instanceof LinkedListTabulatedFunction);
    }
}