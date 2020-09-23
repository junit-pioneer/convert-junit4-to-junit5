package jb.convert.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import jb.convert.ast.tools.Expressions;
import jb.convert.ast.tools.ImportDeclarations;

import static jb.convert.ast.tools.ImportDeclarations.addImportTo;

public class AssertThatConversion extends ModifierVisitor<Void> implements Conversion {

    private static final ImportDeclaration junitAssertThat = JavaParser.parseImport("import static org.junit.Assert.assertThat;");
    private boolean updated = false;


    @Override
    public boolean convert(CompilationUnit cu) {
        visit(cu, null);
        return updated;
    }

    @Override
    public Node visit(ImportDeclaration importDeclaration, Void arg) {
        if (importDeclaration.equals(junitAssertThat)) {
            updated();
            return assertThatFromMatcherAssert();
        }
        return importDeclaration;
    }

    @Override
    public Visitable visit(MethodCallExpr methodCall, Void arg) {
        Visitable visit = super.visit(methodCall, arg);
        if ("assertThat".equals(methodCall.getNameAsString()) && hasTwoOrThreeArguments(methodCall)) {
            methodCall.getScope().ifPresent(scope -> {
                scope.ifFieldAccessExpr(fieldAccessExpr -> fieldAccessExpr.replace(Expressions.fieldAccessExpressionFor("org.hamcrest.MatcherAssert")));
            });
            NodeList<ImportDeclaration> imports = ImportDeclarations.imports(methodCall);
            ImportDeclaration search = new ImportDeclaration("org.junit.Assert", true, true);
            if (imports.contains(search)) {
                updated();
                addImportTo(methodCall, assertThatFromMatcherAssert());
            }
        }
        return visit;
    }

    private boolean hasTwoOrThreeArguments(MethodCallExpr methodCall) {
        int argumentCount = methodCall.getArguments().size();
        return argumentCount == 2 || argumentCount == 3;
    }

    private void updated() {
        updated = true;
    }

    private ImportDeclaration assertThatFromMatcherAssert() {
        return new ImportDeclaration("org.hamcrest.MatcherAssert.assertThat", true, false);
    }

    @Override
    public String toString() {
        return "updated: " + updated;
    }
}
