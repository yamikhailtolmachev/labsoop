package concurrent;

import org.junit.jupiter.api.Test;
import functions.TabulatedFunction;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.Point;
import operations.TabulatedFunctionOperationService;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class SynchronizedTabulatedFunctionTest {

    @Test
    public void testGetCount() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(3, syncFunction.getCount());
    }

    @Test
    public void testGetX() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(1.0, syncFunction.getX(0));
        assertEquals(2.0, syncFunction.getX(1));
        assertEquals(3.0, syncFunction.getX(2));
    }

    @Test
    public void testGetY() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(4.0, syncFunction.getY(0));
        assertEquals(5.0, syncFunction.getY(1));
        assertEquals(6.0, syncFunction.getY(2));
    }

    @Test
    public void testSetY() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        syncFunction.setY(1, 10.0);
        assertEquals(10.0, syncFunction.getY(1));
    }

    @Test
    public void testIndexOfX() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(1, syncFunction.indexOfX(2));
        assertEquals(-1, syncFunction.indexOfX(5));
    }

    @Test
    public void testIndexOfY() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(1, syncFunction.indexOfY(5));
        assertEquals(-1, syncFunction.indexOfY(10));
    }

    @Test
    public void testLeftBound() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(1.0, syncFunction.leftBound());
    }

    @Test
    public void testRightBound() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(3.0, syncFunction.rightBound());
    }

    @Test
    public void testApply() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(4.5, syncFunction.apply(1.5));
        assertEquals(4.0, syncFunction.apply(1));
        assertEquals(6.0, syncFunction.apply(3));
    }

    @Test
    public void testIterator() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        int count = 0;
        for (Point point : syncFunction) {
            assertNotNull(point);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    public void testIteratorWithDataCopy() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        syncFunction.setY(0, 100.0);
        syncFunction.setY(1, 200.0);
        syncFunction.setY(2, 300.0);

        assertTrue(iterator.hasNext());
        Point point1 = iterator.next();
        assertEquals(1.0, point1.x);
        assertEquals(4.0, point1.y);

        Point point2 = iterator.next();
        assertEquals(2.0, point2.x);
        assertEquals(5.0, point2.y);

        Point point3 = iterator.next();
        assertEquals(3.0, point3.x);
        assertEquals(6.0, point3.y);

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testIteratorDataIsolation() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{1, 2, 3});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator = syncFunction.iterator();

        syncFunction.setY(0, 1000.0);
        syncFunction.setY(1, 2000.0);
        syncFunction.setY(2, 3000.0);

        assertEquals(1.0, iterator.next().y);
        assertEquals(2.0, iterator.next().y);
        assertEquals(3.0, iterator.next().y);
    }

    @Test
    public void testIteratorWithTabulatedFunctionOperationService() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{2, 4, 6}, new double[]{8, 16, 32});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Point[] expectedPoints = TabulatedFunctionOperationService.asPoints(arrayFunction);
        Iterator<Point> iterator = syncFunction.iterator();

        int index = 0;
        while (iterator.hasNext()) {
            Point actualPoint = iterator.next();
            Point expectedPoint = expectedPoints[index];

            assertEquals(expectedPoint.x, actualPoint.x);
            assertEquals(expectedPoint.y, actualPoint.y);
            index++;
        }

        assertEquals(expectedPoints.length, index);
    }

    @Test
    public void testMultipleIteratorsIndependence() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{10, 20, 30});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Iterator<Point> iterator1 = syncFunction.iterator();
        Iterator<Point> iterator2 = syncFunction.iterator();

        iterator1.next();
        iterator1.next();

        Point pointFromIterator2 = iterator2.next();
        assertEquals(1.0, pointFromIterator2.x);
        assertEquals(10.0, pointFromIterator2.y);

        Point pointFromIterator1 = iterator1.next();
        assertEquals(3.0, pointFromIterator1.x);
        assertEquals(30.0, pointFromIterator1.y);
    }

    @Test
    public void testDoSynchronouslyWithReturnValue() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Double result = syncFunction.doSynchronously(new SynchronizedTabulatedFunction.Operation<Double>() {
            @Override
            public Double apply(SynchronizedTabulatedFunction function) {
                return function.getY(0) + function.getY(1) + function.getY(2);
            }
        });

        assertEquals(15.0, result, 1e-9);
    }

    @Test
    public void testDoSynchronouslyWithVoid() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{1, 2, 3});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Void result = syncFunction.doSynchronously(new SynchronizedTabulatedFunction.Operation<Void>() {
            @Override
            public Void apply(SynchronizedTabulatedFunction function) {
                function.setY(0, 10.0);
                function.setY(1, 20.0);
                function.setY(2, 30.0);
                return null;
            }
        });

        assertNull(result);
        assertEquals(10.0, syncFunction.getY(0), 1e-9);
        assertEquals(20.0, syncFunction.getY(1), 1e-9);
        assertEquals(30.0, syncFunction.getY(2), 1e-9);
    }

    @Test
    public void testDoSynchronouslyWithLambda() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{2, 4, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Double average = syncFunction.doSynchronously(func -> {
            double sum = 0;
            for (int i = 0; i < func.getCount(); i++) {
                sum += func.getY(i);
            }
            return sum / func.getCount();
        });

        assertEquals(4.0, average, 1e-9);
    }

    @Test
    public void testDoSynchronouslyComplexOperation() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{0, 1, 2}, new double[]{0, 1, 2});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Boolean allPositive = syncFunction.doSynchronously(func -> {
            boolean positive = true;
            for (int i = 0; i < func.getCount(); i++) {
                if (func.getY(i) < 0) {
                    positive = false;
                    break;
                }
            }

            for (int i = 0; i < func.getCount(); i++) {
                func.setY(i, func.getY(i) * 2);
            }

            return positive;
        });

        assertTrue(allPositive);
        assertEquals(2.0, syncFunction.getY(1), 1e-9);
    }

    @Test
    public void testDoSynchronouslyWithIntegerReturn() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3, 4}, new double[]{5, 6, 7, 8});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Integer pointCount = syncFunction.doSynchronously(func -> {
            return func.getCount();
        });

        assertEquals(4, pointCount);
    }

    @Test
    public void testDoSynchronouslyWithVoidLambda() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2}, new double[]{1, 2});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Void result = syncFunction.doSynchronously(func -> {
            for (int i = 0; i < func.getCount(); i++) {
                func.setY(i, func.getY(i) * 10);
            }
            return null;
        });

        assertNull(result);
        assertEquals(10.0, syncFunction.getY(0), 1e-9);
        assertEquals(20.0, syncFunction.getY(1), 1e-9);
    }

    @Test
    public void testDoSynchronouslyWithPointReturn() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        Point middlePoint = syncFunction.doSynchronously(func -> {
            int middleIndex = func.getCount() / 2;
            return new Point(func.getX(middleIndex), func.getY(middleIndex));
        });

        assertEquals(2.0, middlePoint.x, 1e-9);
        assertEquals(5.0, middlePoint.y, 1e-9);
    }

    @Test
    public void testWithLinkedListFunction() {
        TabulatedFunction linkedFunction = new LinkedListTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(linkedFunction);

        assertEquals(3, syncFunction.getCount());
        assertEquals(2.0, syncFunction.getX(1));
        assertEquals(5.0, syncFunction.getY(1));

        syncFunction.setY(2, 10.0);
        assertEquals(10.0, syncFunction.getY(2));
    }

    @Test
    public void testDoSynchronouslyExceptionHandling() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2}, new double[]{1, 2});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertThrows(IllegalArgumentException.class, () -> {
            syncFunction.doSynchronously(func -> {
                if (func.getCount() > 0) {
                    throw new IllegalArgumentException("Test exception");
                }
                return null;
            });
        });
    }

    @Test
    public void testIteratorNoSuchElementException() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {10.0, 20.0, 30.0, 40.0};
        TabulatedFunction baseFunction = new ArrayTabulatedFunction(xValues, yValues);
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(baseFunction);
        Iterator<Point> iterator = syncFunction.iterator();

        while (iterator.hasNext()) {
            iterator.next();
        }
        assertThrows(NoSuchElementException.class, iterator::next);
    }
}