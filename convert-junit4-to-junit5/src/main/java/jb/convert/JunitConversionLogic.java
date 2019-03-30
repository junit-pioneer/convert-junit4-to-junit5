package jb.convert;

import com.github.javaparser.ast.CompilationUnit;
import jb.ProjectRecorder;
import jb.configuration.JunitConversionLogicConfiguration;
import jb.convert.ast.AssertConversion;
import jb.convert.ast.AssertThatConversion;
import jb.convert.ast.AssumeConversion;
import jb.convert.ast.CategoryConversion;
import jb.convert.ast.GeneralConversion;
import jb.convert.ast.ProjectProbe;
import jb.convert.ast.ReduceToDefaultScopeConversion;
import jb.convert.ast.RuleReporter;
import jb.convert.ast.RunnerReporter;
import jb.convert.ast.SetupMethodConversion;
import jb.convert.ast.TestAnnotationConversion;

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
            return ConversionResult.skipped("no junit 4 code to convert");
        }
        ConversionResultBuilder result = new ConversionResultBuilder();
        reportUnsupportedFeatures(compilationUnit, result);
        boolean updated = performAstBasedConversions(compilationUnit);
        if (!updated) {
            return result.outcome(ConversionOutcome.Unchanged);
        }
        String updatedCode = configuration.javaParser().print(compilationUnit);
        return result.outcome(ConversionOutcome.Converted).code(updatedCode);
    }

    private void reportUnsupportedFeatures(CompilationUnit compilationUnit, ConversionResultBuilder result) {
        new RuleReporter().visit(compilationUnit, result);
        new RunnerReporter().visit(compilationUnit, result);
    }

    private boolean performAstBasedConversions(CompilationUnit cu) {
        AssertThatConversion assertThatConversion = new AssertThatConversion();
        assertThatConversion.visit(cu, null);

        AssertConversion assertConversion = new AssertConversion();
        assertConversion.visit(cu, null);

        AssumeConversion assumeConversion = new AssumeConversion();
        assumeConversion.visit(cu, null);

        SetupMethodConversion setupMethodConversion = new SetupMethodConversion();
        setupMethodConversion.visit(cu, null);

        TestAnnotationConversion testAnnotationConversion = new TestAnnotationConversion(configuration);
        testAnnotationConversion.visit(cu, null);

        ReduceToDefaultScopeConversion reduceToDefaultScopeConversion = new ReduceToDefaultScopeConversion();
        reduceToDefaultScopeConversion.visit(cu, new ReduceToDefaultScopeConversion.Accumulator());

        CategoryConversion categoryConversion = new CategoryConversion(projectRecorder);
        categoryConversion.visit(cu, null);

        GeneralConversion generalConversion = new GeneralConversion();
        generalConversion.visit(cu, null);

        return assertThatConversion.performedUpdate()
                || assertConversion.performedUpdate()
                || assumeConversion.performedUpdate()
                || setupMethodConversion.performedUpdate()
                || testAnnotationConversion.performedUpdate()
                || reduceToDefaultScopeConversion.performedUpdate()
                || categoryConversion.performedUpdate()
                || generalConversion.performedUpdate()
                ;
    }

}
