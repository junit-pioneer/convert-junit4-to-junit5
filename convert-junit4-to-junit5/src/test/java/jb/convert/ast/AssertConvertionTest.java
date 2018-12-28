package jb.convert.ast;

import jb.convert.MatchDetector;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.lang.String.format;
import static jb.JunitConversionLogicFixture.assertAfterAddingClassAfter;
import static jb.JunitConversionLogicFixture.assertAfterWrappingInMethod;
import static jb.JunitConversionLogicFixture.assertUnchangedAfterWrappingInMethod;
import static jb.JunitConversionLogicFixture.assertWrappingInClass;

class AssertConvertionTest {

    @Test
    void importWildcardStaticAssertions() {
        String code = "import static org.junit.Assert.*;";
        String expected = "import static org.junit.jupiter.api.Assertions.*;";
        assertAfterAddingClassAfter(code, expected);
    }

    @Test
    void specificStaticAssertions() {
        String originalImport = "import org.junit.Assert;";
        String originalMethod = "Assert.assertTrue(a,b);";
        String expectedImport = "import org.junit.jupiter.api.Assertions;";
        String expectedMethod = "Assertions.assertTrue(b,a);";
        assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, expectedMethod);
    }

    @Test
    void specificStaticFail() {
        String originalImport = "import org.junit.Assert;";
        String originalMethod = "Assert.fail(a);";
        String expectedImport = "import org.junit.jupiter.api.Assertions;";
        String expectedMethod = "Assertions.fail(a);";
        assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, expectedMethod);
    }

    @Test
    void specificFullyQualifiedStaticAssertions() {
        String originalImport = "import org.junit.Assert;";
        String originalMethod = "org.junit.Assert.assertTrue(a,b);";
        String expectedImport = "import org.junit.jupiter.api.Assertions;";
        String expectedMethod = "org.junit.jupiter.api.Assertions.assertTrue(b,a);";
        assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, expectedMethod);
    }

    @ParameterizedTest
    @MethodSource("junit4AssertMethodsWithMatchesInJunit5")
    void fullyQualifiedStaticImportOfTheAssertionMethod(String assertionName) {
        String originalImport = format("import static org.junit.Assert.%s;", assertionName);
        String expectedImport = format("import static org.junit.jupiter.api.Assertions.%s;", assertionName);
        assertAfterAddingClassAfter(originalImport, expectedImport);
    }

    private static Stream<String> junit4AssertMethodsWithMatchesInJunit5() {
        return new MatchDetector().publicStaticMethodsWithMatchingNames(Assert.class, Assertions.class).stream();
    }

    @Test
    void singleAssertion() {
        String code = "assertTrue(message, actual);";
        String expected = "assertTrue(actual, message);";
        assertAfterWrappingInMethod(code, expected);
    }

    @Test
    void multipleAssertions() {
        String code = "assertTrue(message, actual);";
        String expected = "assertTrue(actual, message);";
        assertAfterWrappingInMethod(code, expected);
    }

    @Test
    void nonJunitMethodCall() {
        String code = "doWork(message, actual);";
        assertUnchangedAfterWrappingInMethod(code);
    }

    @Test
    void assertInFirstLineOfMethod() {
        String code = "public void method() { assertTrue(message, actual); }";
        String expected = "public void method() { assertTrue(actual, message); }";
        assertWrappingInClass(code, expected);
    }

    @Test
    void assertAfterCommentOnPreviousLine() {
        String code = "public void method() { assertTrue(message, actual); // comment \n"
                + "assertTrue(message, actual); }";
        String expected = "public void method() { assertTrue(actual, message); // comment \n"
                + "assertTrue(actual, message); }";
        assertWrappingInClass(code, expected);
    }

}