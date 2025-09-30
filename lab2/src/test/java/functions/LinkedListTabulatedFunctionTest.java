package functions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTabulatedFunctionTest {

    private LinkedListTabulatedFunction functionFromArrays;
    private LinkedListTabulatedFunction functionFromMathFunction;

    @BeforeEach
    void setUp() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        functionFromArrays = new LinkedListTabulatedFunction(xValues, yValues);

        MathFunction sqr = new SqrFunction();
        functionFromMathFunction = new LinkedListTabulatedFunction(sqr, 0.0, 3.0, 4);
    }

    @AfterEach
    void tearDown() {
        functionFromArrays = null;
        functionFromMathFunction = null;
    }

    @Test
    void interpolate() {

        assertEquals(2.5, functionFromArrays.apply(1.5), 1e-10);
        assertEquals(6.5, functionFromArrays.apply(2.5), 1e-10);
    }

    @Test
    void apply() {
        assertEquals(1.0, functionFromArrays.apply(1.0));
        assertEquals(4.0, functionFromArrays.apply(2.0));
        assertEquals(9.0, functionFromArrays.apply(3.0));
        assertEquals(16.0, functionFromArrays.apply(4.0));

        assertEquals(2.5, functionFromArrays.apply(1.5), 1e-10);

        assertEquals(-2.0, functionFromArrays.apply(0.0), 1e-10);
        assertEquals(23.0, functionFromArrays.apply(5.0), 1e-10);
    }

    @Test
    void andThen() {
        MathFunction identity = new IdentityFunction();
        MathFunction composite = functionFromArrays.andThen(identity);

        assertEquals(1.0, composite.apply(1.0));
        assertEquals(4.0, composite.apply(2.0));
        assertEquals(9.0, composite.apply(3.0));
    }

    @Test
    void getCount() {
        assertEquals(4, functionFromArrays.getCount());
        assertEquals(4, functionFromMathFunction.getCount());
    }

    @Test
    void getX() {
        assertEquals(1.0, functionFromArrays.getX(0));
        assertEquals(2.0, functionFromArrays.getX(1));
        assertEquals(3.0, functionFromArrays.getX(2));
        assertEquals(4.0, functionFromArrays.getX(3));

        assertEquals(0.0, functionFromMathFunction.getX(0));
        assertEquals(1.0, functionFromMathFunction.getX(1));
        assertEquals(2.0, functionFromMathFunction.getX(2));
        assertEquals(3.0, functionFromMathFunction.getX(3));
    }

    @Test
    void getY() {
        assertEquals(1.0, functionFromArrays.getY(0));
        assertEquals(4.0, functionFromArrays.getY(1));
        assertEquals(9.0, functionFromArrays.getY(2));
        assertEquals(16.0, functionFromArrays.getY(3));

        assertEquals(0.0, functionFromMathFunction.getY(0));
        assertEquals(1.0, functionFromMathFunction.getY(1));
        assertEquals(4.0, functionFromMathFunction.getY(2));
        assertEquals(9.0, functionFromMathFunction.getY(3));
    }

    @Test
    void setY() {
        functionFromArrays.setY(1, 5.0);
        assertEquals(5.0, functionFromArrays.getY(1));
        assertEquals(5.0, functionFromArrays.apply(2.0));

        functionFromMathFunction.setY(2, 10.0);
        assertEquals(10.0, functionFromMathFunction.getY(2));
        assertEquals(10.0, functionFromMathFunction.apply(2.0));
    }

    @Test
    void indexOfX() {
        assertEquals(1, functionFromArrays.indexOfX(2.0));
        assertEquals(2, functionFromArrays.indexOfX(3.0));
        assertEquals(-1, functionFromArrays.indexOfX(5.0));
        assertEquals(-1, functionFromArrays.indexOfX(0.0));

        assertEquals(0, functionFromMathFunction.indexOfX(0.0));
        assertEquals(1, functionFromMathFunction.indexOfX(1.0));
    }

    @Test
    void indexOfY() {
        assertEquals(1, functionFromArrays.indexOfY(4.0));
        assertEquals(2, functionFromArrays.indexOfY(9.0));
        assertEquals(-1, functionFromArrays.indexOfY(5.0));
        assertEquals(-1, functionFromArrays.indexOfY(0.0));

        assertEquals(0, functionFromMathFunction.indexOfY(0.0));
        assertEquals(1, functionFromMathFunction.indexOfY(1.0));
    }

    @Test
    void leftBound() {
        assertEquals(1.0, functionFromArrays.leftBound());
        assertEquals(0.0, functionFromMathFunction.leftBound());
    }

    @Test
    void rightBound() {
        assertEquals(4.0, functionFromArrays.rightBound());
        assertEquals(3.0, functionFromMathFunction.rightBound());
    }

    @Test
    void floorIndexOfX() {
        assertEquals(0, functionFromArrays.floorIndexOfX(0.5));
        assertEquals(0, functionFromArrays.floorIndexOfX(1.0));
        assertEquals(0, functionFromArrays.floorIndexOfX(1.5));
        assertEquals(1, functionFromArrays.floorIndexOfX(2.0));
        assertEquals(2, functionFromArrays.floorIndexOfX(3.5));
        assertEquals(3, functionFromArrays.floorIndexOfX(4.0));
        assertEquals(3, functionFromArrays.floorIndexOfX(5.0));
    }

    @Test
    void extrapolateLeft() {
        assertEquals(-2.0, functionFromArrays.apply(0.0), 1e-10);
        assertEquals(-5.0, functionFromArrays.apply(-1.0), 1e-10);
    }

    @Test
    void extrapolateRight() {
        assertEquals(23.0, functionFromArrays.apply(5.0), 1e-10);
        assertEquals(30.0, functionFromArrays.apply(6.0), 1e-10);
    }

    @Test
    void testInterpolate() {
        LinkedListTabulatedFunction mockFunction = new LinkedListTabulatedFunction(
                new double[]{0.0, 2.0}, new double[]{0.0, 4.0}
        );

        assertEquals(1.0, mockFunction.apply(0.5));  // (0.5/2)*4 = 1.0
        assertEquals(2.0, mockFunction.apply(1.0));  // (1.0/2)*4 = 2.0
        assertEquals(3.0, mockFunction.apply(1.5));  // (1.5/2)*4 = 3.0
    }

    @Test
    void testConstructorWithInvalidParameters() {
        double[] wrongXValues = {1.0};
        double[] wrongYValues = {1.0};

        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(wrongXValues, wrongYValues);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(new SqrFunction(), 0, 1, 1);
        });
    }

    @Test
    void testConstructorWithReversedBounds() {
        LinkedListTabulatedFunction reversed = new LinkedListTabulatedFunction(
                new IdentityFunction(), 5, 1, 4
        );

        assertEquals(1.0, reversed.leftBound());
        assertEquals(5.0, reversed.rightBound());
        assertEquals(1.0, reversed.getX(0));
        assertEquals(5.0, reversed.getX(3));
    }

    @Test
    void testConstructorWithSinglePointRepeated() {
        LinkedListTabulatedFunction singlePoint = new LinkedListTabulatedFunction(
                new SqrFunction(), 2, 2, 3
        );

        assertEquals(3, singlePoint.getCount());
        assertEquals(2.0, singlePoint.getX(0));
        assertEquals(2.0, singlePoint.getX(1));
        assertEquals(2.0, singlePoint.getX(2));
        assertEquals(4.0, singlePoint.getY(0));
        assertEquals(4.0, singlePoint.getY(1));
        assertEquals(4.0, singlePoint.getY(2));
    }
}