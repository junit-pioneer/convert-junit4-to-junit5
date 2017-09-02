package jb;

import static jb.RegExHelper.*;

import java.util.regex.*;
import java.util.stream.*;

public class JunitConversionLogic {

	private JunitConversionLogic() {
		super();
	}

	public static String convert(String originalText) {
		String result = originalText;
		// don't update file if already on JUnit 5
		if (!originalText.contains("org.junit.jupiter")) {
			result = convertFromJUnit38(result);
			result = convertPackage(result);
			result = convertAnnotations(result);
			result = convertClassNames(result);
			result = addAssertThatImport(result);
			result = convertAssertionsAndAssumptions(result);
		}
		return result;
	}

	// if on JUnit 3.8, convert to JUnit 4 first
	private static String convertFromJUnit38(String result) {
		return result.replaceAll("junit.framework", "org.junit");
	}

	private static String convertPackage(String originalText) {
		String result = originalText;
		result = result.replaceAll("org.junit.Assert.assertThat", "org.hamcrest.MatcherAssert.assertThat");
		result = result.replaceAll("org.junit.", "org.junit.jupiter.api.");
		return result;
	}

	private static String convertAnnotations(String originalText) {
		String result = originalText;
		result = result.replace("org.junit.jupiter.api.BeforeClass", "org.junit.jupiter.api.BeforeAll");
		result = replaceUnlessFollowedByEscapingPackageName(result, "org.junit.jupiter.api.Before", "All",
				"org.junit.jupiter.api.BeforeEach");
		result = result.replace("org.junit.jupiter.api.AfterClass", "org.junit.jupiter.api.AfterAll");
		result = replaceUnlessFollowedByEscapingPackageName(result, "org.junit.jupiter.api.After", "All",
				"org.junit.jupiter.api.AfterEach");
		result = result.replace("org.junit.jupiter.api.Ignore", "org.junit.jupiter.api.Disabled");
		result = result.replace("@BeforeClass", "@BeforeAll");
		result = replaceUnlessFollowedByEscapingPackageName(result, "@Before", "All", "@BeforeEach");
		result = result.replace("@AfterClass", "@AfterAll");
		result = replaceUnlessFollowedByEscapingPackageName(result, "@After", "All", "@AfterEach");
		return result.replace("@Ignore", "@Disabled");
	}

	private static String convertClassNames(String originalText) {
		String result = originalText;
		result = result.replace("org.junit.jupiter.api.Assert", "org.junit.jupiter.api.Assertions");
		result = result.replace("org.junit.jupiter.api.Assume", "org.junit.jupiter.api.Assumptions");
		// don't update for hamcrest "MatcherAssert"
		result = replaceUnlessPreceededBy(result, "Assert.assert", "Matcher", "Assertions.assert");
		result = result.replace("Assert.fail", "Assertions.fail");
		result = result.replace("Assume.assume", "Assumptions.assume");
		return result;
	}

	// Assert that moved from junit core to hamcrest matchers
	private static String addAssertThatImport(String originalText) {
		String result = originalText;
		if (originalText.contains("assertThat") && !originalText.contains("org.hamcrest.MatcherAssert")) {
			result = result.replaceFirst("import", "import static org.hamcrest.MatcherAssert.assertThat;\nimport");
		}
		return result;
	}

	private static String convertAssertionsAndAssumptions(String originalText) {
		// split to find statements by separating on blocks or semicolon delimiters
		// and include the delimiter in the match
		return Pattern.compile("(?<=[;{}])").splitAsStream(originalText)
				.map(JunitConversionLogic::convertSingleAssertOrAssume)
				.collect(Collectors.joining(""));
	}

	private static String convertSingleAssertOrAssume(String oneLine) {
		if (oneLine.trim().startsWith("assert") || oneLine.trim().startsWith("assume")) {
			return MoveAssertionMessage.reorder(oneLine);
		}
		return oneLine;

	}
}
