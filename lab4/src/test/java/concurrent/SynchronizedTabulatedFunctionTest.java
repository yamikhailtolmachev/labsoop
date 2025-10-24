package concurrent;

import org.junit.jupiter.api.Test;
import functions.TabulatedFunction;
import functions.ArrayTabulatedFunction;
import functions.Point;
import operations.TabulatedFunctionOperationService;

import java.util.Iterator;

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
}