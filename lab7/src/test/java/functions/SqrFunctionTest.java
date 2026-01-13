package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SqrFunctionTest {

    @Test
    public void testApplyWithVariousInputs() {
        SqrFunction sqrFunction = new SqrFunction();

        assertEquals(0.0, sqrFunction.apply(0.0));
        assertEquals(1.0, sqrFunction.apply(1.0));
        assertEquals(1.0, sqrFunction.apply(-1.0));

        assertEquals(4.0, sqrFunction.apply(2.0));
        assertEquals(9.0, sqrFunction.apply(3.0));

        assertEquals(0.25, sqrFunction.apply(0.5));
        assertEquals(2.25, sqrFunction.apply(1.5));
        
        assertEquals(1_000_000.0, sqrFunction.apply(1000.0));
    }

    @Test
    public void testApplySymmetryProperty() {
        SqrFunction sqrFunction = new SqrFunction();

        assertAll(
                () -> assertEquals(sqrFunction.apply(5.0), sqrFunction.apply(-5.0)),
                () -> assertEquals(sqrFunction.apply(2.5), sqrFunction.apply(-2.5)),
                () -> assertEquals(sqrFunction.apply(0.1), sqrFunction.apply(-0.1))
        );
    }
}