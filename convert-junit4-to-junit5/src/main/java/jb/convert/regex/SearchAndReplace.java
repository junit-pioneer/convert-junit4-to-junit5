package jb.convert.regex;

import static jb.convert.regex.RegExHelper.replaceAllLiterals;
import static jb.convert.regex.RegExHelper.replaceUnlessFollowedByEscapingPackageName;
import static jb.convert.regex.RegExHelper.replaceUnlessPreceededBy;

public class SearchAndReplace {

    public String convert(String originalCode) {
        // easier to do these with plain text
        String currentCode = originalCode;
        currentCode = convertPackage(currentCode);
        currentCode = convertAnnotations(currentCode);
        return currentCode;
    }

    private String convertPackage(String originalText) {
        String result = originalText;
        result = replaceAllLiterals(result, "org.junit.Assert.assertThat", "org.hamcrest.MatcherAssert.assertThat");
        result = replaceAllLiterals(result, "import org.junit.experimental.categories.Category;\n", "");
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
        result = replaceUnlessFollowedByEscapingPackageName(result, "@Before", "[All|Scenario]", "@BeforeEach");
        result = result.replace("@AfterClass", "@AfterAll");
        result = replaceUnlessFollowedByEscapingPackageName(result, "@After", "All", "@AfterEach");
        return result.replace("@Ignore", "@Disabled");
    }

}
