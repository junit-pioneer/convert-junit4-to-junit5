package jb;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

public class MoveAssertionMessageTest {

	@Test
	void notAnAssertion() {
		String code = "// ignore me";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(code, actual);
	}

	@Test
	void noMessage() {
		String code = "assertEquals(expected, actual);";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(code, actual);
	}

	@Test
	void message() {
		String code = "assertEquals(message, expected, actual);";
		String expected = "assertEquals(expected, actual, message);";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(expected, actual);
	}

	@Test
	void whiteSpaceForThreeParams() {
		String code = "assertEquals  (  message,  expected,   actual  )  ;";
		String expected = "assertEquals  (  expected,  actual,   message  )  ;";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(expected, actual);
	}
	
	
	@Test
	void whiteSpaceForTwoParams() {
		String code = "assertNotNull  (  message,   actual  )  ;";
		String expected = "assertNotNull  (  actual,   message  )  ;";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(expected, actual);
	}

	@Test
	void notEnoughParameters() {
		String code = "assertEquals(random);";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(code, actual);
	}

	@Test
	void tooManyParameters() {
		String code = "assertEquals(a, b, c, d, e);";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(code, actual);
	}

	@ParameterizedTest
	@ValueSource(strings = { "assertArrayEquals", "assertEquals", "assertNotSame", "assertSame" })
	void allJunit4MethodNamesReorderExpectedActual(String methodName) {
		String code = methodName + "(message, expected, actual);";
		String expected = methodName + "(expected, actual, message);";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(expected, actual);
	}

	@ParameterizedTest
	@ValueSource(strings = { "assertFalse", "assertNotNull", "assertNull", "assertTrue", "assumeFalse",
			"assumeNoException", "assumeNotNull", "assumeTrue" })
	void allJunit4MethodNamesReorderSingleParam(String methodName) {
		String code = methodName + "(message, actual);";
		String expected = methodName + "(actual, message);";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(expected, actual);
	}

	void deltaDoubleReorders() {
		String code = "assertEquals(message, expected, actual, delta);";
		String expected = "assertEquals(expected, actual, delta, message);";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(expected, actual);
	}

	@ParameterizedTest
	@ValueSource(strings = { "assertDoubles", "assertCustom", "assumeBecauseISaidSo" })
	void otherMethodNamesDoNotChangeParamOrder(String methodName) {
		String code = methodName + "(message, expected, actual);";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(code, actual);
	}
	
	@Test
	void assertThatRemainsUnchanged() {
		String code = "assertThat(message, actual, endsWith(abc))";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(code, actual);
	}
	
	@Test
	void concatInParams() {
		String code = "assertEquals(message + more, expected + more, actual + more);";
		String expected = "assertEquals(expected + more, actual + more, message + more);";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(expected, actual);
	}
	
	@Test
	void methodsInParams() {
		String code = "assertEquals(message(a,b,c), expected(1,2,3), actual(x,y,z));";
		String expected = "assertEquals(expected(1,2,3), actual(x,y,z), message(a,b,c));";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(expected, actual);
	}
	
	@Test
	void newLines() {
		String code = "assertEquals(\nmessage, \nexpected, \nactual);";
		String expected = "assertEquals(\nexpected, \nactual, \nmessage);";
		String actual = MoveAssertionMessage.reorder(code);
		assertEquals(expected, actual);
	}
	
}
