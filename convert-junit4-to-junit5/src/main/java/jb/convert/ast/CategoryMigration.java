package jb.convert.ast;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import jb.ClassName;
import jb.ProjectRecorder;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CategoryMigration extends ModifierVisitor<Void> {
    private final ProjectRecorder projectRecorder;
    private boolean updated = false;

    public boolean performedUpdate() {
        return updated;
    }

    public CategoryMigration(ProjectRecorder projectRecorder) {
        this.projectRecorder = projectRecorder;
    }

    @Override
    public Visitable visit(SingleMemberAnnotationExpr n, Void arg) {
        super.visit(n, arg);
        if ("Category".equals(n.getNameAsString())) {
            updated = true;
            ClassOrInterfaceType type = (ClassOrInterfaceType) ((ClassExpr) n.getMemberValue()).getType();
            SimpleName simpleName = type.getName();
            ClassName categoryClassName = findCanonicalNameFor(simpleName, ImportDeclarations.imports(n));
            projectRecorder.referencesCategory(categoryClassName);
            return new MarkerAnnotationExpr(new Name(simpleName.getIdentifier()));
        }
        return n;
    }

    private ClassName findCanonicalNameFor(SimpleName simpleName, NodeList<ImportDeclaration> imports) {
        List<ImportDeclaration> collect = imports.stream()
                .filter(it -> it.getName().getIdentifier().equals(simpleName.getIdentifier()))
                .collect(toList());
        if (collect.isEmpty() || collect.size() > 1) {
            throw new IllegalStateException("unable to resolve category class to single import, instead found " + collect.size());
        }
        Name category = collect.get(0).getName();
        return new ClassName(category.toString());
    }

}
