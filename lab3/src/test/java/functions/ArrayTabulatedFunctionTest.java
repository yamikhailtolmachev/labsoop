package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;
import exceptions.InterpolationException;

import java.util.Iterator;
import java.util.NoSuchElementException;

class ArrayTabulatedFunctionTest {

    @Test
    void testConstructorThrowsDifferentLengthOfArraysException() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsArrayIsNotSortedException() {
        double[] xValues = {3.0, 1.0, 2.0};
        double[] yValues = {4.0, 5.0, 6.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsArrayIsNotSortedExceptionForEqualValues() {
        double[] xValues = {1.0, 2.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0, 7.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorWithValidArraysAfterModifications() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        assertDoesNotThrow(() -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testApplyThrowsInterpolationExceptionForXOutsideIntervalLeft() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        assertDoesNotThrow(() -> function.apply(1.5));
    }

    @Test
    void testApplyThrowsInterpolationExceptionForXOutsideIntervalRight() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        assertDoesNotThrow(() -> function.apply(2.5));
    }

    @Test
    void testInterpolateThrowsInterpolationExceptionWhenXOutsideBounds() {
        TestableArrayTabulatedFunction function = new TestableArrayTabulatedFunction(
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
        TestableArrayTabulatedFunction function = new TestableArrayTabulatedFunction(
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
        TestableArrayTabulatedFunction function = new TestableArrayTabulatedFunction(
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

    private static class TestableArrayTabulatedFunction extends ArrayTabulatedFunction {
        public TestableArrayTabulatedFunction(double[] xValues, double[] yValues) {
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
            new ArrayTabulatedFunction(xValues, yValues);
            fail("Should throw IllegalArgumentException for length < 2");
        } catch (IllegalArgumentException e) {
        }

        try {
            double[] xValues = {1.0, 2.0, 3.0};
            double[] yValues = {4.0, 5.0};
            new ArrayTabulatedFunction(xValues, yValues);
            fail("Should throw DifferentLengthOfArraysException");
        } catch (DifferentLengthOfArraysException e) {
        }

        try {
            double[] xValues = {3.0, 1.0, 2.0};
            double[] yValues = {4.0, 5.0, 6.0};
            new ArrayTabulatedFunction(xValues, yValues);
            fail("Should throw ArrayIsNotSortedException");
        } catch (ArrayIsNotSortedException e) {
        }
    }

    @Test
    void testConstructorThrowsForDifferentLengthArraysUpdated() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsForNonStrictlyIncreasingXUpdated() {
        double[] xValues = {1.0, 1.0, 2.0};
        double[] yValues = {3.0, 4.0, 5.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorWithArrays() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(4.0, function.getY(0));
        assertEquals(5.0, function.getY(1));
        assertEquals(6.0, function.getY(2));
    }

    @Test
    void testConstructorWithArraysCopiesData() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        xValues[0] = 100.0;
        yValues[0] = 200.0;

        assertEquals(1.0, function.getX(0));
        assertEquals(4.0, function.getY(0));
    }

    @Test
    void testConstructorWithFunction() {
        MathFunction sqr = new SqrFunction();
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(sqr, 0.0, 2.0, 3);

        assertEquals(3, function.getCount());
        assertEquals(0.0, function.getX(0));
        assertEquals(1.0, function.getX(1));
        assertEquals(2.0, function.getX(2));
        assertEquals(0.0, function.getY(0));
        assertEquals(1.0, function.getY(1));
        assertEquals(4.0, function.getY(2));
    }

    @Test
    void testConstructorWithFunctionReversedBounds() {
        MathFunction sqr = new SqrFunction();
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(sqr, 2.0, 0.0, 3);

        assertEquals(0.0, function.getX(0));
        assertEquals(1.0, function.getX(1));
        assertEquals(2.0, function.getX(2));
    }

    @Test
    void testGetCount() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(2, function.getCount());
    }

    @Test
    void testGetX() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
    }

    @Test
    void testGetY() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(4.0, function.getY(0));
        assertEquals(5.0, function.getY(1));
        assertEquals(6.0, function.getY(2));
    }

    @Test
    void testSetY() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.setY(1, 10.0);

        assertEquals(10.0, function.getY(1));
        assertEquals(4.0, function.getY(0));
        assertEquals(6.0, function.getY(2));
    }

    @Test
    void testIndexOfX() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfX(1.0));
        assertEquals(1, function.indexOfX(2.0));
        assertEquals(2, function.indexOfX(3.0));
        assertEquals(-1, function.indexOfX(4.0));
    }

    @Test
    void testIndexOfY() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfY(4.0));
        assertEquals(1, function.indexOfY(5.0));
        assertEquals(2, function.indexOfY(6.0));
        assertEquals(-1, function.indexOfY(7.0));
    }

    @Test
    void testLeftBound() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(1.0, function.leftBound());
    }

    @Test
    void testRightBound() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(3.0, function.rightBound());
    }

    @Test
    void testFloorIndexOfX() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0, function.floorIndexOfX(1.0));
        assertEquals(0, function.floorIndexOfX(1.5));
        assertEquals(1, function.floorIndexOfX(2.5));
        assertEquals(2, function.floorIndexOfX(3.0));
        assertEquals(2, function.floorIndexOfX(4.0));
    }

    @Test
    void testFloorIndexOfXThrowsForXLessThanLeftBound() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.floorIndexOfX(0.5));
        assertThrows(IllegalArgumentException.class, () -> function.floorIndexOfX(-1.0));
    }

    @Test
    void testApplyExactMatch() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(4.0, function.apply(1.0));
        assertEquals(5.0, function.apply(2.0));
        assertEquals(6.0, function.apply(3.0));
    }

    @Test
    void testApplyInterpolation() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {2.0, 4.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(3.0, function.apply(1.5));
        assertEquals(5.0, function.apply(2.5));
    }

    @Test
    void testApplyExtrapolationLeft() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {2.0, 4.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0.0, function.apply(0.0));
    }

    @Test
    void testApplyExtrapolationRight() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {2.0, 4.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(8.0, function.apply(4.0));
    }

    @Test
    void testConstructorThrowsForInvalidArrays() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsForNonIncreasingX() {
        double[] xValues = {1.0, 1.0, 2.0};
        double[] yValues = {3.0, 4.0, 5.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsForDifferentLengthArrays() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsForNonStrictlyIncreasingX() {
        double[] xValues = {1.0, 1.0, 2.0};
        double[] yValues = {3.0, 4.0, 5.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testGetThrowsForInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.getX(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getX(2));
        assertThrows(IllegalArgumentException.class, () -> function.getY(-1));
        assertThrows(IllegalArgumentException.class, () -> function.getY(2));
    }

    @Test
    void testSetYThrowsForInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.setY(-1, 5.0));
        assertThrows(IllegalArgumentException.class, () -> function.setY(2, 5.0));
    }

    @Test
    void testInsertAtBeginning() {
        double[] xValues = {2.0, 3.0, 4.0};
        double[] yValues = {4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.insert(1.0, 1.0);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(1.0, function.getY(0), 1e-10);
        assertEquals(2.0, function.getX(1), 1e-10);
        assertEquals(4.0, function.getY(1), 1e-10);
    }

    @Test
    void testInsertAtEnd() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.insert(4.0, 16.0);

        assertEquals(4, function.getCount());
        assertEquals(3.0, function.getX(2), 1e-10);
        assertEquals(9.0, function.getY(2), 1e-10);
        assertEquals(4.0, function.getX(3), 1e-10);
        assertEquals(16.0, function.getY(3), 1e-10);
    }

    @Test
    void testInsertInMiddle() {
        double[] xValues = {1.0, 3.0, 4.0};
        double[] yValues = {1.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.insert(2.0, 4.0);

        assertEquals(4, function.getCount());
        assertEquals(1.0, function.getX(0), 1e-10);
        assertEquals(2.0, function.getX(1), 1e-10);
        assertEquals(3.0, function.getX(2), 1e-10);
        assertEquals(4.0, function.getX(3), 1e-10);
        assertEquals(4.0, function.getY(1), 1e-10);
    }

    @Test
    void testInsertUpdatesExistingPoint() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.insert(2.0, 8.0);

        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(1), 1e-10);
        assertEquals(8.0, function.getY(1), 1e-10);
    }

    @Test
    void testInsertMaintainsOrder() {
        double[] xValues = {1.0, 5.0};
        double[] yValues = {1.0, 25.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.insert(3.0, 9.0);
        function.insert(4.0, 16.0);
        function.insert(2.0, 4.0);

        assertEquals(5, function.getCount());

        for (int i = 1; i < function.getCount(); i++) {
            assertTrue(function.getX(i) > function.getX(i - 1), "xValues should be strictly increasing at index " + i);
        }

        double[] expectedX = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] expectedY = {1.0, 4.0, 9.0, 16.0, 25.0};

        for (int i = 0; i < expectedX.length; i++) {
            assertEquals(expectedX[i], function.getX(i));
            assertEquals(expectedY[i], function.getY(i));
        }
    }

    @Test
    void testInsertAndApply() {
        double[] xValues = {0.0, 2.0, 4.0};
        double[] yValues = {0.0, 4.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.insert(1.0, 1.0);

        assertEquals(1.0, function.apply(1.0));
        assertEquals(2.5, function.apply(1.5));
        assertEquals(10.0, function.apply(3.0));
    }

    @Test
    void testRemoveFirstElement() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0);

        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(0));
        assertEquals(20.0, function.getY(0));
        assertEquals(3.0, function.getX(1));
        assertEquals(4.0, function.getX(2));
    }

    @Test
    void testRemoveLastElement() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(3);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(3.0, function.getX(2));
        assertEquals(30.0, function.getY(2));
    }

    @Test
    void testRemoveMiddleElement() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(10.0, function.getY(0));
        assertEquals(3.0, function.getX(1));
        assertEquals(30.0, function.getY(1));
        assertEquals(4.0, function.getX(2));
        assertEquals(40.0, function.getY(2));
    }

    @Test
    void testRemoveMultipleElements() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);
        function.remove(2);

        assertEquals(3, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(3.0, function.getX(1));
        assertEquals(5.0, function.getX(2));
        assertEquals(10.0, function.getY(0));
        assertEquals(30.0, function.getY(1));
        assertEquals(50.0, function.getY(2));
    }

    @Test
    void testRemoveUpdatesBounds() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(0);

        assertEquals(2.0, function.leftBound());
        assertEquals(4.0, function.rightBound());

        function.remove(2);

        assertEquals(2.0, function.leftBound());
        assertEquals(3.0, function.rightBound());
    }

    @Test
    void testRemoveAndInterpolation() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);

        assertEquals(15.0, function.apply(1.5));
        assertEquals(20.0, function.apply(2.0));
    }

    @Test
    void testRemoveInvalidIndex() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(IllegalArgumentException.class, () -> function.remove(-1));
        assertThrows(IllegalArgumentException.class, () -> function.remove(3));
    }

    @Test
    void testRemoveFromMinimumSize() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(IllegalStateException.class, () -> function.remove(0));
        assertThrows(IllegalStateException.class, () -> function.remove(1));

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
    }

    @Test
    void testRemoveMaintainsOrder() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0, 50.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(2);

        for (int i = 1; i < function.getCount(); i++) {
            assertTrue(function.getX(i) > function.getX(i - 1));
        }
    }

    @Test
    void testRemoveAndIndexOfX() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(1);

        assertEquals(-1, function.indexOfX(2.0));
        assertEquals(1, function.indexOfX(3.0));
        assertEquals(2, function.indexOfX(4.0));
    }

    @Test
    void testIteratorWithWhileLoop() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Point point = iterator.next();
            assertEquals(xValues[count], point.x, 1e-9);
            assertEquals(yValues[count], point.y, 1e-9);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testIteratorWithForEachLoop() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        int count = 0;
        for (Point point : function) {
            assertEquals(xValues[count], point.x, 1e-9);
            assertEquals(yValues[count], point.y, 1e-9);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testIteratorNoSuchElementException() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        iterator.next();
        iterator.next();

        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testIteratorHasNextBehavior() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();

        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertTrue(iterator.hasNext());
        iterator.next();
        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorOrder() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        double[] expectedX = {1.0, 2.0, 3.0, 4.0};
        double[] expectedY = {1.0, 4.0, 9.0, 16.0};

        int index = 0;
        for (Point point : function) {
            assertEquals(expectedX[index], point.x, 1e-9);
            assertEquals(expectedY[index], point.y, 1e-9);
            index++;
        }
    }

    @Test
    void testIteratorWithSingleElementFunction() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        assertTrue(iterator.hasNext());
        Point point1 = iterator.next();
        assertEquals(1.0, point1.x, 1e-9);
        assertEquals(3.0, point1.y, 1e-9);

        assertTrue(iterator.hasNext());
        Point point2 = iterator.next();
        assertEquals(2.0, point2.x, 1e-9);
        assertEquals(4.0, point2.y, 1e-9);

        assertFalse(iterator.hasNext());
    }

    @Test
    void testIteratorWithEmptyFunction() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        assertTrue(iterator.hasNext());
    }

    @Test
    void testConstructorWithMathFunctionAndEqualBounds() {
        MathFunction sqr = new SqrFunction();
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(sqr, 2.0, 2.0, 3);

        assertEquals(3, function.getCount());
        assertEquals(2.0, function.getX(0));
        assertEquals(2.0, function.getX(1));
        assertEquals(2.0, function.getX(2));
        assertEquals(4.0, function.getY(0));
        assertEquals(4.0, function.getY(1));
        assertEquals(4.0, function.getY(2));
    }

    @Test
    void testConstructorThrowsForSinglePoint() {
        double[] xValues = {1.0};
        double[] yValues = {2.0};

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testFloorIndexOfXAtExactPoints() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0, function.floorIndexOfX(1.0));
        assertEquals(1, function.floorIndexOfX(2.0));
        assertEquals(2, function.floorIndexOfX(3.0));
        assertEquals(3, function.floorIndexOfX(4.0));
    }

    @Test
    void testFloorIndexOfXAtRightBound() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(2, function.floorIndexOfX(3.5));
        assertEquals(2, function.floorIndexOfX(4.0));
        assertEquals(2, function.floorIndexOfX(100.0));
    }

    @Test
    void testInterpolateAtLastSegment() {
        TestableArrayTabulatedFunction function = new TestableArrayTabulatedFunction(
                new double[]{1.0, 2.0, 3.0},
                new double[]{1.0, 4.0, 9.0}
        );

        double result = function.testInterpolate(2.5, 1);
        assertEquals(6.5, result, 1e-9);
    }

    @Test
    void testInterpolateWhenFloorIndexIsLast() {
        TestableArrayTabulatedFunction function = new TestableArrayTabulatedFunction(
                new double[]{1.0, 2.0, 3.0},
                new double[]{1.0, 4.0, 9.0}
        );

        double result = function.testInterpolate(2.5, 2);
        assertEquals(6.5, result, 1e-9);
    }

    @Test
    void testExtrapolateLeft() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(-2.0, function.apply(0.0), 1e-9);
        assertEquals(-0.5, function.apply(0.5), 1e-9);
    }

    @Test
    void testExtrapolateRight() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(14.0, function.apply(4.0), 1e-9);
        assertEquals(19.0, function.apply(5.0), 1e-9);
    }

    @Test
    void testInsertIntoEmptyFunction() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.insert(0.5, 0.25);
        assertEquals(3, function.getCount());
        assertEquals(0.5, function.getX(0), 1e-9);
        assertEquals(0.25, function.getY(0), 1e-9);

        function.insert(3.0, 9.0);
        assertEquals(4, function.getCount());
        assertEquals(3.0, function.getX(3), 1e-9);
        assertEquals(9.0, function.getY(3), 1e-9);
    }

    @Test
    void testRemoveAllButTwoElements() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        function.remove(2);
        function.remove(1);

        assertEquals(2, function.getCount());
        assertEquals(1.0, function.getX(0));
        assertEquals(4.0, function.getX(1));
    }

    @Test
    void testIteratorRemoveNotSupported() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Iterator<Point> iterator = function.iterator();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    void testInterpolateExactMatch() {
        TestableArrayTabulatedFunction function = new TestableArrayTabulatedFunction(
                new double[]{1.0, 2.0, 3.0},
                new double[]{1.0, 4.0, 9.0}
        );

        assertEquals(1.0, function.testInterpolate(1.0, 0), 1e-9);
        assertEquals(4.0, function.testInterpolate(2.0, 1), 1e-9);
        assertEquals(9.0, function.testInterpolate(3.0, 2), 1e-9);
    }

    @Test
    void testConstructorWithMathFunctionZeroStep() {
        MathFunction identity = x -> x;
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(identity, 0.0, 0.0, 5);

        assertEquals(5, function.getCount());
        for (int i = 0; i < 5; i++) {
            assertEquals(0.0, function.getX(i), 1e-9);
            assertEquals(0.0, function.getY(i), 1e-9);
        }
    }

    @Test
    void testIndexOfXWithPrecision() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfX(1.0 + 1e-13));
        assertEquals(1, function.indexOfX(2.0 - 1e-13));
        assertEquals(-1, function.indexOfX(1.0 + 1e-10));
    }

    @Test
    void testIndexOfYWithPrecision() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertEquals(0, function.indexOfY(1.0 + 1e-13));
        assertEquals(1, function.indexOfY(4.0 - 1e-13));
        assertEquals(-1, function.indexOfY(1.0 + 1e-10));
    }
}