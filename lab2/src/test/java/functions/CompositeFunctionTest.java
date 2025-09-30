package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompositeFunctionTest {

    @Test
    void testCompositeSqr() {
        MathFunction identity = new IdentityFunction();
        MathFunction sqr = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(identity, sqr);

        assertEquals(0.0, composite.apply(0.0));
        assertEquals(1.0, composite.apply(1.0));
        assertEquals(4.0, composite.apply(2.0));
        assertEquals(9.0, composite.apply(3.0));
        assertEquals(16.0, composite.apply(4.0));
    }

    @Test
    void testCompositeSqrSqr() {
        MathFunction sqr = new SqrFunction();
        CompositeFunction composite = new CompositeFunction(sqr, sqr);

        assertEquals(0.0, composite.apply(0.0));
        assertEquals(1.0, composite.apply(1.0));
        assertEquals(16.0, composite.apply(2.0));
        assertEquals(81.0, composite.apply(3.0));
        assertEquals(256.0, composite.apply(4.0));
    }

    @Test
    void testCompositeOfComposite() {
        MathFunction sqr = new SqrFunction();
        MathFunction identity = new IdentityFunction();

        CompositeFunction composite1 = new CompositeFunction(sqr, identity);
        CompositeFunction composite2 = new CompositeFunction(composite1, sqr);

        assertEquals(0.0, composite2.apply(0.0));
        assertEquals(1.0, composite2.apply(1.0));
        assertEquals(16.0, composite2.apply(2.0));
        assertEquals(81.0, composite2.apply(3.0));
    }

    @Test
    void testCompositeWithConstants() {
        MathFunction identity = new IdentityFunction();
        // Заменяем ConstantFunction на лямбда-выражение
        MathFunction constant = x -> 5.0;

        CompositeFunction composite1 = new CompositeFunction(identity, constant);
        assertEquals(5.0, composite1.apply(0.0));
        assertEquals(5.0, composite1.apply(10.0));
        assertEquals(5.0, composite1.apply(-5.0));

        CompositeFunction composite2 = new CompositeFunction(constant, identity);
        assertEquals(5.0, composite2.apply(0.0));
        assertEquals(5.0, composite2.apply(10.0));
        assertEquals(5.0, composite2.apply(-5.0));
    }

    @Test
    void testCompositeOfComplexFunctions() {
        MathFunction addOne = x -> x + 1;
        MathFunction multiplyByTwo = x -> x * 2;
        CompositeFunction f = new CompositeFunction(addOne, multiplyByTwo);

        MathFunction subtractOne = x -> x - 1;
        MathFunction sqr = new SqrFunction();
        CompositeFunction g = new CompositeFunction(subtractOne, sqr);

        CompositeFunction h = new CompositeFunction(f, g);

        assertEquals(1.0, h.apply(0.0));
        assertEquals(9.0, h.apply(1.0));
        assertEquals(25.0, h.apply(2.0));
        assertEquals(49.0, h.apply(3.0));
    }
}