package operations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import functions.MathFunction;
import functions.SqrFunction;

class LeftSteppingDifferentialOperatorTest {

    private static class QuadraticFunction implements MathFunction {
        @Override
        public double apply(double x) {
            return x * x;
        }
    }

    private static class LinearFunction implements MathFunction {
        @Override
        public double apply(double x) {
            return 3 * x + 2;
        }
    }

    private static class CubicFunction implements MathFunction {
        @Override
        public double apply(double x) {
            return x * x * x;
        }
    }

    private static class ConstantFunction implements MathFunction {
        @Override
        public double apply(double x) {
            return 5.0;
        }
    }

    @Test
    void testDeriveWithQuadraticFunction() {
        double step = 0.001;
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(step);
        QuadraticFunction function = new QuadraticFunction();

        MathFunction derivative = operator.derive(function);

        assertEquals(4.0, derivative.apply(2.0), 0.02);
        assertEquals(0.0, derivative.apply(0.0), 0.02);
        assertEquals(-2.0, derivative.apply(-1.0), 0.02);
        assertEquals(6.0, derivative.apply(3.0), 0.02);
    }

    @Test
    void testDeriveWithLinearFunction() {
        double step = 0.001;
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(step);
        LinearFunction function = new LinearFunction();

        MathFunction derivative = operator.derive(function);

        assertEquals(3.0, derivative.apply(0.0), 0.02);
        assertEquals(3.0, derivative.apply(1.0), 0.02);
        assertEquals(3.0, derivative.apply(-1.0), 0.02);
        assertEquals(3.0, derivative.apply(10.0), 0.02);
        assertEquals(3.0, derivative.apply(-10.0), 0.02);
    }

    @Test
    void testDeriveWithCubicFunction() {
        double step = 0.001;
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(step);
        CubicFunction function = new CubicFunction();

        MathFunction derivative = operator.derive(function);

        assertEquals(0.0, derivative.apply(0.0), 0.02);
        assertEquals(3.0, derivative.apply(1.0), 0.02);
        assertEquals(12.0, derivative.apply(2.0), 0.02);
        assertEquals(27.0, derivative.apply(3.0), 0.02);
        assertEquals(48.0, derivative.apply(4.0), 0.03);
    }

    @Test
    void testDeriveWithConstantFunction() {
        double step = 0.001;
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(step);
        ConstantFunction function = new ConstantFunction();

        MathFunction derivative = operator.derive(function);

        assertEquals(0.0, derivative.apply(0.0), 0.02);
        assertEquals(0.0, derivative.apply(1.0), 0.02);
        assertEquals(0.0, derivative.apply(-1.0), 0.02);
        assertEquals(0.0, derivative.apply(100.0), 0.02);
    }

    @Test
    void testDeriveWithSqrFunction() {
        double step = 0.0001;
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(step);
        SqrFunction sqrFunction = new SqrFunction();

        MathFunction derivative = operator.derive(sqrFunction);

        assertEquals(2.0, derivative.apply(1.0), 0.02);
        assertEquals(4.0, derivative.apply(2.0), 0.02);
        assertEquals(6.0, derivative.apply(3.0), 0.02);
        assertEquals(0.0, derivative.apply(0.0), 0.02);
    }

    @Test
    void testDifferentStepSizes() {
        QuadraticFunction function = new QuadraticFunction();
        double testPoint = 2.0;
        double expected = 4.0;

        double[] steps = {0.1, 0.01, 0.001};

        for (double step : steps) {
            LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(step);
            MathFunction derivative = operator.derive(function);
            double result = derivative.apply(testPoint);

            assertEquals(expected, result, step * 15);
        }
    }

    @Test
    void testHighPrecisionWithSmallStep() {
        double step = 0.00001;
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(step);
        CubicFunction function = new CubicFunction();

        MathFunction derivative = operator.derive(function);

        assertEquals(48.0, derivative.apply(4.0), 0.001);
        assertEquals(75.0, derivative.apply(5.0), 0.001);
    }

    @Test
    void testDerivativeIsFunction() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.01);
        QuadraticFunction function = new QuadraticFunction();

        MathFunction derivative = operator.derive(function);

        assertDoesNotThrow(() -> {
            double result1 = derivative.apply(1.0);
            double result2 = derivative.apply(2.0);
            double result3 = derivative.apply(-1.0);
            double result4 = derivative.apply(0.5);

            assertTrue(Double.isFinite(result1));
            assertTrue(Double.isFinite(result2));
            assertTrue(Double.isFinite(result3));
            assertTrue(Double.isFinite(result4));
        });
    }

    @Test
    void testMultipleDerivativeCalls() {
        LeftSteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.01);
        QuadraticFunction function = new QuadraticFunction();

        MathFunction derivative1 = operator.derive(function);
        MathFunction derivative2 = operator.derive(function);

        assertEquals(derivative1.apply(2.0), derivative2.apply(2.0), 1e-10);
        assertEquals(derivative1.apply(5.0), derivative2.apply(5.0), 1e-10);
        assertEquals(derivative1.apply(-3.0), derivative2.apply(-3.0), 1e-10);
    }
}