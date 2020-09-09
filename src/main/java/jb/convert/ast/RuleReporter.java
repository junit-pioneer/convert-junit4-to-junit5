package jb.convert.ast;

import com.github.javaparser.Position;
import com.github.javaparser.ast.body.FieldDeclaration;
import jb.convert.ConversionResultBuilder;
import jb.convert.UsedFeature;
import jb.convert.ast.tools.Nodes;
import org.junit.Rule;

public class RuleReporter extends ReportVisitor {

    @Override
    public void visit(FieldDeclaration fieldDeclaration, ConversionResultBuilder resultBuilder) {
        super.visit(fieldDeclaration, resultBuilder);
        fieldDeclaration.getAnnotationByClass(Rule.class).ifPresent(annotation -> {
            Position position = Nodes.beginOrStartOfFile(annotation);
            resultBuilder.usedFeature(new UsedFeature(false, "Rule", fieldDeclaration.getCommonType().toString(), position));
        });

    }

}
