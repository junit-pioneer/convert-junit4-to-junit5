package jb;

import jb.convert.ConversionOutcome;
import jb.convert.ConversionResult;
import org.junit.jupiter.api.Test;

import static jb.JunitConversionLogicFixture.assertUnchangedWrappingInClass;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JunitConversionLogicTest {

    @Test
    void doNotUpdateIfAlreadyJupiter() {
        String code = "import static org.junit.jupiter.api.Assertions.*;";
        ConversionResult result = JunitConversionLogicFixture.convert(code);
        assertEquals(ConversionOutcome.Skipped, result.outcome);
        assertEquals("already using junit 5", result.details);
    }

    @Test
    void doNotEditClassIfNotJUnit() {
        String code = "public void randomMethod() {}";
        assertUnchangedWrappingInClass(code);
    }

}
