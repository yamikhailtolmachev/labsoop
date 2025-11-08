package functions;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AndThenMethodTest {

    @Test
    void testAndThenWithIdentityAndSqr() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();

        MathFunction composite = identity.andThen(sqr);

        assertEquals(4.0, composite.apply(2.0));
        assertEquals(9.0, composite.apply(3.0));
        assertEquals(0.0, composite.apply(0.0));
    }

    @Test
    void testAndThenWithSqrAndIdentity() {
        SqrFunction sqr = new SqrFunction();
        IdentityFunction identity = new IdentityFunction();

        MathFunction composite = sqr.andThen(identity);

        assertEquals(4.0, composite.apply(2.0));
        assertEquals(9.0, composite.apply(3.0));
        assertEquals(0.0, composite.apply(0.0));
    }

    @Test
    void testAndThenChainOfThreeFunctions() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();
        ConstantFunction constant = new ConstantFunction(5.0);

        MathFunction composite = identity.andThen(sqr).andThen(constant);

        assertEquals(5.0, composite.apply(2.0));
        assertEquals(5.0, composite.apply(3.0));
        assertEquals(5.0, composite.apply(0.0));
    }

    @Test
    void testAndThenChainOfFourFunctions() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr1 = new SqrFunction();
        SqrFunction sqr2 = new SqrFunction();
        ConstantFunction constant = new ConstantFunction(10.0);

        MathFunction composite = identity.andThen(sqr1).andThen(sqr2).andThen(constant);

        assertEquals(10.0, composite.apply(2.0));
        assertEquals(10.0, composite.apply(3.0));
        assertEquals(10.0, composite.apply(1.0));
    }

    @Test
    void testAndThenWithZeroFunction() {
        SqrFunction sqr = new SqrFunction();
        ZeroFunction zero = new ZeroFunction();

        MathFunction composite = sqr.andThen(zero);

        assertEquals(0.0, composite.apply(2.0));
        assertEquals(0.0, composite.apply(3.0));
        assertEquals(0.0, composite.apply(0.0));
    }

    @Test
    void testAndThenWithUnitFunction() {
        IdentityFunction identity = new IdentityFunction();
        UnitFunction unit = new UnitFunction();

        MathFunction composite = identity.andThen(unit);

        assertEquals(1.0, composite.apply(2.0));
        assertEquals(1.0, composite.apply(3.0));
        assertEquals(1.0, composite.apply(0.0));
    }

    @Test
    void testAndThenDirectApplication() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();

        double result = identity.andThen(sqr).apply(5.0);

        assertEquals(25.0, result);
    }

    @Test
    void testAndThenWithComplexChain() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();

        MathFunction addFive = new MathFunction() {
            @Override
            public double apply(double x) {
                return x + 5.0;
            }
        };

        MathFunction composite = identity.andThen(sqr).andThen(addFive);

        assertEquals(9.0, composite.apply(2.0));
        assertEquals(14.0, composite.apply(3.0));
        assertEquals(5.0, composite.apply(0.0));
    }

    @Test
    void testAndThenReturnsCompositeFunction() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();

        MathFunction result = identity.andThen(sqr);

        assertTrue(result instanceof CompositeFunction);
    }

    @Test
    void testAndThenWithSameFunction() {
        SqrFunction sqr = new SqrFunction();

        MathFunction composite = sqr.andThen(sqr);

        assertEquals(16.0, composite.apply(2.0));
        assertEquals(81.0, composite.apply(3.0));
        assertEquals(0.0, composite.apply(0.0));
    }

    @Test
    void testAndThenOrderMatters() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();

        MathFunction composite1 = identity.andThen(sqr);
        MathFunction composite2 = sqr.andThen(identity);

        assertEquals(composite1.apply(3.0), composite2.apply(3.0));
        assertEquals(composite1.apply(4.0), composite2.apply(4.0));
    }

    @Test
    void testAndThenWithMultipleCalls() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();

        MathFunction composite = identity.andThen(sqr);

        double firstCall = composite.apply(3.0);
        double secondCall = composite.apply(3.0);
        double thirdCall = composite.apply(3.0);

        assertEquals(9.0, firstCall);
        assertEquals(9.0, secondCall);
        assertEquals(9.0, thirdCall);
    }

    @Test
    void testAndThenWithNegativeNumbers() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();

        MathFunction composite = identity.andThen(sqr);

        assertEquals(4.0, composite.apply(-2.0));
        assertEquals(9.0, composite.apply(-3.0));
        assertEquals(25.0, composite.apply(-5.0));
    }

    @Test
    void testAndThenWithFractionalNumbers() {
        IdentityFunction identity = new IdentityFunction();
        SqrFunction sqr = new SqrFunction();

        MathFunction composite = identity.andThen(sqr);

        assertEquals(0.25, composite.apply(0.5));
        assertEquals(0.25, composite.apply(-0.5));
        assertEquals(2.25, composite.apply(1.5));
    }

    @Test
    void testAndThenWithCustomFunction() {
        MathFunction doubleFunction = new MathFunction() {
            @Override
            public double apply(double x) {
                return x * 2;
            }
        };

        MathFunction tripleFunction = new MathFunction() {
            @Override
            public double apply(double x) {
                return x * 3;
            }
        };

        MathFunction composite = doubleFunction.andThen(tripleFunction);

        assertEquals(12.0, composite.apply(2.0));
        assertEquals(18.0, composite.apply(3.0));
        assertEquals(0.0, composite.apply(0.0));
    }
}