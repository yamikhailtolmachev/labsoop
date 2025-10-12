package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import exceptions.ArrayIsNotSortedException;
import exceptions.DifferentLengthOfArraysException;

import java.util.Iterator;
import java.util.NoSuchElementException;

class AbstractTabulatedFunctionTest {

    @Test
    void testCheckLengthIsTheSameWithValidArrays() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0, 6.0};

        assertDoesNotThrow(() -> {
            AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        });
    }

    @Test
    void testCheckLengthIsTheSameWithDifferentLengths() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        });
    }

    @Test
    void testCheckLengthIsTheSameWithEmptyAndNonEmpty() {
        double[] xValues = {};
        double[] yValues = {4.0};

        assertThrows(DifferentLengthOfArraysException.class, () -> {
            AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        });
    }

    @Test
    void testCheckSortedWithValidSortedArray() {
        double[] sortedValues = {1.0, 2.0, 3.0, 4.0, 5.0};

        assertDoesNotThrow(() -> {
            AbstractTabulatedFunction.checkSorted(sortedValues);
        });
    }

    @Test
    void testCheckSortedWithSingleElement() {
        double[] singleElement = {1.0};

        assertDoesNotThrow(() -> {
            AbstractTabulatedFunction.checkSorted(singleElement);
        });
    }

    @Test
    void testCheckSortedWithEmptyArray() {
        double[] emptyArray = {};

        assertDoesNotThrow(() -> {
            AbstractTabulatedFunction.checkSorted(emptyArray);
        });
    }

    @Test
    void testCheckSortedWithUnsortedArray() {
        double[] unsortedValues = {1.0, 3.0, 2.0, 4.0, 5.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(unsortedValues);
        });
    }

    @Test
    void testCheckSortedWithEqualValues() {
        double[] equalValues = {1.0, 2.0, 2.0, 3.0, 4.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(equalValues);
        });
    }

    @Test
    void testCheckSortedWithDescendingArray() {
        double[] descendingValues = {5.0, 4.0, 3.0, 2.0, 1.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(descendingValues);
        });
    }

    @Test
    void testCheckSortedWithTwoEqualElements() {
        double[] twoEqual = {1.0, 1.0};

        assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(twoEqual);
        });
    }

    @Test
    void testCheckSortedMessageContent() {
        double[] unsortedValues = {1.0, 3.0, 2.0};

        Exception exception = assertThrows(ArrayIsNotSortedException.class, () -> {
            AbstractTabulatedFunction.checkSorted(unsortedValues);
        });

        assertNotNull(exception.getMessage());
    }

    @Test
    void testCheckLengthMessageContent() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {4.0, 5.0};

        Exception exception = assertThrows(DifferentLengthOfArraysException.class, () -> {
            AbstractTabulatedFunction.checkLengthIsTheSame(xValues, yValues);
        });

        assertNotNull(exception.getMessage());
    }

    @Test
    void testInterpolateWithGivenValues() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 3.0, 2.0, 6.0);

        double result = mock.testInterpolate(2.0, 1.0, 3.0, 2.0, 6.0);

        assertEquals(4.0, result, 1e-6);
    }

    @Test
    void testInterpolateAtLeftBound() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 3.0, 2.0, 6.0);

        double result = mock.testInterpolate(1.0, 1.0, 3.0, 2.0, 6.0);

        assertEquals(2.0, result, 1e-6);
    }

    @Test
    void testInterpolateAtRightBound() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 3.0, 2.0, 6.0);

        double result = mock.testInterpolate(3.0, 1.0, 3.0, 2.0, 6.0);

        assertEquals(6.0, result, 1e-6);
    }

    @Test
    void testInterpolateWithNegativeValues() {
        MockTabulatedFunction mock = new MockTabulatedFunction(-2.0, 2.0, -4.0, 4.0);

        double result = mock.testInterpolate(0.0, -2.0, 2.0, -4.0, 4.0);

        assertEquals(0.0, result, 1e-6);
    }

    @Test
    void testApplyWithExactXValue() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 3.0, 2.0, 6.0);

        double result = mock.apply(1.0);

        assertEquals(2.0, result, 1e-6);
    }

    @Test
    void testApplyWithAnotherExactXValue() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 3.0, 2.0, 6.0);

        double result = mock.apply(3.0);

        assertEquals(6.0, result, 1e-6);
    }

    @Test
    void testApplyWithInterpolation() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 3.0, 2.0, 6.0);

        double result = mock.apply(2.0);

        assertEquals(4.0, result, 1e-6);
    }

    @Test
    void testApplyWithExtrapolationLeft() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 3.0, 2.0, 6.0);

        double result = mock.apply(0.0);

        assertEquals(0.0, result, 1e-6);
    }

    @Test
    void testApplyWithExtrapolationRight() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 3.0, 2.0, 6.0);

        double result = mock.apply(4.0);

        assertEquals(8.0, result, 1e-6);
    }

    @Test
    void testApplyWithSinglePointTable() {
        MockTabulatedFunction mock = new MockTabulatedFunction(5.0, 5.0, 10.0, 10.0);

        double result1 = mock.apply(5.0);
        double result2 = mock.apply(0.0);
        double result3 = mock.apply(10.0);

        assertEquals(10.0, result1, 1e-6);
        assertEquals(10.0, result2, 1e-6);
        assertEquals(10.0, result3, 1e-6);
    }

    @Test
    void testMockIterator() {
        MockTabulatedFunction mock = new MockTabulatedFunction(1.0, 3.0, 2.0, 6.0);

        int count = 0;
        for (Point point : mock) {
            if (count == 0) {
                assertEquals(1.0, point.x, 1e-6);
                assertEquals(2.0, point.y, 1e-6);
            } else if (count == 1) {
                assertEquals(3.0, point.x, 1e-6);
                assertEquals(6.0, point.y, 1e-6);
            }
            count++;
        }

        assertEquals(2, count);
    }

    private static class MockTabulatedFunction extends AbstractTabulatedFunction {
        private final double x0;
        private final double x1;
        private final double y0;
        private final double y1;

        public MockTabulatedFunction(double x0, double x1, double y0, double y1) {
            this.x0 = x0;
            this.x1 = x1;
            this.y0 = y0;
            this.y1 = y1;
        }

        public double testInterpolate(double x, double leftX, double rightX, double leftY, double rightY) {
            return interpolate(x, leftX, rightX, leftY, rightY);
        }

        @Override
        public Iterator<Point> iterator() {
            return new Iterator<Point>() {
                private int currentIndex = 0;
                private final Point[] points = {
                        new Point(x0, y0),
                        new Point(x1, y1)
                };

                @Override
                public boolean hasNext() {
                    return currentIndex < points.length;
                }

                @Override
                public Point next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException("No more elements");
                    }
                    return points[currentIndex++];
                }
            };
        }

        @Override
        protected int floorIndexOfX(double x) {
            if (x < x0) return 0;
            if (x > x1) return getCount();
            if (x == x0) return 0;
            return 1;
        }

        @Override
        protected double extrapolateLeft(double x) {
            return interpolate(x, x0, x1, y0, y1);
        }

        @Override
        protected double extrapolateRight(double x) {
            return interpolate(x, x0, x1, y0, y1);
        }

        @Override
        protected double interpolate(double x, int floorIndex) {
            return interpolate(x, x0, x1, y0, y1);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public double getX(int index) {
            if (index == 0) return x0;
            if (index == 1) return x1;
            throw new IllegalArgumentException("Index out of bounds");
        }

        @Override
        public double getY(int index) {
            if (index == 0) return y0;
            if (index == 1) return y1;
            throw new IllegalArgumentException("Index out of bounds");
        }

        @Override
        public void setY(int index, double value) {
            throw new UnsupportedOperationException("Mock object is immutable");
        }

        @Override
        public int indexOfX(double x) {
            if (x == x0) return 0;
            if (x == x1) return 1;
            return -1;
        }

        @Override
        public int indexOfY(double y) {
            if (y == y0) return 0;
            if (y == y1) return 1;
            return -1;
        }

        @Override
        public double leftBound() {
            return x0;
        }

        @Override
        public double rightBound() {
            return x1;
        }
    }
}