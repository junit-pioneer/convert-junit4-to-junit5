package jb.convert.ast;

import com.github.javaparser.Position;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import jb.convert.ConversionResultBuilder;
import jb.convert.UsedFeature;
import jb.convert.ast.tools.Nodes;
import org.junit.runner.RunWith;

public class RunnerReporter extends RuleReporter {

    @Override
    public void visit(ClassOrInterfaceDeclaration n, ConversionResultBuilder resultBuilder) {
        n.getAnnotationByClass(RunWith.class).ifPresent(annotation -> {
            annotation.ifSingleMemberAnnotationExpr(runWith -> {
                runWith.getMemberValue().ifClassExpr(classExpr -> {
                    String typeAsString = classExpr.getTypeAsString();
                    Position position = Nodes.beginOrStartOfFile(annotation);
                    resultBuilder.usedFeature(new UsedFeature(false, "Runner", typeAsString, position));
                });
            });
        });
    }
}
