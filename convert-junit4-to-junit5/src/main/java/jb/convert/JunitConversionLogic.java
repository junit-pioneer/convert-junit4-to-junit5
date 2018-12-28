package jb.convert;

import com.github.javaparser.ast.CompilationUnit;
import jb.ProjectRecorder;
import jb.configuration.JunitConversionLogicConfiguration;
import jb.convert.ast.AssertThatMigration;
import jb.convert.ast.AssumeMigration;
import jb.convert.ast.CategoryMigration;
import jb.convert.ast.AssertMigration;
import jb.convert.ast.GeneralMigration;
import jb.convert.ast.ProjectProbe;
import jb.convert.ast.ReduceToDefaultScope;
import jb.convert.ast.SetupMethodMigration;
import jb.convert.ast.TestAnnotationMigration;

public class JunitConversionLogic {

    private final JunitConversionLogicConfiguration configuration;
    private final ProjectRecorder projectRecorder;

    public JunitConversionLogic(JunitConversionLogicConfiguration configuration, ProjectRecorder projectRecorder) {
        this.configuration = configuration;
        this.projectRecorder = projectRecorder;
    }

    public ConversionResultBuilder convert(String originalCode) {
        ProjectProbe projectProbe = new ProjectProbe(projectRecorder);
        CompilationUnit compilationUnit = configuration.javaParser().parse(originalCode);
        projectProbe.visit(compilationUnit, null);

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
        boolean updated = performAstBasedConversions(compilationUnit);
        if (!updated) {
            return result.outcome(ConversionOutcome.Unchanged);
        }
        String updatedCode = configuration.javaParser().print(compilationUnit);
        if (originalCode.equals(updatedCode)) {
            return result.outcome(ConversionOutcome.Unchanged);
        }
        return result.outcome(ConversionOutcome.Converted).code(updatedCode);
    }

    private boolean performAstBasedConversions(CompilationUnit cu) {
        AssertThatMigration assertThatMigration = new AssertThatMigration();
        assertThatMigration.visit(cu, null);

        AssertMigration assertMigration = new AssertMigration();
        assertMigration.visit(cu, null);

        AssumeMigration assumeMigration = new AssumeMigration();
        assumeMigration.visit(cu, null);

        SetupMethodMigration setupMethodMigration = new SetupMethodMigration();
        setupMethodMigration.visit(cu, null);

        TestAnnotationMigration testAnnotationMigration = new TestAnnotationMigration();
        testAnnotationMigration.visit(cu, null);

        ReduceToDefaultScope reduceToDefaultScope = new ReduceToDefaultScope();
        reduceToDefaultScope.visit(cu, new ReduceToDefaultScope.Accumulator());

        CategoryMigration categoryMigration = new CategoryMigration(projectRecorder);
        categoryMigration.visit(cu, null);

        GeneralMigration generalMigration = new GeneralMigration();
        generalMigration.visit(cu, null);

        return assertThatMigration.performedUpdate()
                || assertMigration.performedUpdate()
                || assumeMigration.performedUpdate()
                || setupMethodMigration.performedUpdate()
                || testAnnotationMigration.performedUpdate()
                || reduceToDefaultScope.performedUpdate()
                || categoryMigration.performedUpdate()
                || generalMigration.performedUpdate()
                ;
    }

}
