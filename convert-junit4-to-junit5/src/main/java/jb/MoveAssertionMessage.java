package jb;

import java.util.*;

/**
 * This is not 100% but it reorders most assertion message parameters properly
 * 
 * @author jeanne
 *
 */
public class MoveAssertionMessage {

	private static final Set<String> ONE_PARAM_METHODS = new HashSet<>(
			Arrays.asList("assertFalse", "assertTrue", "assertNull", "assertNotNull", "assumeTrue", "assumeFalse",
					"assumeNoException", "assumeNull", "assumeNotNull"));

	private static final Set<String> TWO_PARAM_METHODS = new HashSet<>(
			Arrays.asList("assertEquals", "assertArrayEquals", "assertNotSame", "assertSame"));

	private static final Set<String> THREE_PARAM_METHODS = new HashSet<>(Arrays.asList("assertEquals"));

	public static String reorder(String code) {
		String openParen = "\\(";
		String closeParen = "\\)";
		String anyChar = ".*";
		String whitespace = "\\s*";
		String result = code;
		String methodName = code.replaceFirst(openParen + anyChar + "$", "").trim();
		String params = code.replaceFirst("^" + anyChar + openParen, "")
				.replaceFirst(closeParen + whitespace + ";$", "");
		String[] paramArray = params.split(",");

		if (paramArray.length == 3 && TWO_PARAM_METHODS.contains(methodName)) {
			String messageParam = paramArray[0].trim();
			String expectedParam = paramArray[1].trim();
			String actualParam = paramArray[2].trim();
			result = methodName + "(" + expectedParam + ", " + actualParam + ", " + messageParam + ");";

		} else if (paramArray.length == 2 && ONE_PARAM_METHODS.contains(methodName)) {
			String messageParam = paramArray[0].trim();
			String actualParam = paramArray[1].trim();
			result = methodName + "(" + actualParam + ", " + messageParam + ");";

		} else if (paramArray.length == 4 && THREE_PARAM_METHODS.contains(methodName)) {
			String messageParam = paramArray[0].trim();
			String expectedParam = paramArray[1].trim();
			String actualParam = paramArray[2].trim();
			String deltaParam = paramArray[3].trim();
			result = methodName + "(" + expectedParam + ", " + actualParam + ", " + deltaParam + ", " + messageParam
					+ ");";

		}
		return result;
	}

}
