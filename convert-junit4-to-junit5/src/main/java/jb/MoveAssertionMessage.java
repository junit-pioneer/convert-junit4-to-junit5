package jb;

import java.util.*;

/**
 * This is not 100% but it reorders most assertion message parameters properly.
 * It also feels like it will be a pain to figure this out later. Glad I have
 * good unit tests!
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

	private MoveAssertionMessage() {
		super();
	}

	public static String reorder(String code) {
		String dotAll = "(?s)";
		String openParen = "\\(";
		String closeParen = "\\)";
		String anyChar = ".*";
		String whitespace = "\\s*";
		String semicolon = ";";
		String methodName = code.replaceFirst(dotAll + openParen + anyChar + "$", "");
		String params = code.replaceFirst(dotAll + "^" + methodName + openParen, "")
				.replaceFirst(closeParen + whitespace + semicolon + whitespace + "$", "");
		String closeParenAndSemicolon = code.replaceFirst(
				dotAll + anyChar + "(" + closeParen + whitespace + semicolon + whitespace + ")",
				"$1");
		String[] paramArray = splitParams(params);

		return moveMessageParamToEnd(code, methodName, closeParenAndSemicolon, paramArray);
	}

	// split by commas but not if within a string or parens
	// what would be a better way of doing this?
	private static String[] splitParams(String params) {
		List<String> list = new ArrayList<>();
		StringBuilder builder = new StringBuilder();
		int numNestedParams = 0;
		boolean insideQuotes = false;
		for (int i = 0; i < params.length(); i++) {
			char ch = params.charAt(i);
			// start a new parameter where needed
			if (ch == ',' && numNestedParams == 0 && ! insideQuotes) {
				list.add(builder.toString());
				builder = new StringBuilder();
			} else {
				builder.append(ch);
			}
			// count nested paren level
			if (ch == '(') {
				numNestedParams++;
			} else if (ch == ')') {
				numNestedParams--;
			} else if (ch == '"' && ! previousCharacterIsBackslash(params, i)) {
				insideQuotes = ! insideQuotes;
			}

		}
		list.add(builder.toString());
		adjustParameterSpaces(list);

		return list.toArray(new String[0]);
	}

	private static boolean previousCharacterIsBackslash(String params, int currentPosition) {
		if (currentPosition == 0) {
			return false;
		}
		return params.charAt(currentPosition - 1) == '\\';
	}

	// adjust spaces before/after each parameter to preserve logical position in
	// file
	private static void adjustParameterSpaces(List<String> list) {
		shiftLeadingWhitespaceForParams(list);
		shiftTrailingWhitespaceForParams(list);
	}

	private static void shiftLeadingWhitespaceForParams(List<String> list) {
		String regex = "(?s)^(\\s*)(.*?)$";
		String first = list.get(0);
		String previousLeadingWhitespace = first.replaceFirst(regex, "$1");
		String trimmedFirstElement = first.replaceFirst(regex, "$2");

		for (int i = 1; i < list.size(); i++) {
			String current = list.get(i);
			String currentLeadingWhitespace = current.replaceFirst(regex, "$1");
			String trimmedCurrentElement = current.replaceFirst(regex, "$2");

			list.set(i, previousLeadingWhitespace + trimmedCurrentElement);
			previousLeadingWhitespace = currentLeadingWhitespace;
		}
		list.set(0, previousLeadingWhitespace + trimmedFirstElement);
	}

	private static void shiftTrailingWhitespaceForParams(List<String> list) {
		String regex = "(?s)^(.*?)(\\s*)$";
		String last = list.get(list.size() - 1);
		String trimmedLastElement = last.replaceFirst(regex, "$1");
		String previousTrailingWhitespace = last.replaceFirst(regex, "$2");

		for (int i = 0; i < list.size() - 1; i++) {
			String current = list.get(i);
			String trimmedCurrentElement = current.replaceFirst(regex, "$1");
			list.set(i, trimmedCurrentElement + previousTrailingWhitespace);

			previousTrailingWhitespace = current.replaceFirst(regex, "$2");
		}
		list.set(list.size() - 1, trimmedLastElement + previousTrailingWhitespace);
	}

	private static String moveMessageParamToEnd(String code, String methodName, String closeParenAndSemicolon,
			String[] paramArray) {
		String trimmedMethodName = methodName.trim();
		String result = code;
		if (paramArray.length == 3 && TWO_PARAM_METHODS.contains(trimmedMethodName)) {
			String messageParam = paramArray[0];
			String expectedParam = paramArray[1];
			String actualParam = paramArray[2];
			result = methodName + "(" + expectedParam + "," + actualParam + "," + messageParam + closeParenAndSemicolon;

		} else if (paramArray.length == 2 && ONE_PARAM_METHODS.contains(trimmedMethodName)) {
			String messageParam = paramArray[0];
			String actualParam = paramArray[1];
			result = methodName + "(" + actualParam + "," + messageParam + closeParenAndSemicolon;

		} else if (paramArray.length == 4 && THREE_PARAM_METHODS.contains(trimmedMethodName)) {
			String messageParam = paramArray[0];
			String expectedParam = paramArray[1];
			String actualParam = paramArray[2];
			String deltaParam = paramArray[3];
			result = methodName + "(" + expectedParam + "," + actualParam + "," + deltaParam + "," + messageParam
					+ closeParenAndSemicolon;

		}
		return result;
	}

}
