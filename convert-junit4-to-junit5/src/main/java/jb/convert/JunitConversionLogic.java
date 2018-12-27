package jb.convert;

import com.github.javaparser.ast.CompilationUnit;
import jb.ProjectRecorder;
import jb.configuration.JunitConversionLogicConfiguration;
import jb.convert.ast.AssertThatMigration;
import jb.convert.ast.AssumeMigration;
import jb.convert.ast.CategoryMigration;
import jb.convert.ast.AssertMigration;
import jb.convert.ast.ProjectProbe;
import jb.convert.ast.ReduceToDefaultScope;
import jb.convert.ast.TestMethodMigration;
import jb.convert.regex.SearchAndReplace;
import org.junit.jupiter.api.Assertions;

public class JunitConversionLogic {

    private final JunitConversionLogicConfiguration configuration;
    private final ProjectRecorder projectRecorder;
    private final SearchAndReplace searchAndReplace = new SearchAndReplace();

    public JunitConversionLogic(JunitConversionLogicConfiguration configuration, ProjectRecorder projectRecorder) {
        this.configuration = configuration;
        this.projectRecorder = projectRecorder;
    }

    public ConversionResultBuilder convert(String originalCode) {
        ProjectProbe projectProbe = new ProjectProbe(projectRecorder);
        projectProbe.visit(configuration.javaParser().parse(originalCode), null);

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
        AssertThatMigration assertThatMigration = new AssertThatMigration();
        assertThatMigration.visit(cu, null);

        AssertMigration assertMigration = new AssertMigration();
        assertMigration.visit(cu, null);

        AssumeMigration assumeMigration = new AssumeMigration();
        assumeMigration.visit(cu, null);

        TestMethodMigration testMethodMigration = new TestMethodMigration();
        testMethodMigration.visit(cu, null);

        ReduceToDefaultScope reduceToDefaultScope = new ReduceToDefaultScope();
        reduceToDefaultScope.visit(cu, new ReduceToDefaultScope.Accumulator());

        CategoryMigration categoryMigration = new CategoryMigration(projectRecorder);
        categoryMigration.visit(cu, null);
        return assertThatMigration.performedUpdate()
                || assertMigration.performedUpdate()
                || assumeMigration.performedUpdate()
                || testMethodMigration.performedUpdate()
                || reduceToDefaultScope.performedUpdate()
                || categoryMigration.performedUpdate();
    }

}
