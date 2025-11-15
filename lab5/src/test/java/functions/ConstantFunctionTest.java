package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConstantFunctionTest {

    @Test
    void testConstantFunction() {
        ConstantFunction constant = new ConstantFunction(5.0);

        assertEquals(5.0, constant.apply(0.0));
        assertEquals(5.0, constant.apply(10.0));
        assertEquals(5.0, constant.apply(-5.0));
        assertEquals(5.0, constant.apply(100.0));
        assertEquals(5.0, constant.getConstant());
    }

    @Test
    void testZeroFunction() {
        ZeroFunction zero = new ZeroFunction();

        assertEquals(0.0, zero.apply(0.0));
        assertEquals(0.0, zero.apply(10.0));
        assertEquals(0.0, zero.apply(-5.0));
        assertEquals(0.0, zero.getConstant());
        assertTrue(zero instanceof ConstantFunction);
    }

    @Test
    void testUnitFunction() {
        UnitFunction unit = new UnitFunction();

        assertEquals(1.0, unit.apply(0.0));
        assertEquals(1.0, unit.apply(10.0));
        assertEquals(1.0, unit.apply(-5.0));
        assertEquals(1.0, unit.getConstant());
        assertTrue(unit instanceof ConstantFunction);
    }

    @Test
    void testDifferentConstants() {
        ConstantFunction negative = new ConstantFunction(-3.5);
        ConstantFunction fractional = new ConstantFunction(2.75);

        assertEquals(-3.5, negative.apply(100.0));
        assertEquals(2.75, fractional.apply(-50.0));
    }

    @Test
    void testMultipleCallsConsistency() {
        ConstantFunction constant = new ConstantFunction(7.2);

        assertEquals(7.2, constant.apply(1.0));
        assertEquals(7.2, constant.apply(2.0));
        assertEquals(7.2, constant.apply(3.0));
    }
}