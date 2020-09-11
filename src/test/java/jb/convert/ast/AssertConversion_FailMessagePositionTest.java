package jb.convert.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static jb.JunitConversionLogicFixture.assertAfterWrappingInMethod;
import static jb.JunitConversionLogicFixture.assertUnchangedAfterWrappingInMethod;

class AssertConversion_FailMessagePositionTest {

    @Test
    void notAnAssertion() {
        String code = "// ignore me \n";
        assertUnchangedAfterWrappingInMethod(code);
    }

    @Test
    void noMessage() {
        String code = "assertEquals(expected, actual);";
        assertUnchangedAfterWrappingInMethod(code);
    }

    @Test
    void message() {
        String code = "assertEquals(message, expected, actual);";
        String expected = "assertEquals(expected, actual, message);";
        assertAfterWrappingInMethod(code, expected);
    }

    @Test
    void noneZeroDoubleDeltaNoMessage() {
        String code = "assertEquals(expected, actual, 0.1);";

        assertUnchangedAfterWrappingInMethod(code);
    }

    @Test
    void zeroDeltaIsNotSupportedInJunitJupiterRemoveIt() {
        String code = "assertEquals(expected, actual, 0.0);";
        String expected = "assertEquals(expected, actual);";

        assertAfterWrappingInMethod(code, expected);
    }

    @Test
    void whiteSpaceForThreeParams() {
        String code = "assertEquals  (  message,  expected,   actual  )  ;";
        String expected = "assertEquals  (  expected,  actual,   message  )  ;";
        assertAfterWrappingInMethod(code, expected);
    }

    @Test
    void whiteSpaceForTwoParams() {
        String code = "assertNotNull  (  message,   actual  )  ; ";
        String expected = "assertNotNull  (  actual,   message  )  ; ";
        assertAfterWrappingInMethod(code, expected);
    }

    @Test
    void notEnoughParameters() {
        String code = "assertEquals(random);";
        assertUnchangedAfterWrappingInMethod(code);
    }

    @Test
    void tooManyParameters() {
        String code = "assertEquals(a, b, c, d, e);";
        assertUnchangedAfterWrappingInMethod(code);
    }

    // ------------------------------------------------------

    @ParameterizedTest
    @ValueSource(strings = {"assertArrayEquals", "assertEquals", "assertNotEquals"})
    void allJunit4MethodNamesReorderExpectedActual(String methodName) {
        String code = methodName + "(message, expected, actual);";
        String expected = methodName + "(expected, actual, message);";
        assertAfterWrappingInMethod(code, expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"assertFalse", "assertNotNull", "assertNull", "assertTrue"})
    void allJunit4MethodNamesReorderSingleParam(String methodName) {
        String code = methodName + "(message, actual);";
        String expected = methodName + "(actual, message);";
        assertAfterWrappingInMethod(code, expected);
    }

    // ------------------------------------------------------

    void deltaDoubleReorders() {
        String code = "assertEquals(message, expected, actual, delta);";
        String expected = "assertEquals(expected, actual, delta, message);";
        assertAfterWrappingInMethod(code, expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"assertDoubles", "assertCustom", "assumeBecauseISaidSo"})
    void otherMethodNamesDoNotChangeParamOrder(String methodName) {
        String code = methodName + "(message, expected, actual);";
        assertUnchangedAfterWrappingInMethod(code);
    }

    @Test
    void assertThatRemainsUnchanged() {
        String code = "assertThat(message, actual, endsWith(abc));";
        assertUnchangedAfterWrappingInMethod(code);
    }

    @Test
    void concatInParams() {
        String code = "assertEquals(message + more, expected + more, actual + more);";
        String expected = "assertEquals(expected + more, actual + more, message + more);";
        assertAfterWrappingInMethod(code, expected);
    }

    @Test
    void methodsInParams() {
        String code = "assertEquals(message(a,b,c), expected(1,2,3), actual(x,y,z));";
        String expected = "assertEquals(expected(1,2,3), actual(x,y,z), message(a,b,c));";
        assertAfterWrappingInMethod(code, expected);
    }

    @Test
    void newLines() {
        String code = "assertEquals(\nmessage, \nexpected, \nactual);";
        String expected = "assertEquals(\nexpected, \nactual, \nmessage);";
        assertAfterWrappingInMethod(code, expected);
    }

    // ------------------------------------------------------

    @Test
    void commaInStringButNoMessage() {
        String code = "assertEquals(\"Empty message, please enter a message or quit.\", PostCommon.validatePost(post, true, 1));";
        assertUnchangedAfterWrappingInMethod(code);
    }

    @Test
    void commaInStringInsideMessage() {
        String code = "assertEquals(\"a,b,c\", \"Empty message, please enter a message or quit.\", PostCommon.validatePost(post, true, 1));";
        String expected = "assertEquals(\"Empty message, please enter a message or quit.\", PostCommon.validatePost(post, true, 1), \"a,b,c\");";
        assertAfterWrappingInMethod(code, expected);
    }

    @Test
    void commaAndEscapedQuoteInStringInsideMessage() {
        String code = "assertEquals(\"a,\\\",c\", \"Empty message, please enter a message or quit.\", PostCommon.validatePost(post, true, 1));";
        String expected = "assertEquals(\"Empty message, please enter a message or quit.\", PostCommon.validatePost(post, true, 1), \"a,\\\",c\");";
        assertAfterWrappingInMethod(code, expected);
    }

}
