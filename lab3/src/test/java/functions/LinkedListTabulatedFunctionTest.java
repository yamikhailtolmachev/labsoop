package functions;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;

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
    void testConstructorThrowsDifferentLengthOfArraysException() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsArrayIsNotSortedException() {
        double[] xValues = {3.0, 1.0, 2.0};
        double[] yValues = {4.0, 5.0, 6.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsArrayIsNotSortedExceptionForEqualValues() {
        double[] xValues = {1.0, 2.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0, 7.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorWithValidArraysAfterModifications() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        assertDoesNotThrow(() -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testInterpolateThrowsInterpolationExceptionWhenXOutsideBounds() {
        TestableLinkedListTabulatedFunction function = new TestableLinkedListTabulatedFunction(
                new double[]{1.0, 2.0, 3.0},
                new double[]{4.0, 5.0, 6.0}
        );

        assertThrows(InterpolationException.class, () -> {
            function.testInterpolate(0.5, 0);
        });

        assertThrows(InterpolationException.class, () -> {
            function.testInterpolate(2.5, 0);
        });
    }

    @Test
    void testInterpolateDoesNotThrowWhenXInsideBounds() {
        TestableLinkedListTabulatedFunction function = new TestableLinkedListTabulatedFunction(
                new double[]{1.0, 2.0, 3.0},
                new double[]{4.0, 5.0, 6.0}
        );

        assertDoesNotThrow(() -> {
            function.testInterpolate(1.5, 0);
        });

        assertDoesNotThrow(() -> {
            function.testInterpolate(2.5, 1);
        });
    }

    @Test
    void testInterpolateAtBounds() {
        TestableLinkedListTabulatedFunction function = new TestableLinkedListTabulatedFunction(
                new double[]{1.0, 2.0, 3.0},
                new double[]{4.0, 5.0, 6.0}
        );

        assertDoesNotThrow(() -> {
            function.testInterpolate(1.0, 0);
        });

        assertDoesNotThrow(() -> {
            function.testInterpolate(2.0, 0);
        });
    }

    private static class TestableLinkedListTabulatedFunction extends LinkedListTabulatedFunction {
        public TestableLinkedListTabulatedFunction(double[] xValues, double[] yValues) {
            super(xValues, yValues);
        }

        public double testInterpolate(double x, int floorIndex) {
            return super.interpolate(x, floorIndex);
        }
    }

    @Test
    void testValidationOrder() {
        try {
            double[] xValues = {1.0};
            double[] yValues = {2.0};
            new LinkedListTabulatedFunction(xValues, yValues);
            fail("Should throw IllegalArgumentException for length < 2");
        } catch (IllegalArgumentException e) {}

        try {
            double[] xValues = {1.0, 2.0, 3.0};
            double[] yValues = {4.0, 5.0};
            new LinkedListTabulatedFunction(xValues, yValues);
            fail("Should throw DifferentLengthOfArraysException");
        } catch (DifferentLengthOfArraysException e) {}

        try {
            double[] xValues = {3.0, 1.0, 2.0};
            double[] yValues = {4.0, 5.0, 6.0};
            new LinkedListTabulatedFunction(xValues, yValues);
            fail("Should throw ArrayIsNotSortedException");
        } catch (ArrayIsNotSortedException e) {}
    }

    @Test
    void interpolate() {
        assertEquals(2.5, functionFromArrays.apply(1.5));
        assertEquals(6.5, functionFromArrays.apply(2.5));
    }

    @Test
    void apply() {
        assertEquals(1.0, functionFromArrays.apply(1.0));
        assertEquals(4.0, functionFromArrays.apply(2.0));
        assertEquals(9.0, functionFromArrays.apply(3.0));
        assertEquals(16.0, functionFromArrays.apply(4.0));

        assertEquals(2.5, functionFromArrays.apply(1.5));
        assertEquals(6.5, functionFromArrays.apply(2.5));

        assertEquals(-2.0, functionFromArrays.apply(0.0));
        assertEquals(23.0, functionFromArrays.apply(5.0));
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
    void testGetXThrowsForInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> functionFromArrays.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> functionFromArrays.getX(10));
    }

    @Test
    void testGetYThrowsForInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> functionFromArrays.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> functionFromArrays.getY(10));
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
    void testSetYThrowsForInvalidIndex() {
        assertThrows(IllegalArgumentException.class, () -> functionFromArrays.setY(-1, 5.0));
        assertThrows(IllegalArgumentException.class, () -> functionFromArrays.setY(10, 5.0));
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
        assertEquals(0, functionFromArrays.floorIndexOfX(1.0));
        assertEquals(0, functionFromArrays.floorIndexOfX(1.5));
        assertEquals(1, functionFromArrays.floorIndexOfX(2.0));
        assertEquals(2, functionFromArrays.floorIndexOfX(3.5));
        assertEquals(3, functionFromArrays.floorIndexOfX(4.0));
        assertEquals(3, functionFromArrays.floorIndexOfX(5.0));
    }

    @Test
    void testFloorIndexOfXThrowsForXLessThanLeftBound() {
        assertThrows(IllegalArgumentException.class, () -> functionFromArrays.floorIndexOfX(0.5));
        assertThrows(IllegalArgumentException.class, () -> functionFromArrays.floorIndexOfX(-1.0));
    }

    @Test
    void extrapolateLeft() {
        assertEquals(-2.0, functionFromArrays.apply(0.0));
        assertEquals(-5.0, functionFromArrays.apply(-1.0));
    }

    @Test
    void extrapolateRight() {
        assertEquals(23.0, functionFromArrays.apply(5.0));
        assertEquals(30.0, functionFromArrays.apply(6.0));
    }

    @Test
    void testInterpolate() {
        LinkedListTabulatedFunction mockFunction = new LinkedListTabulatedFunction(new double[]{0.0, 2.0}, new double[]{0.0, 4.0});

        assertEquals(1.0, mockFunction.apply(0.5));
        assertEquals(2.0, mockFunction.apply(1.0));
        assertEquals(3.0, mockFunction.apply(1.5));
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
        LinkedListTabulatedFunction reversed = new LinkedListTabulatedFunction(new IdentityFunction(), 5, 1, 4);

        assertEquals(1.0, reversed.leftBound());
        assertEquals(5.0, reversed.rightBound());
        assertEquals(1.0, reversed.getX(0));
        assertEquals(5.0, reversed.getX(3));
    }

    @Test
    void testConstructorWithSinglePointRepeated() {
        LinkedListTabulatedFunction singlePoint = new LinkedListTabulatedFunction(new SqrFunction(), 2, 2, 3);

        assertEquals(3, singlePoint.getCount());
        assertEquals(2.0, singlePoint.getX(0));
        assertEquals(2.0, singlePoint.getX(1));
        assertEquals(2.0, singlePoint.getX(2));
        assertEquals(4.0, singlePoint.getY(0));
        assertEquals(4.0, singlePoint.getY(1));
        assertEquals(4.0, singlePoint.getY(2));
    }

    @Test
    void testConstructorThrowsForLessThan2Points() {
        double[] xValues = {1.0};
        double[] yValues = {2.0};

        assertThrows(IllegalArgumentException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsForDifferentLengthArrays() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsForNonStrictlyIncreasingX() {
        double[] xValues = {1.0, 1.0, 2.0};
        double[] yValues = {3.0, 4.0, 5.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new LinkedListTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testFunctionWorksCorrectlyAfterAllModifications() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};

        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.leftBound());
        assertEquals(4.0, function.rightBound());

        assertEquals(2.5, function.apply(1.5));
        assertEquals(6.5, function.apply(2.5));

        assertEquals(-2.0, function.apply(0.0));
        assertEquals(23.0, function.apply(5.0));
    }

    @Test
    void testInsertIntoEmptyList() {
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(new double[]{2.0, 3.0}, new double[]{20.0, 30.0});

        function.insert(1.0, 10.0);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(10.0, function.getY(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
    }

    @Test
    void testInsertAtBeginning() {
        double[] xValues = {2.0, 3.0, 4.0};
        double[] yValues = {20.0, 30.0, 40.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(1.0, 10.0);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(10.0, function.getY(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(20.0, function.getY(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(4.0, function.getX(3));
    }

    @Test
    void testInsertAtEnd() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(4.0, 40.0);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(4.0, function.getX(3));
        assertEquals(40.0, function.getY(3));
    }

    @Test
    void testInsertInMiddle() {
        double[] xValues = {1.0, 3.0, 4.0};
        double[] yValues = {10.0, 30.0, 40.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(2.0, 20.0);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(20.0, function.getY(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(4.0, function.getX(3));
    }

    @Test
    void testInsertReplaceExisting() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(2.0, 25.0);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(25.0, function.getY(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(10.0, function.getY(0));
        assertEquals(30.0, function.getY(2));
    }

    @Test
    void testInsertWithNegativeValues() {
        double[] xValues = {-2.0, 0.0, 2.0};
        double[] yValues = {-20.0, 0.0, 20.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(-1.0, -10.0);
        function.insert(1.0, 10.0);

        assertEquals(5, function.getCount());
        assertEquals(-2.0, function.getX(0));
        assertEquals(-1.0, function.getX(1));
        assertEquals(-10.0, function.getY(1));
        assertEquals(0.0, function.getX(2));
        assertEquals(1.0, function.getX(3));
        assertEquals(10.0, function.getY(3));
        assertEquals(2.0, function.getX(4));
    }

    @Test
    void testInsertWithDuplicateXValues() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(2.0, 10.0);
        function.insert(2.0, 20.0);

        assertEquals(3, function.getCount());
        assertEquals(20.0, function.getY(1));
    }

    @Test
    void testInsertWithFractionalValues() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.5, 2.5};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.insert(1.5, 2.0);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(1.5, function.getX(1));
        assertEquals(2.0, function.getY(1));
        assertEquals(2.0, function.getX(2));
        assertEquals(2.5, function.getY(2));
    }

    @Test
    public void testRemoveFromBeginning() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(0);

        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(0));
        assertEquals(4.0, function.getY(0));
        assertEquals(3.0, function.getX(1));
        assertEquals(4.0, function.getX(2));
        assertEquals(2.0, function.leftBound());
    }

    @Test
    public void testRemoveFromEnd() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(3);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(9.0, function.getY(2));
        assertEquals(3.0, function.rightBound());
    }

    @Test
    public void testRemoveFromMiddle() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(3.0, function.getX(1));
        assertEquals(4.0, function.getX(2));
        assertEquals(9.0, function.getY(1));
    }

    @Test
    public void testRemoveAndApply() {
        double[] xValues = {0.0, 1.0, 2.0, 3.0};
        double[] yValues = {0.0, 1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);

        assertEquals(0.0, function.apply(0.0));
        assertEquals(2.0, function.apply(1.0));
        assertEquals(4.0, function.apply(2.0));
    }

    @Test
    public void testRemoveThrowsForInvalidIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.remove(-1));
        assertThrows(IllegalArgumentException.class, () -> function.remove(3));
    }

    @Test
    public void testRemoveThrowsWhenOnlyTwoPointsLeft() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(0);
        assertEquals(2, function.getCount());

        assertThrows(IllegalStateException.class, () -> function.remove(0));
    }

    @Test
    public void testMultipleRemovals() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);
        function.remove(2);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(3.0, function.getX(1));
        assertEquals(5.0, function.getX(2));
    }

    @Test
    public void testRemoveHeadAndUpdateReferences() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        double originalSecondX = function.getX(1);
        double originalLastX = function.getX(2);

        function.remove(0);

        assertEquals(2, function.getCount());
        assertEquals(originalSecondX, function.getX(0));
        assertEquals(originalLastX, function.getX(1));
        assertEquals(originalSecondX, function.leftBound());
    }

    @Test
    public void testRemoveLastNode() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(2);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(2.0, function.rightBound());
    }

    @Test
    public void testRemoveSingleMiddleNode() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(1);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(3.0, function.getX(1));

        assertEquals(3.0, function.getX(1));
        assertEquals(9.0, function.getY(1));
    }

    @Test
    public void testRemoveAllButTwoNodes() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0, 25.0};
        LinkedListTabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        function.remove(2);
        function.remove(2);
        function.remove(2);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));

        assertThrows(IllegalStateException.class, () -> function.remove(0));
    }
}