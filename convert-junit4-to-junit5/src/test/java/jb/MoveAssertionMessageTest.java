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
	void whiteSpace() {
		String code = "assertEquals  (  message, expected, actual  )  ;";
		String expected = "assertEquals(expected, actual, message);";
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

	// TODO methods, string concat, multiple lines, raw string
	// TODO multiple assertions (or deal with in caller?)
	// TODO implement assertThat/assumeThat - remains unchanged?
}
