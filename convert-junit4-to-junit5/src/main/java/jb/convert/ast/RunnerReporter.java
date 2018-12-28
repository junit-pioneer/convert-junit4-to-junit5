package jb.convert.ast;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import jb.convert.ConversionResultBuilder;
import jb.convert.UsedFeature;
import org.junit.runner.RunWith;

public class RunnerReporter extends RuleReporter {

    @Override
    public void visit(ClassOrInterfaceDeclaration n, ConversionResultBuilder resultBuilder) {
        n.getAnnotationByClass(RunWith.class).ifPresent(annotation -> {
            annotation.ifSingleMemberAnnotationExpr(runWith -> {
                runWith.getMemberValue().ifClassExpr(classExpr -> {
                    String typeAsString = classExpr.getTypeAsString();
                    resultBuilder.usedFeature(new UsedFeature(false, "Runner", typeAsString));
                });
            });
        });
    }
}
