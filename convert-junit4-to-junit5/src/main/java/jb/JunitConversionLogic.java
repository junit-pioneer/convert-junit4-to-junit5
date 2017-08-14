package jb;

public class JunitConversionLogic {

	private JunitConversionLogic() {
		super();
	}

	public static String convert(String originalText) {
		String result = originalText;
		// don't update file if already on JUnit 5
		if (!originalText.contains("org.junit.jupiter")) {
			result = convertPackage(result);
			result = convertAnnotations(result);
			result = convertClassNames(result);
			result = addAssertThatImport(result);
		}
		return result;
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
		result = replaceUnlessFollowedBy(result, "org.junit.jupiter.api.Before", "All",
				"org.junit.jupiter.api.BeforeEach");
		result = result.replace("org.junit.jupiter.api.AfterClass", "org.junit.jupiter.api.AfterAll");
		result = replaceUnlessFollowedBy(result, "org.junit.jupiter.api.After", "All",
				"org.junit.jupiter.api.AfterEach");
		result = result.replace("org.junit.jupiter.api.Ignore", "org.junit.jupiter.api.Disabled");
		result = result.replace("@BeforeClass", "@BeforeAll");
		result = replaceUnlessFollowedBy(result, "@Before", "All", "@BeforeEach");
		result = result.replace("@AfterClass", "@AfterAll");
		result = replaceUnlessFollowedBy(result, "@After", "All", "@AfterEach");
		return result.replace("@Ignore", "@Disabled");
	}

	private static String convertClassNames(String originalText) {
		String result = originalText;
		result = result.replace("org.junit.jupiter.api.Assert", "org.junit.jupiter.api.Assertions");
		result = result.replace("org.junit.jupiter.api.Assume", "org.junit.jupiter.api.Assumptions");
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

	private static String replaceUnlessFollowedBy(String originalText,
			String targetFullyQualifiedName, String exceptIfFollowing, String replacement) {
		String escapedDotsInPackageName = targetFullyQualifiedName.replace(".", "\\.");
		String notFollowedBy = "(?!" + exceptIfFollowing + ")";
		String regex = escapedDotsInPackageName + notFollowedBy;
		return originalText.replaceAll(regex, replacement);
	}

}
