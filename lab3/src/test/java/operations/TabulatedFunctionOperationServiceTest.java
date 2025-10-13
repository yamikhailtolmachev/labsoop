package operations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;
import functions.factory.ArrayTabulatedFunctionFactory;
import functions.factory.LinkedListTabulatedFunctionFactory;
import functions.factory.TabulatedFunctionFactory;

class TabulatedFunctionOperationServiceTest {
    @Test
    void testAsPointsWithArrayTabulatedFunction() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithLinkedListTabulatedFunction() {
        double[] xValues = {0.5, 1.0, 1.5, 2.0};
        double[] yValues = {0.25, 1.0, 2.25, 4.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithMinimumSizeFunction() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {10.0, 20.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(2, points.length);
        assertEquals(1.0, points[0].x, 1e-9);
        assertEquals(10.0, points[0].y, 1e-9);
        assertEquals(2.0, points[1].x, 1e-9);
        assertEquals(20.0, points[1].y, 1e-9);
    }

    @Test
    void testAsPointsWithNegativeValues() {
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(5, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithFractionalValues() {
        double[] xValues = {0.1, 0.2, 0.3, 0.4};
        double[] yValues = {0.01, 0.04, 0.09, 0.16};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsReturnsIndependentArray() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 3.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points1 = TabulatedFunctionOperationService.asPoints(function);
        Point[] points2 = TabulatedFunctionOperationService.asPoints(function);

        assertNotSame(points1, points2);
        assertEquals(points1.length, points2.length);
        for (int i = 0; i < points1.length; i++) {
            assertEquals(points1[i].x, points2[i].x, 1e-9);
            assertEquals(points1[i].y, points2[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithLargeFunction() {
        int size = 100;
        double[] xValues = new double[size];
        double[] yValues = new double[size];

        for (int i = 0; i < size; i++) {
            xValues[i] = i;
            yValues[i] = i * i;
        }

        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);
        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(size, points.length);
        for (int i = 0; i < size; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithConstantFunction() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {5.0, 5.0, 5.0, 5.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(5.0, points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithLinearFunction() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {2.0, 4.0, 6.0, 8.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsOrderConsistency() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {10.0, 20.0, 30.0};
        TabulatedFunction arrayFunction = new ArrayTabulatedFunction(xValues, yValues);
        TabulatedFunction linkedListFunction = new LinkedListTabulatedFunction(xValues, yValues);

        Point[] arrayPoints = TabulatedFunctionOperationService.asPoints(arrayFunction);
        Point[] linkedListPoints = TabulatedFunctionOperationService.asPoints(linkedListFunction);

        assertEquals(arrayPoints.length, linkedListPoints.length);
        for (int i = 0; i < arrayPoints.length; i++) {
            assertEquals(arrayPoints[i].x, linkedListPoints[i].x, 1e-9);
            assertEquals(arrayPoints[i].y, linkedListPoints[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithZeroFunction() {
        double[] xValues = {-1.0, 0.0, 1.0};
        double[] yValues = {0.0, 0.0, 0.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(3, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(0.0, points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithIdentityFunction() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 2.0, 3.0, 4.0};
        TabulatedFunction function = new LinkedListTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithEmptyArrays() {
        double[] xValues = {1.0, 2.0};
        double[] yValues = {3.0, 4.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(2, points.length);
        assertNotNull(points[0]);
        assertNotNull(points[1]);
    }

    @Test
    void testDefaultConstructor() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        assertNotNull(service.getFactory());
        assertTrue(service.getFactory() instanceof ArrayTabulatedFunctionFactory);
    }

    @Test
    void testConstructorWithFactory() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(factory);

        assertSame(factory, service.getFactory());
    }

    @Test
    void testGetFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        TabulatedFunctionFactory factory = service.getFactory();

        assertNotNull(factory);
        assertTrue(factory instanceof ArrayTabulatedFunctionFactory);
    }

    @Test
    void testSetFactory() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        assertTrue(service.getFactory() instanceof ArrayTabulatedFunctionFactory);

        TabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();
        service.setFactory(newFactory);

        assertSame(newFactory, service.getFactory());
        assertTrue(service.getFactory() instanceof LinkedListTabulatedFunctionFactory);
    }

    @Test
    void testSetFactoryMultipleTimes() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunctionFactory factory1 = new LinkedListTabulatedFunctionFactory();
        service.setFactory(factory1);
        assertSame(factory1, service.getFactory());

        TabulatedFunctionFactory factory2 = new ArrayTabulatedFunctionFactory();
        service.setFactory(factory2);
        assertSame(factory2, service.getFactory());

        TabulatedFunctionFactory factory3 = new LinkedListTabulatedFunctionFactory();
        service.setFactory(factory3);
        assertSame(factory3, service.getFactory());
    }

    @Test
    void testSetFactoryToNull() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunctionFactory originalFactory = service.getFactory();
        assertNotNull(originalFactory);

        assertThrows(IllegalArgumentException.class, () -> {
            service.setFactory(null);
        });

        assertNotNull(service.getFactory());
        assertSame(originalFactory, service.getFactory());
    }

    @Test
    void testConstructorWithNullFactory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TabulatedFunctionOperationService(null);
        });
    }

    @Test
    void testFactoryIntegrationWithAsPoints() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunctionFactory factory = service.getFactory();
        assertNotNull(factory);

        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};

        TabulatedFunction function = factory.create(xValues, yValues);
        assertNotNull(function);
        assertEquals(3, function.getCount());
    }

    @Test
    void testServiceWithDifferentFactories() {
        TabulatedFunctionOperationService arrayService = new TabulatedFunctionOperationService(new ArrayTabulatedFunctionFactory());
        assertTrue(arrayService.getFactory() instanceof ArrayTabulatedFunctionFactory);

        TabulatedFunctionOperationService linkedListService = new TabulatedFunctionOperationService(new LinkedListTabulatedFunctionFactory());
        assertTrue(linkedListService.getFactory() instanceof LinkedListTabulatedFunctionFactory);

        assertNotSame(arrayService.getFactory(), linkedListService.getFactory());
    }

    @Test
    void testConstructorAndGetterConsistency() {
        TabulatedFunctionFactory factory = new LinkedListTabulatedFunctionFactory();
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService(factory);

        assertSame(factory, service.getFactory());
    }

    @Test
    void testSetAndGetConsistency() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();

        TabulatedFunctionFactory newFactory = new LinkedListTabulatedFunctionFactory();
        service.setFactory(newFactory);

        assertSame(newFactory, service.getFactory());
    }

    @Test
    void testAsPointsWithSortedButNonUniformXValues() {
        double[] xValues = {1.0, 1.5, 3.0, 5.0};
        double[] yValues = {1.0, 2.25, 9.0, 25.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithStrictlyIncreasingXValues() {
        double[] xValues = {1.0, 2.0, 3.0, 4.0};
        double[] yValues = {1.0, 4.0, 9.0, 16.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithNonUniformSpacing() {
        double[] xValues = {0.0, 0.5, 1.5, 3.0};
        double[] yValues = {0.0, 0.25, 2.25, 9.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithNegativeIncreasingXValues() {
        double[] xValues = {-3.0, -2.0, -1.0, 0.0};
        double[] yValues = {9.0, 4.0, 1.0, 0.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithMixedPositiveNegativeXValues() {
        double[] xValues = {-2.0, -1.0, 0.0, 1.0, 2.0};
        double[] yValues = {4.0, 1.0, 0.0, 1.0, 4.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(5, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithVeryCloseXValues() {
        double[] xValues = {1.0, 1.0001, 1.0002, 1.0003};
        double[] yValues = {1.0, 1.0002, 1.0004, 1.0006};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(4, points.length);
        for (int i = 0; i < points.length; i++) {
            assertEquals(xValues[i], points[i].x, 1e-9);
            assertEquals(yValues[i], points[i].y, 1e-9);
        }
    }

    @Test
    void testAsPointsWithNaNValues() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, Double.NaN, 3.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(3, points.length);
        assertEquals(1.0, points[0].x, 1e-9);
        assertEquals(1.0, points[0].y, 1e-9);
        assertTrue(Double.isNaN(points[1].y));
        assertEquals(3.0, points[2].y, 1e-9);
    }

    @Test
    void testAsPointsIteratorBehavior() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 2.0, 3.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        for (Point point : points) {
            assertNotNull(point);
            assertTrue(point.x >= 1.0 && point.x <= 3.0);
            assertTrue(point.y >= 1.0 && point.y <= 3.0);
        }
    }

    @Test
    void testServiceStateConsistency() {
        TabulatedFunctionOperationService service = new TabulatedFunctionOperationService();
        TabulatedFunctionFactory originalFactory = service.getFactory();

        double[] xValues = {1.0, 2.0};
        double[] yValues = {1.0, 2.0};
        TabulatedFunction function = originalFactory.create(xValues, yValues);
        Point[] points = TabulatedFunctionOperationService.asPoints(function);

        assertSame(originalFactory, service.getFactory());
        assertEquals(2, points.length);
    }

    @Test
    void testMultipleAsPointsCallsConsistency() {
        double[] xValues = {1.0, 2.0, 3.0};
        double[] yValues = {1.0, 4.0, 9.0};
        TabulatedFunction function = new ArrayTabulatedFunction(xValues, yValues);

        Point[] points1 = TabulatedFunctionOperationService.asPoints(function);
        Point[] points2 = TabulatedFunctionOperationService.asPoints(function);
        Point[] points3 = TabulatedFunctionOperationService.asPoints(function);

        assertEquals(3, points1.length);
        assertEquals(3, points2.length);
        assertEquals(3, points3.length);

        for (int i = 0; i < 3; i++) {
            assertEquals(points1[i].x, points2[i].x, 1e-9);
            assertEquals(points1[i].y, points2[i].y, 1e-9);
            assertEquals(points1[i].x, points3[i].x, 1e-9);
            assertEquals(points1[i].y, points3[i].y, 1e-9);
        }
    }
}