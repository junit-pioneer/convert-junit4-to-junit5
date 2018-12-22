package jb;

import com.github.javaparser.ast.CompilationUnit;
import jb.configuration.JunitConversionLogicConfiguration;
import org.junit.jupiter.api.Assertions;

import static jb.RegExHelper.replaceAllLiterals;
import static jb.RegExHelper.replaceUnlessFollowedByEscapingPackageName;
import static jb.RegExHelper.replaceUnlessPreceededBy;

class JunitConversionLogic {

	private JunitConversionLogicConfiguration configuration;

	JunitConversionLogic(JunitConversionLogicConfiguration configuration) {
		this.configuration = configuration;
	}

	ConversionResult convert(String originalText) {
		// don't update file if already on JUnit 5
		if (originalText.contains("org.junit.jupiter")) {
			return ConversionResult.skipped("already using junit 5");
		}
		// only look at files that contain JUnit 4 imports
		if (!originalText.contains("org.junit.")) {
			return ConversionResult.skipped("no junit 4 code to migrate");
		}
		if (originalText.contains("@Rule")) {
			return ConversionResult.skipped("rules not supported");
		}
		if (originalText.contains("@Category")) {
			return ConversionResult.skipped("category not supported");
		}
		if (originalText.contains("@RunWith")) {
			return ConversionResult.skipped("runner not supported");
		}
		// easier to do these with plain text
		String result = originalText;
		result = convertPackage(result);
		result = convertAnnotations(result);
		result = convertClassNames(result);
		result = addAssertThatImport(result);


		// easier to do move parameter order with AST parser

		CompilationUnit cu = null;
		try {
			cu = configuration.javaParser().parse(result);
		} catch (Exception e) {
		    e.printStackTrace();
			Assertions.assertEquals(originalText, result);
			Assertions.fail("the original source is not parsable");
		}
		boolean updated = convertAssertionsAndAssumptionMethodParamOrder(cu);
		if (! originalText.equals(result) || updated) {
			// only update result if there were changes
			result = configuration.javaParser().print(cu);
		}
		if (originalText.equals(result)) {
			return ConversionResult.unchanged();
		}
		return ConversionResult.converted(result);
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

	private boolean convertAssertionsAndAssumptionMethodParamOrder(CompilationUnit cu) {
		MoveMessageParameterVisitor messageParameterLocation = new MoveMessageParameterVisitor();
		messageParameterLocation.visit(cu, null);
		TestMethodMigration testMethodMigration = new TestMethodMigration();
		testMethodMigration.visit(cu, null);
		return messageParameterLocation.performedUpdate()|| testMethodMigration.performedUpdate();
	}

}
