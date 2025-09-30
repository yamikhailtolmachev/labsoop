package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AdditionalCompositeFunctionTest {

    @Test
    void testCompositeWithArrayTabulatedFunction() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 2.0, 4.0, 6.0};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(xValues, yValues);

        MathFunction sqr = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(arrayFunc, sqr);

        assertEquals(0.0, composite.apply(0.0));
        assertEquals(4.0, composite.apply(1.0));
        assertEquals(16.0, composite.apply(2.0));
        assertEquals(36.0, composite.apply(3.0));
    }

    @Test
    void testCompositeWithLinkedListTabulatedFunction() {
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};
        TabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(xValues, yValues);

        MathFunction identity = new IdentityFunction();
        CompositeFunction composite = new CompositeFunction(linkedListFunc, identity);

        assertEquals(4.0, composite.apply(-2.0));
        assertEquals(1.0, composite.apply(-1.0));
        assertEquals(0.0, composite.apply(0.0));
        assertEquals(1.0, composite.apply(1.0));
        assertEquals(4.0, composite.apply(2.0));
    }

    @Test
    void testCompositeTwoTabulatedFunctions() {
        double[] xValues1 = {0.0, 1.0, 2.0};
        double[] yValues1 = {0.0, 1.0, 2.0};
        TabulatedFunction func1 = new ArrayTabulatedFunction(xValues1, yValues1);

        double[] xValues2 = {0.0, 1.0, 2.0};
        double[] yValues2 = {0.0, 1.0, 4.0};
        TabulatedFunction func2 = new LinkedListTabulatedFunction(xValues2, yValues2);

        CompositeFunction composite = new CompositeFunction(func1, func2);

        assertEquals(0.0, composite.apply(0.0));
        assertEquals(1.0, composite.apply(1.0));
        assertEquals(4.0, composite.apply(2.0));
    }

    @Test
    void testCompositeTabulatedWithInterpolation() {
        double[] xValues = {0.0, 2.0, 4.0};
        double[] yValues = {0.0, 4.0, 16.0};
        TabulatedFunction tabulatedFunc = new ArrayTabulatedFunction(xValues, yValues);

        MathFunction sqr = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(tabulatedFunc, sqr);

        assertEquals(0.0, composite.apply(0.0));
        assertEquals(16.0, composite.apply(2.0));
        assertEquals(256.0, composite.apply(4.0));

        assertEquals(1.0, composite.apply(1.0), 1e-10);
        assertEquals(36.0, composite.apply(3.0), 1e-10);
    }
}