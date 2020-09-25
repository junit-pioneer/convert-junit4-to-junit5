package jb.convert.ast;

import com.github.javaparser.HasParentNode;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import org.junit.jupiter.api.Tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static jb.convert.ast.tools.ImportDeclarations.addImportTo;

public class CategoryClassToTagMetaAnnotationConversion extends ModifierVisitor<Void> {

    @Override
    public Visitable visit(ClassOrInterfaceDeclaration n, Void arg) {
        super.visit(n, arg);

        TypeDeclaration<?> replacement = new AnnotationDeclaration(new NodeList<>(), n.getNameAsString());
        n.replace(replacement);
        replacement.setModifiers(n.getModifiers());

        addImportTo(replacement, ElementType.class);
        addImportTo(replacement, Retention.class);
        addImportTo(replacement, RetentionPolicy.class);
        addImportTo(replacement, Target.class);

        replacement.addSingleMemberAnnotation(Target.class, arrayOf(ElementType.TYPE, ElementType.METHOD));
        replacement.addSingleMemberAnnotation(Retention.class, "RetentionPolicy.RUNTIME");
        replacement.addSingleMemberAnnotation(Tag.class, "\"" + packageNameOf(replacement) + "." + n.getNameAsString() + "\"");

        return replacement;
    }

    private ArrayInitializerExpr arrayOf(ElementType... types) {
        List<FieldAccessExpr> elements = Arrays.stream(types)
                .map(type -> new FieldAccessExpr(new NameExpr("ElementType"), type.name()))
                .collect(Collectors.toList());
        NodeList<Expression> expressions = new NodeList<>();
        expressions.addAll(elements);
        return new ArrayInitializerExpr(expressions);
    }

    private String packageNameOf(HasParentNode<?> n) {
        return n.findAncestor(CompilationUnit.class).map(it -> it.getPackageDeclaration().get()).get().getNameAsString();
    }

}
