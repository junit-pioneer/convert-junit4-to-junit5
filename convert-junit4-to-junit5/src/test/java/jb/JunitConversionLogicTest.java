package jb;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.expr.Expression;
import jb.convert.ConversionOutcome;
import jb.convert.ConversionResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jb.JunitConversionLogicFixture.assertAfterAddingClassAfter;
import static jb.JunitConversionLogicFixture.assertAfterWrappingInMethod;
import static jb.JunitConversionLogicFixture.assertPrettyPrintEqual;
import static jb.JunitConversionLogicFixture.assertUnchangedAfterWrappingInMethod;
import static jb.JunitConversionLogicFixture.assertUnchangedWrappingInClass;
import static jb.JunitConversionLogicFixture.assertWrappingInClass;
import static jb.JunitConversionLogicFixture.converted;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JunitConversionLogicTest {

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
    void importWildcardMainPackage() {
        String originalImport = "import org.junit.*;";
        String expectedImport = "import org.junit.jupiter.api.*;";
        assertAfterAddingClassAfter(originalImport, expectedImport);
    }

    @Test
    void specificTestAnnotation() {
        String annotation = "import org.junit.Test;";
        String expected = "import org.junit.jupiter.api.Test;";
        assertAfterAddingClassAfter(annotation, expected);
    }

    @Test
    void doNotUpdateIfAlreadyJupiter() {
        String code = "import static org.junit.jupiter.api.Assertions.*;";
        ConversionResult result = JunitConversionLogicFixture.convert(code);
        assertEquals(ConversionOutcome.Skipped, result.outcome);
        assertEquals("already using junit 5", result.details);
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

    // -------------------------------------------------------

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

    // -------------------------------------------------------

    @Test
    void doNotEditClassIfNotJUnit() {
        String code = "public void randomMethod() {}";
        assertUnchangedWrappingInClass(code);
    }

    // -------------------------------------------------------
    @Test
    void doNotTouchJBehaveAnnotations() {
        String compatibleCode = "import org.jbehave.core.annotations.BeforeScenario;\n"
                + "public class A { \n"
                + "@BeforeScenario\n"
                + "public void m() { }}";

        assertPrettyPrintEqual(compatibleCode, converted(compatibleCode));
    }

    @Test
    void ignoreToDisabled() {
        String junit4 = "import org.junit.Ignore;\n" +
                "import org.junit.Test;\n" +
                "public class A { \n" +
                "@Test @Ignore\n" +
                "public void m() { }}";
        String junit5 = "import org.junit.jupiter.api.Disabled;\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "class A { \n" +
                "@Test @Disabled\n" +
                "void m() { }}";

        assertPrettyPrintEqual(junit5, converted(junit4));
    }

    @ParameterizedTest
    @MethodSource("annotationsProvider")
    void newAnnotationNames(String oldAnnotationName, String newAnnotationName) {
        String junit4 = "import org.junit." + oldAnnotationName + ";\n"
                + "public class A { \n"
                + "@" + oldAnnotationName + "\n"
                + "public void m() { }}";
        String junit5 = "import org.junit.jupiter.api." + newAnnotationName + ";\n"
                + "public class A { \n"
                + "@" + newAnnotationName + "\n"
                + "void m() { }}";

        assertPrettyPrintEqual(junit5, converted(junit4));
    }

    static Stream<Arguments> annotationsProvider() {
        return Stream.of(Arguments.of("Before", "BeforeEach"),
                Arguments.of("BeforeClass", "BeforeAll"),
                Arguments.of("After", "AfterEach"),
                Arguments.of("AfterClass", "AfterAll")
        );
    }

}
