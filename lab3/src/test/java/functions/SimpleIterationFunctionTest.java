package functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class SimpleIterationFunctionTest {

    private MathFunction cosineFunction;
    private MathFunction linearFunction;
    private MathFunction identityFunction;

    @BeforeEach
    void setUp() {
        cosineFunction = x -> Math.cos(x);
        linearFunction = x -> 0.5 * x + 1;
        identityFunction = x -> x;
    }

    @Test
    void testDefaultConstructor() {
        SimpleIterationFunction solver = new SimpleIterationFunction(identityFunction);

        assertNotNull(solver);
        assertEquals(identityFunction, solver.getPhiFunction());
        assertEquals(0.0, solver.getInitialGuess());
        assertEquals(1e-6, solver.getTolerance());
        assertEquals(1000, solver.getMaxIterations());
    }

    @Test
    void testFullConstructor() {
        SimpleIterationFunction solver = new SimpleIterationFunction(cosineFunction, 1.0, 1e-8, 500);

        assertEquals(cosineFunction, solver.getPhiFunction());
        assertEquals(1.0, solver.getInitialGuess());
        assertEquals(1e-8, solver.getTolerance());
        assertEquals(500, solver.getMaxIterations());
    }

    @Test
    void testConstructorWithNullFunction() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SimpleIterationFunction(null));
        assertEquals("Phi function cannot be null", exception.getMessage());
    }

    @Test
    void testConstructorWithNegativeTolerance() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SimpleIterationFunction(identityFunction, 0.0, -1e-6, 1000));
        assertEquals("Tolerance must be positive", exception.getMessage());
    }

    @Test
    void testConstructorWithZeroTolerance() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SimpleIterationFunction(identityFunction, 0.0, 0.0, 1000));
        assertEquals("Tolerance must be positive", exception.getMessage());
    }

    @Test
    void testConstructorWithZeroMaxIterations() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SimpleIterationFunction(identityFunction, 0.0, 1e-6, 0));
        assertEquals("Max iterations must be positive", exception.getMessage());
    }

    @Test
    void testConstructorWithNegativeMaxIterations() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> new SimpleIterationFunction(identityFunction, 0.0, 1e-6, -100));
        assertEquals("Max iterations must be positive", exception.getMessage());
    }

    @Test
    void testSolveCosineEquation() {
        SimpleIterationFunction solver = new SimpleIterationFunction(cosineFunction, 0.5, 1e-8, 1000);

        double result = solver.solve();

        assertEquals(0.7390851332151607, result, 1e-6);
        assertEquals(Math.cos(result), result, 1e-6);
    }

    @Test
    void testSolveLinearEquation() {
        SimpleIterationFunction solver = new SimpleIterationFunction(linearFunction, 0.0, 1e-8, 1000);

        double result = solver.solve();

        assertEquals(2.0, result, 1e-6);
        assertEquals(0.5 * result + 1, result, 1e-6);
    }

    @Test
    void testSolveIdentityEquation() {
        SimpleIterationFunction solver = new SimpleIterationFunction(identityFunction, 5.0, 1e-8, 1000);

        double result = solver.solve();

        assertEquals(5.0, result, 1e-6);
    }

    @Test
    void testApplyMethod() {
        SimpleIterationFunction solver = new SimpleIterationFunction(linearFunction, 0.0, 1e-8, 1000);

        double result = solver.apply(123.45);

        assertEquals(2.0, result, 1e-6);
    }

    @Test
    void testSolveWithDifferentInitialGuesses() {
        SimpleIterationFunction solver = new SimpleIterationFunction(cosineFunction, 0.0, 1e-8, 1000);

        assertEquals(0.7390851332151607, solver.solve(0.5), 1e-6);
        assertEquals(0.7390851332151607, solver.solve(1.0), 1e-6);
        assertEquals(0.7390851332151607, solver.solve(0.0), 1e-6);
        assertEquals(0.7390851332151607, solver.solve(1.5), 1e-6);
        assertEquals(0.7390851332151607, solver.solve(2.0), 1e-6);
    }

    @Test
    void testConvergenceWithHighTolerance() {
        SimpleIterationFunction solver = new SimpleIterationFunction(cosineFunction, 0.5, 0.1, 1000);

        double result = solver.solve();

        assertTrue(Math.abs(Math.cos(result) - result) <= 0.1 + 1e-10);
    }

    @Test
    void testMaximumIterationsLimit() {
        MathFunction slowConvergingFunction = x -> 0.9 * x + 0.1;
        SimpleIterationFunction solver = new SimpleIterationFunction(slowConvergingFunction, 0.0, 1e-12, 10);

        double result = solver.solve();

        assertNotNull(result);
        assertTrue(Double.isFinite(result));
        assertTrue(result < 1.0);
    }

    @Test
    void testGetters() {
        SimpleIterationFunction solver = new SimpleIterationFunction(cosineFunction, 2.0, 1e-10, 200);

        assertEquals(cosineFunction, solver.getPhiFunction());
        assertEquals(2.0, solver.getInitialGuess());
        assertEquals(1e-10, solver.getTolerance());
        assertEquals(200, solver.getMaxIterations());
    }

    @Test
    void testWithConstantFunction() {
        MathFunction constantFunction = x -> 5.0;
        SimpleIterationFunction solver = new SimpleIterationFunction(constantFunction, 0.0, 1e-8, 1000);

        double result = solver.solve();

        assertEquals(5.0, result, 1e-6);
    }

    @Test
    void testWithWellConvergingFunction() {
        MathFunction convergingFunction = x -> 0.25 * x + 0.75;
        SimpleIterationFunction solver = new SimpleIterationFunction(convergingFunction, 0.0, 1e-8, 1000);

        double result = solver.solve();

        assertEquals(1.0, result, 1e-6);
        assertEquals(0.25 * result + 0.75, result, 1e-6);
    }

    @Test
    void testDivergentFunction() {
        MathFunction divergentFunction = x -> 2 * x;
        SimpleIterationFunction solver = new SimpleIterationFunction(divergentFunction, 1.0, 1e-8, 100);

        double result = solver.solve();

        assertTrue(Double.isFinite(result));
        assertTrue(Math.abs(result) > 0);
    }

    @Test
    void testWithOscillatingFunction() {
        MathFunction oscillatingFunction = x -> -x;
        SimpleIterationFunction solver = new SimpleIterationFunction(oscillatingFunction, 1.0, 1e-8, 1000);

        double result = solver.solve();

        assertTrue(Double.isFinite(result));
    }

    @Test
    void testSolveWithDifferentInitialGuessesComparison() {
        SimpleIterationFunction solver1 = new SimpleIterationFunction(cosineFunction, 0.5, 1e-8, 1000);
        SimpleIterationFunction solver2 = new SimpleIterationFunction(cosineFunction, 1.0, 1e-8, 1000);

        double result1 = solver1.solve();
        double result2 = solver2.solve();
        double result3 = solver1.solve(2.0);

        assertEquals(0.7390851332151607, result1, 1e-6);
        assertEquals(result1, result2, 1e-6);
        assertEquals(result1, result3, 1e-6);
    }

    @Test
    void testSolveWithConstructorInitialGuess() {
        SimpleIterationFunction solver = new SimpleIterationFunction(linearFunction, 10.0, 1e-8, 1000);

        double result = solver.solve();

        assertEquals(2.0, result, 1e-6);
    }

    @Test
    void testSolveWithParameterInitialGuess() {
        SimpleIterationFunction solver = new SimpleIterationFunction(linearFunction, 999.0, 1e-8, 1000);

        double result = solver.solve(10.0);

        assertEquals(2.0, result, 1e-6);
    }

    @Test
    void testImmediateConvergence() {
        MathFunction immediateConvergence = x -> 42.0;
        SimpleIterationFunction solver = new SimpleIterationFunction(immediateConvergence, 0.0, 1e-8, 1000);

        double result = solver.solve();

        assertEquals(42.0, result, 1e-6);
    }

    @Test
    void testVerySmallTolerance() {
        SimpleIterationFunction solver = new SimpleIterationFunction(cosineFunction, 0.5, 1e-12, 10000);

        double result = solver.solve();

        assertEquals(0.7390851332151607, result, 1e-10);
        assertEquals(Math.cos(result), result, 1e-12);
    }
}