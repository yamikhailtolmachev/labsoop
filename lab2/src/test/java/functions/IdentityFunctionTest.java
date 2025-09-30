package functions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdentityFunctionTest {
    @Test
    void testApply() {
        IdentityFunction identityFunction = new IdentityFunction();
        Assertions.assertEquals(107, identityFunction.apply(107));
        Assertions.assertEquals(16.78, identityFunction.apply(16.78));
        Assertions.assertEquals(0, identityFunction.apply(0));
    }
}