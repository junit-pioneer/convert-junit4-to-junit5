package jb.convert.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import jb.ProjectRecorder;
import jb.convert.ast.tools.ClassName;
import jb.convert.ast.tools.ImportDeclarations;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class CategoryConversion extends ModifierVisitor<Void> implements Conversion {
    private static final ImportDeclaration categoryImport = new ImportDeclaration("org.junit.experimental.categories.Category", false, false);
    private final ProjectRecorder projectRecorder;
    private boolean updated = false;

    @Override
    public boolean convert(CompilationUnit cu) {
        visit(cu, null);
        return updated;
    }

    public CategoryConversion(ProjectRecorder projectRecorder) {
        this.projectRecorder = projectRecorder;
    }

    @Override
    public Node visit(ImportDeclaration importDeclaration, Void arg) {
        if (importDeclaration.equals(categoryImport)) {
            updated = true;
            return null;
        }
        return importDeclaration;
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
        if (collect.size() != 1) {
            throw new IllegalStateException("unable to resolve category class to single import, instead found " + collect.size());
        }
        Name category = collect.get(0).getName();
        return new ClassName(category.toString());
    }

    @Override
    public String toString() {
        return "updated: " + updated;
    }

}
