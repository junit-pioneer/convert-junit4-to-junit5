package jb;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;

class JunitConversionLogicTest {
	
	private static String convertWhitespaceForJavaParser(String string) {
		CompilationUnit cu = JavaParser.parse(string);
		return cu.toString();
	}

	// wrap in class so well formed for parser
	private static void assertWrappingInClass(String code, String expected) {
		String prefix = "public class A { ";
		String postfix = " }";
		String codeWrapped = prefix + code + postfix;
		String expectedWrapped = prefix + expected + postfix;
		String actual = JunitConversionLogic.convert(codeWrapped);
		assertEquals(convertWhitespaceForJavaParser(expectedWrapped), actual);
	}

	// wrap in class/method so well formed for parser
	public static void assertAfterWrappingInMethod(String code, String expected) {
		String prefix = "public class A { public void m() { ";
		String postfix = " }}";
		String codeWrapped = prefix + code + postfix;
		String expectedWrapped = prefix + expected + postfix;
		String actual = JunitConversionLogic.convert(codeWrapped);
		assertEquals(convertWhitespaceForJavaParser(expectedWrapped), actual);
	}

	// add class after import so well formed for parser
	private static void assertAfterAddingClassAfter(String code, String expected) {
		String postfix = "public class A {}";
		String codeWrapped = code + postfix;
		String expectedWrapped = expected + postfix;
		String actual = JunitConversionLogic.convert(codeWrapped);
		assertEquals(convertWhitespaceForJavaParser(expectedWrapped), actual);
	}

	// wrap in class/method so well formed for parser
	private static void assertAfterWrappingInMethod(String originalImport, String originalMethod, String expectedImport,
			String expectedMethod) {
		String codeWrapped = originalImport + "public class A { public void m() { " + originalMethod + "}}";
		String expectedWrapped = expectedImport + "public class A { public void m() { " + expectedMethod + "}}";
		String actual = JunitConversionLogic.convert(codeWrapped);
		assertEquals(convertWhitespaceForJavaParser(expectedWrapped), actual);
	}

	// ------------------------------------------------------------------

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

	@Test
	void importWildcardMainPackage() {
		String originalImport = "import org.junit.*;";
		String expectedImport = "import org.junit.jupiter.api.*;";
		assertAfterAddingClassAfter(originalImport, expectedImport);
	}

	@Test
	void importJunit38Wildcard() {
		String originalImport = "import junit.framework.*;";
		String expectedImport = "import org.junit.jupiter.api.*;";
		assertAfterAddingClassAfter(originalImport, expectedImport);
	}

	@Test
	void importJunit38Assert() {
		String originalImport = "import junit.framework.Assert;";
		String expectedImport = "import org.junit.jupiter.api.Assertions;";
		assertAfterAddingClassAfter(originalImport, expectedImport);
	}

	@Test
	void specificTestAnnotation() {
		String annotation = "import org.junit.Test;";
		String expected = "import org.junit.jupiter.api.Test;";
		assertAfterAddingClassAfter(annotation, expected);
	}

	@Test
	void assertThat() {
		String code = "org.junit.Assert.assertThat(a, b);";
		String expected = "org.hamcrest.MatcherAssert.assertThat(a, b);";
		assertAfterWrappingInMethod(code, expected);
	}

	@Test
	void newImportForAssertThat() {
		String originalImport = "import static org.junit.Assert.*;\n";
		String originalMethod = "assertThat(xxx);";
		String expectedImport = "import static org.hamcrest.MatcherAssert.assertThat;\n"
				+ "import static org.junit.jupiter.api.Assertions.*;\n";
		assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, originalMethod);
	}

	@Test
	void importAlreadyPresentForAssertThat() {
		String originalImport = "import static org.hamcrest.MatcherAssert.assertThat;\n";
		String originalMethod = "assertThat(xxx);";
		assertAfterWrappingInMethod(originalImport, originalMethod, originalImport, originalMethod);
	}

	@Test
	void doNotUpdateIfAlreadyJupiter() {
		String code = "import static org.junit.jupiter.api.Assertions.*;";
		String actual = JunitConversionLogic.convert(code);
		assertEquals(code, actual);
	}

	@Test
	void singleAssertion() {
		String code = "assertTrue(message, actual);";
		String expected = "assertTrue(actual, message);";
		assertAfterWrappingInMethod(code, expected);
	}

	@Test
	void mutlipleAssertions() {
		String code = "assertTrue(message, actual);";
		String expected = "assertTrue(actual, message);";
		assertAfterWrappingInMethod(code, expected);
	}

	@Test
	void nonJunitMethodCall() {
		String code = "doWork(message, actual);";
		assertAfterWrappingInMethod(code, code);
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

	@ParameterizedTest
	@MethodSource("annotationsProvider")
	void newAnnotationNames(String oldAnnotationName, String newAnnotationName) {
		String code = "import org.junit." + oldAnnotationName + ";\n"
				+ "public class A { \n@" + oldAnnotationName + "\npublic void m() { }}";
		String expected = "import org.junit.jupiter.api." + newAnnotationName + ";\n"
				+ "public class A { \n@" + newAnnotationName + "\npublic void m() { }}";

		String actual = JunitConversionLogic.convert(code);
		assertEquals(convertWhitespaceForJavaParser(expected), actual);
	}

	static Stream<Arguments> annotationsProvider() {
		return Stream.of(Arguments.of("Before", "BeforeEach"),
				Arguments.of("BeforeClass", "BeforeAll"),
				Arguments.of("After", "AfterEach"),
				Arguments.of("AfterClass", "AfterAll"),
				Arguments.of("Ignore", "Disabled"));
	}

}
