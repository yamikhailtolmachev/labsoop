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
            new BSplineFunction(knots, coefficients, 2);
        });
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
}