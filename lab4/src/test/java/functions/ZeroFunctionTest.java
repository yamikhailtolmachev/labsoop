package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ZeroFunctionTest {

    @Test
    void testApplyWithVariousInputs() {
        ZeroFunction zeroFunction = new ZeroFunction();

        assertEquals(0.0, zeroFunction.apply(0.0));
        assertEquals(0.0, zeroFunction.apply(1.0));
        assertEquals(0.0, zeroFunction.apply(-1.0));
        assertEquals(0.0, zeroFunction.apply(10.0));
        assertEquals(0.0, zeroFunction.apply(-10.0));
        assertEquals(0.0, zeroFunction.apply(100.5));
        assertEquals(0.0, zeroFunction.apply(-100.5));
    }

    @Test
    void testGetConstant() {
        ZeroFunction zeroFunction = new ZeroFunction();

        assertEquals(0.0, zeroFunction.getConstant());
    }

    @Test
    void testInheritance() {
        ZeroFunction zeroFunction = new ZeroFunction();

        assertTrue(zeroFunction instanceof ConstantFunction);
        assertTrue(zeroFunction instanceof MathFunction);
    }

    @Test
    void testMultipleCallsConsistency() {
        ZeroFunction zeroFunction = new ZeroFunction();

        double firstCall = zeroFunction.apply(5.0);
        double secondCall = zeroFunction.apply(10.0);
        double thirdCall = zeroFunction.apply(-3.0);

        assertEquals(0.0, firstCall);
        assertEquals(0.0, secondCall);
        assertEquals(0.0, thirdCall);
        assertEquals(firstCall, secondCall);
        assertEquals(secondCall, thirdCall);
    }

    @Test
    void testObjectCreation() {
        ZeroFunction zeroFunction = new ZeroFunction();

        assertNotNull(zeroFunction);
    }
}