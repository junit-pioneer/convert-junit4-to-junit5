package jb.convert.ast;

import org.junit.jupiter.api.Test;

import static jb.JunitConversionLogicFixture.assertAfterAddingClassAfter;
import static jb.JunitConversionLogicFixture.assertAfterWrappingInMethod;
import static jb.JunitConversionLogicFixture.assertUnchangedAfterWrappingInMethod;
import static jb.JunitConversionLogicFixture.assertWrappingInClass;

class AssertMigrationTest {

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

    @Test
    void fullyQualifiedStaticStaticImportOfTheAssertionMethod() {
        String originalImport = "import static org.junit.Assert.assertTrue;";
        String originalMethod = "assertTrue(a,b);";
        String expectedImport = "import static org.junit.jupiter.api.Assertions.assertTrue;";
        String expectedMethod = "assertTrue(b,a);";
        assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, expectedMethod);
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