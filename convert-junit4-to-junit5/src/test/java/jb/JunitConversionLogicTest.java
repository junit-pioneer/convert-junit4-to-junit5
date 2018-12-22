package jb;

import jb.convert.ConversionOutcome;
import jb.convert.ConversionResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jb.JunitConversionLogicFixture.assertAfterAddingClassAfter;
import static jb.JunitConversionLogicFixture.assertAfterWrappingInMethod;
import static jb.JunitConversionLogicFixture.assertUnchangedAfterWrappingInMethod;
import static jb.JunitConversionLogicFixture.assertUnchangedWrappingInClass;
import static jb.JunitConversionLogicFixture.assertWrappingInClass;
import static jb.JunitConversionLogicFixture.prettyPrint;
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
		assertUnchangedAfterWrappingInMethod(originalImport, originalMethod);
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

	@ParameterizedTest
	@MethodSource("annotationsProvider")
	void newAnnotationNames(String oldAnnotationName, String newAnnotationName) {
		String code = "import org.junit." + oldAnnotationName + ";\n"
				+ "public class A { \n@" + oldAnnotationName + "\npublic void m() { }}";
		String expected = "import org.junit.jupiter.api." + newAnnotationName + ";\n"
				+ "public class A { \n@" + newAnnotationName + "\npublic void m() { }}";

		String actual = convertAndPrettyPrint(code);
		assertEquals(prettyPrint(expected), actual);
	}

	static Stream<Arguments> annotationsProvider() {
		return Stream.of(Arguments.of("Before", "BeforeEach"),
				Arguments.of("BeforeClass", "BeforeAll"),
				Arguments.of("After", "AfterEach"),
				Arguments.of("AfterClass", "AfterAll"),
				Arguments.of("Ignore", "Disabled"));
	}

	private String convertAndPrettyPrint(String code){
		return JunitConversionLogicFixture.prettyPrint(JunitConversionLogicFixture.convert(code).code);
	}

}
