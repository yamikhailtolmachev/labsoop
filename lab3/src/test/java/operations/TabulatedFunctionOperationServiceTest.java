package operations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.Point;
import functions.TabulatedFunction;

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
}