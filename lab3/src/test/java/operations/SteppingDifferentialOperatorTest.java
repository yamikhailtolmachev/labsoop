package operations;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SteppingDifferentialOperatorTest {

    @Test
    void testConstructorWithValidStep() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);
        assertEquals(0.1, operator.getStep(), 1e-10);

        operator = new LeftSteppingDifferentialOperator(1.0);
        assertEquals(1.0, operator.getStep(), 1e-10);

        operator = new LeftSteppingDifferentialOperator(0.0001);
        assertEquals(0.0001, operator.getStep(), 1e-10);
    }

    @Test
    void testConstructorWithZeroStep() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(0.0);
        });
    }

    @Test
    void testConstructorWithNegativeStep() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(-0.1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(-1.0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(-0.0001);
        });
    }

    @Test
    void testConstructorWithPositiveInfinity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(Double.POSITIVE_INFINITY);
        });
    }

    @Test
    void testConstructorWithNegativeInfinity() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(Double.NEGATIVE_INFINITY);
        });
    }

    @Test
    void testConstructorWithNaN() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LeftSteppingDifferentialOperator(Double.NaN);
        });
    }

    @Test
    void testGetStep() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.5);
        assertEquals(0.5, operator.getStep(), 1e-10);

        operator = new RightSteppingDifferentialOperator(2.0);
        assertEquals(2.0, operator.getStep(), 1e-10);

        operator = new MiddleSteppingDifferentialOperator(0.001);
        assertEquals(0.001, operator.getStep(), 1e-10);
    }

    @Test
    void testSetStepWithValidValue() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        operator.setStep(0.05);
        assertEquals(0.05, operator.getStep(), 1e-10);

        operator.setStep(1.0);
        assertEquals(1.0, operator.getStep(), 1e-10);

        operator.setStep(0.0001);
        assertEquals(0.0001, operator.getStep(), 1e-10);
    }

    @Test
    void testSetStepWithZero() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(0.0);
        });

        assertEquals(0.1, operator.getStep(), 1e-10);
    }

    @Test
    void testSetStepWithNegativeValue() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(-0.1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(-1.0);
        });

        assertEquals(0.1, operator.getStep(), 1e-10);
    }

    @Test
    void testSetStepWithPositiveInfinity() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(Double.POSITIVE_INFINITY);
        });

        assertEquals(0.1, operator.getStep(), 1e-10);
    }

    @Test
    void testSetStepWithNegativeInfinity() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(Double.NEGATIVE_INFINITY);
        });

        assertEquals(0.1, operator.getStep(), 1e-10);
    }

    @Test
    void testSetStepWithNaN() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        assertThrows(IllegalArgumentException.class, () -> {
            operator.setStep(Double.NaN);
        });

        assertEquals(0.1, operator.getStep(), 1e-10);
    }

    @Test
    void testMultipleSetStepCalls() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        operator.setStep(0.2);
        assertEquals(0.2, operator.getStep(), 1e-10);

        operator.setStep(0.3);
        assertEquals(0.3, operator.getStep(), 1e-10);

        operator.setStep(0.4);
        assertEquals(0.4, operator.getStep(), 1e-10);
    }

    @Test
    void testStepConsistencyAfterInvalidSet() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.5);

        try {
            operator.setStep(-1.0);
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {}

        assertEquals(0.5, operator.getStep(), 1e-10);
    }

    @Test
    void testDifferentOperatorTypesWithSameStep() {
        SteppingDifferentialOperator leftOp = new LeftSteppingDifferentialOperator(0.1);
        SteppingDifferentialOperator rightOp = new RightSteppingDifferentialOperator(0.1);
        SteppingDifferentialOperator middleOp = new MiddleSteppingDifferentialOperator(0.1);

        assertEquals(0.1, leftOp.getStep(), 1e-10);
        assertEquals(0.1, rightOp.getStep(), 1e-10);
        assertEquals(0.1, middleOp.getStep(), 1e-10);
    }

    @Test
    void testStepPrecision() {
        double preciseStep = 0.123456789;
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(preciseStep);

        assertEquals(preciseStep, operator.getStep(), 1e-10);

        operator.setStep(0.987654321);
        assertEquals(0.987654321, operator.getStep(), 1e-10);
    }

    @Test
    void testVerySmallStep() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(1e-10);
        assertEquals(1e-10, operator.getStep(), 1e-15);

        operator.setStep(1e-15);
        assertEquals(1e-15, operator.getStep(), 1e-20);
    }

    @Test
    void testVeryLargeStep() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(1e10);
        assertEquals(1e10, operator.getStep(), 1e5);

        operator.setStep(1e15);
        assertEquals(1e15, operator.getStep(), 1e10);
    }

    @Test
    void testStepIsProtectedField() {
        SteppingDifferentialOperator operator = new LeftSteppingDifferentialOperator(0.1);

        double step = operator.getStep();
        assertEquals(0.1, step, 1e-10);
    }
}