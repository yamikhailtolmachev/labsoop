package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ArrayTabulatedFunctionTest {

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
    void testConstructorWithFunctionSinglePoint() {
        MathFunction sqr = new SqrFunction();
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(sqr, 5.0, 5.0, 3);

        assertEquals(5.0, function.getX(0));
        assertEquals(5.0, function.getX(1));
        assertEquals(5.0, function.getX(2));
        assertEquals(25.0, function.getY(0));
        assertEquals(25.0, function.getY(1));
        assertEquals(25.0, function.getY(2));
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

        assertEquals(0, function.floorIndexOfX(0.5));
        assertEquals(0, function.floorIndexOfX(1.0));
        assertEquals(0, function.floorIndexOfX(1.5));
        assertEquals(1, function.floorIndexOfX(2.5));
        assertEquals(2, function.floorIndexOfX(3.0));
        assertEquals(3, function.floorIndexOfX(4.0));
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

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testConstructorThrowsForNonIncreasingX() {
        double[] xValues = {1.0, 1.0, 2.0};
        double[] yValues = {3.0, 4.0, 5.0};

        assertThrows(IllegalArgumentException.class, () -> {
            new ArrayTabulatedFunction(xValues, yValues);
        });
    }

    @Test
    void testGetThrowsForInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(IndexOutOfBoundsException.class, () -> function.getX(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> function.getX(2));
        assertThrows(IndexOutOfBoundsException.class, () -> function.getY(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> function.getY(2));
    }

    @Test
    void testSetYThrowsForInvalidIndex() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        ArrayTabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        assertThrows(IndexOutOfBoundsException.class, () -> function.setY(-1, 5.0));
        assertThrows(IndexOutOfBoundsException.class, () -> function.setY(2, 5.0));
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

        assertThrows(IndexOutOfBoundsException.class, () -> function.remove(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> function.remove(3));
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
}