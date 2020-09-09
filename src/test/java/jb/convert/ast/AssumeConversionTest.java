package jb.convert.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static jb.JunitConversionLogicFixture.assertAfterAddingClassAfter;
import static jb.JunitConversionLogicFixture.assertAfterWrappingInMethod;

class AssumeConversionTest {

    @Test
    void importWildcardStaticAssumptions() {
        String originalImport = "import static org.junit.Assume.*;";
        String expectedImport = "import static org.junit.jupiter.api.Assumptions.*;";
        assertAfterAddingClassAfter(originalImport, expectedImport);
    }

    @Test
    void specificStaticAssumptions() {
        String originalImport = "import org.junit.Assume;";
        String originalMethod = "Assume.assumeTrue(a,b);";
        String expectedImport = "import org.junit.jupiter.api.Assumptions;";
        String expectedMethod = "Assumptions.assumeTrue(b,a);";
        assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, expectedMethod);
    }

    @Test
    void specificFullyQualifiedStaticAssumptions() {
        String originalImport = "import static org.junit.Assume.assumeFalse;";
        String expectedImport = "import static org.junit.jupiter.api.Assumptions.assumeFalse;";
        assertAfterAddingClassAfter(originalImport, expectedImport);
    }

    @ParameterizedTest
    @ValueSource(strings = {"assumeFalse", "assumeTrue"})
    void keepUnchangedTwoArgumentsAssumeMethods(String methodName) {
        String code = methodName + "(actual);";
        String expected = methodName + "(actual);";
        assertAfterWrappingInMethod(code, expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"assumeFalse", "assumeTrue"})
    void reorderTwoArgumentsAssumeMethods(String methodName) {
        String code = methodName + "(message, actual);";
        String expected = methodName + "(actual, message);";
        assertAfterWrappingInMethod(code, expected);
    }

}