package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BSplineFunctionTest {
    @Test
    void testConstantSpline() {
        double[] knots = {0, 1, 2, 3};
        double[] coefficients = {1.0, 2.0, 3.0};
        BSplineFunction spline = new BSplineFunction(knots, coefficients, 0);

        assertEquals(1.0, spline.apply(0.5));
        assertEquals(2.0, spline.apply(1.5));
        assertEquals(3.0, spline.apply(2.5));
        assertEquals(0.0, spline.apply(3.5));
        assertEquals(0.0, spline.apply(-0.5));
    }

    @Test
    void testLinearSpline() {
        double[] knots = {0, 1, 2, 3};
        double[] coefficients = {1.0, 2.0};
        BSplineFunction spline = new BSplineFunction(knots, coefficients, 1);

        assertTrue(spline.apply(1.0) > 0);
        assertTrue(spline.apply(1.5) > 0);
        assertTrue(spline.apply(2.0) > 0);

        assertEquals(0.0, spline.apply(0.5));
        assertEquals(0.0, spline.apply(2.5));
    }

    @Test
    void testQuadraticSpline() {
        double[] knots = {0, 0, 0, 1, 2, 3, 3, 3};
        double[] coefficients = {1.0, 2.0, 3.0, 4.0, 5.0};
        BSplineFunction spline = new BSplineFunction(knots, coefficients, 2);

        assertEquals(1.0, spline.apply(0.0), 1e-10);
        assertEquals(0.0, spline.apply(-1.0));
        assertTrue(spline.apply(1.5) > 0);
    }

    @Test
    void testSingleSegment() {
        double[] knots = {0, 1, 2};
        double[] coefficients = {5.0};
        BSplineFunction spline = new BSplineFunction(knots, coefficients, 1);

        assertEquals(5.0, spline.apply(1.0));
        assertEquals(0.0, spline.apply(0.5));
        assertEquals(0.0, spline.apply(1.5));
        assertEquals(0.0, spline.apply(2.5));
    }

    @Test
    void testSingleSegmentWithRepeatedKnots() {
        double[] knots = {0, 0, 1, 1};
        double[] coefficients = {5.0, 3.0};
        BSplineFunction spline = new BSplineFunction(knots, coefficients, 1);

        assertTrue(spline.apply(0.5) > 0);
        assertEquals(0.0, spline.apply(-0.5));
        assertEquals(0.0, spline.apply(1.5));
    }

    @Test
    void testValidation() {
        double[] knots = {0, 1, 2};
        double[] coefficients = {1.0};

        assertDoesNotThrow(() -> new BSplineFunction(knots, coefficients, 1));

        assertThrows(IllegalArgumentException.class, () -> {
            new BSplineFunction(null, coefficients, 1);
        }, "Knots cannot be null");

        assertThrows(IllegalArgumentException.class, () -> {
            new BSplineFunction(knots, null, 1);
        }, "Coefficients cannot be null");

        assertThrows(IllegalArgumentException.class, () -> {
            new BSplineFunction(knots, coefficients, -1);
        }, "Degree must be non-negative");

        assertThrows(IllegalArgumentException.class, () -> {
            new BSplineFunction(knots, coefficients, 2);
        }, "Not enough knots for the given degree");

        double[] invalidCoeffs = {1.0, 2.0};
        assertThrows(IllegalArgumentException.class, () -> {
            new BSplineFunction(knots, invalidCoeffs, 1);
        }, "Number of coefficients must equal knots.length - degree - 1");
    }

    @Test
    void testBasicLinearCase() {
        double[] knots = {0, 1, 2, 3, 4};
        double[] coefficients = {1.0, 2.0, 1.0};
        BSplineFunction spline = new BSplineFunction(knots, coefficients, 1);

        assertTrue(spline.apply(1.5) > 0);
        assertTrue(spline.apply(2.5) > 0);
        assertEquals(0.0, spline.apply(0.5));
        assertEquals(0.0, spline.apply(3.5));
    }

    @Test
    void testZeroOutsideDomain() {
        double[] knots = {0, 1, 2, 3};
        double[] coefficients = {1.0, 2.0, 3.0};
        BSplineFunction spline = new BSplineFunction(knots, coefficients, 0);

        assertEquals(0.0, spline.apply(-1.0));
        assertEquals(0.0, spline.apply(3.5));
        assertEquals(0.0, spline.apply(10.0));
    }

    @Test
    void testDeBoorEdgeCases() {
        double[] knots1 = {0, 0, 1, 2};
        double[] coefficients1 = {1.0, 2.0};
        BSplineFunction spline1 = new BSplineFunction(knots1, coefficients1, 1);

        double result1 = spline1.apply(0.5);
        assertTrue(result1 >= 0);

        double[] knots2 = {0, 1, 2, 2};
        double[] coefficients2 = {1.0, 2.0};
        BSplineFunction spline2 = new BSplineFunction(knots2, coefficients2, 1);

        double result2 = spline2.apply(1.5);
        assertTrue(result2 >= 0);

        double[] knots3 = {0, 1, 2, 3};
        double[] coefficients3 = {1.0, 2.0};
        BSplineFunction spline3 = new BSplineFunction(knots3, coefficients3, 1);

        double result3 = spline3.apply(1.5);
        assertTrue(result3 > 0);
    }

    @Test
    void testBoundaryValues() {
        double[] knots = {0, 1, 2, 3};
        double[] coefficients = {1.0, 2.0, 3.0};
        BSplineFunction spline = new BSplineFunction(knots, coefficients, 0);

        assertEquals(1.0, spline.apply(0.0));
        assertEquals(1.0, spline.apply(0.999));
        assertEquals(2.0, spline.apply(1.0));
        assertEquals(2.0, spline.apply(1.999));
        assertEquals(3.0, spline.apply(2.0));
        assertEquals(3.0, spline.apply(2.999));
    }

    @Test
    void testHigherDegreeSpline() {
        double[] knots = {0, 0, 0, 0, 1, 2, 3, 3, 3, 3};
        double[] coefficients = {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};
        BSplineFunction spline = new BSplineFunction(knots, coefficients, 3);

        double result1 = spline.apply(0.5);
        double result2 = spline.apply(1.5);
        double result3 = spline.apply(2.5);

        assertTrue(result1 >= 0);
        assertTrue(result2 >= 0);
        assertTrue(result3 >= 0);
        assertEquals(0.0, spline.apply(-0.5));
        assertEquals(0.0, spline.apply(3.5));
    }
}