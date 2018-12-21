package jb;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;

public class TestMethodMigration extends ModifierVisitor<Void> {
    private boolean updated = false;

    public boolean performedUpdate() {
        return updated;
    }

    @Override
    public Node visit(ImportDeclaration n, Void arg) {
        ImportDeclarations.replace(n, Test.class, org.junit.jupiter.api.Test.class);
        return n;
    }

    @Override
    public Visitable visit(MethodDeclaration methodDeclaration, Void arg) {
        Optional<AnnotationExpr> annotationByClass = methodDeclaration.getAnnotationByClass(Test.class);
        annotationByClass.ifPresent(it -> {
            TestAnnotationMigration.Aggregator aggregator = new TestAnnotationMigration.Aggregator();
            new TestAnnotationMigration().visit(methodDeclaration.getAnnotations(), aggregator);
            if (null != aggregator.exceptionClass) {
                methodDeclaration.getBody().ifPresent(body -> {
                    if (body.getStatements().isEmpty()) {
                        return;
                    }
                    String s = body.toString();

                    ImportDeclaration importDeclaration = JavaParser.parseImport("import static " + Assertions.class.getCanonicalName() + ".assertThrows;");
                    methodDeclaration.findAncestor(CompilationUnit.class).ifPresent(p -> p.addImport(importDeclaration));

                    Statement statement = JavaParser.parseStatement("assertThrows(" + aggregator.exceptionClass.toString() + ",()->" +
                            "" + s +
                            ");\n");
                    NodeList<Statement> statements = new NodeList<>();
                    statements.add(statement);
                    body.setStatements(statements);
                    updated = true;
                });
            }
        });
        return methodDeclaration;
    }

    public static class TestAnnotationMigration extends ModifierVisitor<TestAnnotationMigration.Aggregator> {
        static class Aggregator {
            ClassExpr exceptionClass;
        }

        @Override
        public Visitable visit(NormalAnnotationExpr n, Aggregator arg) {
            super.visit(n, arg);
            return new MarkerAnnotationExpr("Test");
        }

        @Override
        public Visitable visit(MemberValuePair n, Aggregator arg) {
            if ("expected".equals(n.getName().toString())) {
                arg.exceptionClass = (ClassExpr) n.getValue();
                return null;
            }
            throw new RuntimeException("you should handle this one");
        }
    }
}
