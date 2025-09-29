package functions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdentityFunctionTest {
    @Test
    void testApply() {
        IdentityFunction identityFunction = new IdentityFunction();
        Assertions.assertEquals(101, identityFunction.apply(101));
        Assertions.assertEquals(16.78, identityFunction.apply(16.78));
        Assertions.assertEquals(0, identityFunction.apply(0));
    }
}