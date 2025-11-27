package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnitFunctionTest {

    @Test
    void testApplyWithVariousInputs() {
        UnitFunction unitFunction = new UnitFunction();

        assertEquals(1.0, unitFunction.apply(0.0));
        assertEquals(1.0, unitFunction.apply(1.0));
        assertEquals(1.0, unitFunction.apply(-1.0));
        assertEquals(1.0, unitFunction.apply(10.0));
        assertEquals(1.0, unitFunction.apply(-10.0));
        assertEquals(1.0, unitFunction.apply(100.5));
        assertEquals(1.0, unitFunction.apply(-100.5));
    }

    @Test
    void testGetConstant() {
        UnitFunction unitFunction = new UnitFunction();

        assertEquals(1.0, unitFunction.getConstant());
    }

    @Test
    void testInheritance() {
        UnitFunction unitFunction = new UnitFunction();

        assertTrue(unitFunction instanceof ConstantFunction);
        assertTrue(unitFunction instanceof MathFunction);
    }

    @Test
    void testMultipleCallsConsistency() {
        UnitFunction unitFunction = new UnitFunction();

        double firstCall = unitFunction.apply(5.0);
        double secondCall = unitFunction.apply(10.0);
        double thirdCall = unitFunction.apply(-3.0);

        assertEquals(1.0, firstCall);
        assertEquals(1.0, secondCall);
        assertEquals(1.0, thirdCall);
        assertEquals(firstCall, secondCall);
        assertEquals(secondCall, thirdCall);
    }

    @Test
    void testObjectCreation() {
        UnitFunction unitFunction = new UnitFunction();

        assertNotNull(unitFunction);
    }
}