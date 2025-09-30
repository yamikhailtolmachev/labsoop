package functions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    @DisplayName("Test default constructor")
    void testDefaultConstructor() {
        SimpleIterationFunction solver = new SimpleIterationFunction(identityFunction);

        assertNotNull(solver);
        assertEquals(identityFunction, solver.getPhiFunction());
        assertEquals(0.0, solver.getInitialGuess());
        assertEquals(1e-6, solver.getTolerance());
        assertEquals(1000, solver.getMaxIterations());
    }

    @Test
    @DisplayName("Test full constructor")
    void testFullConstructor() {
        SimpleIterationFunction solver = new SimpleIterationFunction(
                cosineFunction, 1.0, 1e-8, 500
        );

        assertEquals(cosineFunction, solver.getPhiFunction());
        assertEquals(1.0, solver.getInitialGuess());
        assertEquals(1e-8, solver.getTolerance());
        assertEquals(500, solver.getMaxIterations());
    }

    @Test
    @DisplayName("Test constructor with null function")
    void testConstructorWithNullFunction() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleIterationFunction(null)
        );
        assertEquals("Phi function cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Test constructor with negative tolerance")
    void testConstructorWithNegativeTolerance() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleIterationFunction(identityFunction, 0.0, -1e-6, 1000)
        );
        assertEquals("Tolerance must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("Test constructor with zero tolerance")
    void testConstructorWithZeroTolerance() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleIterationFunction(identityFunction, 0.0, 0.0, 1000)
        );
        assertEquals("Tolerance must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("Test constructor with zero max iterations")
    void testConstructorWithZeroMaxIterations() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleIterationFunction(identityFunction, 0.0, 1e-6, 0)
        );
        assertEquals("Max iterations must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("Test constructor with negative max iterations")
    void testConstructorWithNegativeMaxIterations() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleIterationFunction(identityFunction, 0.0, 1e-6, -100)
        );
        assertEquals("Max iterations must be positive", exception.getMessage());
    }

    @Test
    @DisplayName("Test solving cos(x) = x equation")
    void testSolveCosineEquation() {
        SimpleIterationFunction solver = new SimpleIterationFunction(
                cosineFunction, 0.5, 1e-8, 1000
        );

        double result = solver.solve();

        assertEquals(0.7390851332151607, result, 1e-6);
        assertEquals(Math.cos(result), result, 1e-6);
    }

    @Test
    @DisplayName("Test solving linear equation")
    void testSolveLinearEquation() {
        SimpleIterationFunction solver = new SimpleIterationFunction(
                linearFunction, 0.0, 1e-8, 1000
        );

        double result = solver.solve();

        assertEquals(2.0, result, 1e-6);
        assertEquals(0.5 * result + 1, result, 1e-6);
    }

    @Test
    @DisplayName("Test solving identity equation")
    void testSolveIdentityEquation() {
        SimpleIterationFunction solver = new SimpleIterationFunction(
                identityFunction, 5.0, 1e-8, 1000
        );

        double result = solver.solve();

        assertEquals(5.0, result, 1e-6);
    }

    @Test
    @DisplayName("Test apply method from MathFunction interface")
    void testApplyMethod() {
        SimpleIterationFunction solver = new SimpleIterationFunction(
                linearFunction, 0.0, 1e-8, 1000
        );

        double result = solver.apply(123.45);

        assertEquals(2.0, result, 1e-6);
    }

    @Test
    @DisplayName("Test solve with different initial guesses")
    void testSolveWithDifferentInitialGuesses() {
        SimpleIterationFunction solver = new SimpleIterationFunction(
                cosineFunction, 0.0, 1e-8, 1000
        );

        double result1 = solver.solve(0.5);
        double result2 = solver.solve(1.0);
        double result3 = solver.solve(0.0);

        assertEquals(0.7390851332151607, result1, 1e-6);
        assertEquals(result1, result2, 1e-6);
        assertEquals(result1, result3, 1e-6);
    }

    @ParameterizedTest
    @DisplayName("Test solve with various initial guesses")
    @ValueSource(doubles = {0.0, 0.5, 1.0, 1.5, 2.0})
    void testSolveWithVariousInitialGuesses(double initialGuess) {
        SimpleIterationFunction solver = new SimpleIterationFunction(
                cosineFunction, 0.0, 1e-8, 1000
        );

        double result = solver.solve(initialGuess);

        assertEquals(0.7390851332151607, result, 1e-6);
    }

    @Test
    @DisplayName("Test convergence with high tolerance")
    void testConvergenceWithHighTolerance() {
        SimpleIterationFunction solver = new SimpleIterationFunction(
                cosineFunction, 0.5, 0.1, 1000
        );

        double result = solver.solve();

        assertTrue(Math.abs(Math.cos(result) - result) <= 0.1);
    }

    @Test
    @DisplayName("Test maximum iterations limit")
    void testMaximumIterationsLimit() {
        MathFunction slowConvergingFunction = x -> 0.9 * x + 0.1;
        SimpleIterationFunction solver = new SimpleIterationFunction(
                slowConvergingFunction, 0.0, 1e-12, 10
        );

        double result = solver.solve();

        assertNotNull(result);
        assertTrue(Double.isFinite(result));
    }

    @Test
    @DisplayName("Test getters")
    void testGetters() {
        SimpleIterationFunction solver = new SimpleIterationFunction(
                cosineFunction, 2.0, 1e-10, 200
        );

        assertEquals(cosineFunction, solver.getPhiFunction());
        assertEquals(2.0, solver.getInitialGuess());
        assertEquals(1e-10, solver.getTolerance());
        assertEquals(200, solver.getMaxIterations());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        SimpleIterationFunction solver = new SimpleIterationFunction(
                identityFunction, 1.0, 1e-8, 100
        );

        String str = solver.toString();
        assertTrue(str.contains("SimpleIterationFunction"));
        assertTrue(str.contains("initialGuess=1.0"));
        assertTrue(str.contains("tolerance=1.0E-8"));
        assertTrue(str.contains("maxIterations=100"));
    }

    @Test
    @DisplayName("Test with constant function")
    void testWithConstantFunction() {
        MathFunction constantFunction = x -> 5.0;
        SimpleIterationFunction solver = new SimpleIterationFunction(
                constantFunction, 0.0, 1e-8, 1000
        );

        double result = solver.solve();

        assertEquals(5.0, result, 1e-6);
    }

    @Test
    @DisplayName("Test with quadratic function")
    void testWithQuadraticFunction() {
        MathFunction quadraticFunction = x -> 0.5 * x * x + 0.5;
        SimpleIterationFunction solver = new SimpleIterationFunction(
                quadraticFunction, 0.5, 1e-8, 1000
        );

        double result = solver.solve();

        assertTrue(result > 0);
        assertEquals(0.5 * result * result + 0.5, result, 1e-6);
    }
}