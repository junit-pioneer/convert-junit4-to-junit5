package jb.convert;

import com.github.javaparser.ast.CompilationUnit;
import jb.configuration.JunitConversionLogicConfiguration;
import jb.convert.ast.MoveMessageParameterVisitor;
import jb.convert.ast.TestMethodMigration;
import jb.convert.regex.SearchAndReplace;
import org.junit.jupiter.api.Assertions;

public class JunitConversionLogic {

	private final JunitConversionLogicConfiguration configuration;
	private final SearchAndReplace searchAndReplace = new SearchAndReplace();

	public JunitConversionLogic(JunitConversionLogicConfiguration configuration) {
		this.configuration = configuration;
	}

	public ConversionResultBuilder convert(String originalCode) {
		// don't update file if already on JUnit 5
		if (originalCode.contains("org.junit.jupiter")) {
			return ConversionResult.skipped("already using junit 5");
		}
		// only look at files that contain JUnit 4 imports
		if (!originalCode.contains("org.junit.")) {
			return ConversionResult.skipped("no junit 4 code to migrate");
		}
        ConversionResultBuilder result = new ConversionResultBuilder();
        if (originalCode.contains("@Rule")) {
            result.unsupportedFeature("rules");
		}
		if (originalCode.contains("@Category")) {
            result.unsupportedFeature("categories");
		}
		if (originalCode.contains("@RunWith")) {
            result.unsupportedFeature("runner");
		}

		String currentCode = searchAndReplace.convert(originalCode);

		CompilationUnit cu = parseAst(currentCode, originalCode);
		boolean updated = performAstBasedConversions(cu);
		if (updated) {
			// only update result if there were changes
			currentCode = configuration.javaParser().print(cu);
		}
		if (originalCode.equals(currentCode)) {
            return result.outcome(ConversionOutcome.Unchanged);
		}
        return result.outcome(ConversionOutcome.Converted).code(currentCode);
	}

	private CompilationUnit parseAst(String currentCode, String originalCode) {
		CompilationUnit cu = null;
		try {
			cu = configuration.javaParser().parse(currentCode);
		} catch (Exception e) {
		    e.printStackTrace();
			Assertions.assertEquals(originalCode, currentCode);
			Assertions.fail("the original source is not parsable");
		}
		return cu;
	}

	private boolean performAstBasedConversions(CompilationUnit cu) {
		MoveMessageParameterVisitor messageParameterLocation = new MoveMessageParameterVisitor();
		messageParameterLocation.visit(cu, null);
		TestMethodMigration testMethodMigration = new TestMethodMigration();
		testMethodMigration.visit(cu, null);
		return messageParameterLocation.performedUpdate()|| testMethodMigration.performedUpdate();
	}

}
