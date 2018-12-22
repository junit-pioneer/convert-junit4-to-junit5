package jb.convert.regex;

import static jb.convert.regex.RegExHelper.replaceAllLiterals;
import static jb.convert.regex.RegExHelper.replaceUnlessFollowedByEscapingPackageName;
import static jb.convert.regex.RegExHelper.replaceUnlessPreceededBy;

public class SearchAndReplace {

    public String convert(String originalCode) {
        String currentCode = originalCode;
        currentCode = convertPackage(currentCode);
        currentCode = convertAnnotations(currentCode);
        currentCode = convertClassNames(currentCode);
        currentCode = addAssertThatImport(currentCode);
        return currentCode;
    }

    private String convertPackage(String originalText) {
        String result = originalText;
        result = replaceAllLiterals(result, "org.junit.Assert.assertThat", "org.hamcrest.MatcherAssert.assertThat");
        result = replaceAllLiterals(result, "org.junit.", "org.junit.jupiter.api.");
        return result;
    }

    private String convertAnnotations(String originalText) {
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

    private String convertClassNames(String originalText) {
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
    private String addAssertThatImport(String originalText) {
        String result = originalText;
        if (originalText.contains("assertThat") && !originalText.contains("org.hamcrest.MatcherAssert")) {
            result = result.replaceFirst("import", "import static org.hamcrest.MatcherAssert.assertThat;\nimport");
        }
        return result;
    }

}