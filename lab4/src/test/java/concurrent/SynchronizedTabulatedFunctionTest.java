package concurrent;

import org.junit.jupiter.api.Test;
import functions.TabulatedFunction;
import functions.ArrayTabulatedFunction;
import functions.Point;

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

        syncFunction.setY(0, 15.0);
        assertEquals(15.0, syncFunction.getY(0));

        syncFunction.setY(2, 20.0);
        assertEquals(20.0, syncFunction.getY(2));
    }

    @Test
    public void testIndexOfX() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(1, syncFunction.indexOfX(2));
        assertEquals(-1, syncFunction.indexOfX(5));
        assertEquals(0, syncFunction.indexOfX(1));
        assertEquals(2, syncFunction.indexOfX(3));
        assertEquals(-1, syncFunction.indexOfX(0));
    }

    @Test
    public void testIndexOfY() {
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(new double[]{1, 2, 3}, new double[]{4, 5, 6});
        SynchronizedTabulatedFunction syncFunction = new SynchronizedTabulatedFunction(arrayFunction);

        assertEquals(1, syncFunction.indexOfY(5));
        assertEquals(-1, syncFunction.indexOfY(10));
        assertEquals(0, syncFunction.indexOfY(4));
        assertEquals(2, syncFunction.indexOfY(6));
        assertEquals(-1, syncFunction.indexOfY(100));
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
        assertEquals(3.5, syncFunction.apply(0.5));
        assertEquals(6.5, syncFunction.apply(3.5));
        assertEquals(5.5, syncFunction.apply(2.5));
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
}