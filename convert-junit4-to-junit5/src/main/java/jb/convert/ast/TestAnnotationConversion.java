package jb.convert.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import jb.convert.ast.tools.ImportDeclarations;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.time.Duration;
import java.util.Optional;

public class TestAnnotationConversion extends ModifierVisitor<Void> {
    private boolean updated = false;

    public boolean performedUpdate() {
        return updated;
    }

    @Override
    public Node visit(ImportDeclaration n, Void arg) {
        ImportDeclarations.replace(n, Test.class, org.junit.jupiter.api.Test.class, this::updated);
        return n;
    }

    @Override
    public Visitable visit(MethodDeclaration methodDeclaration, Void arg) {
        Optional<AnnotationExpr> annotationByClass = methodDeclaration.getAnnotationByClass(Test.class);
        annotationByClass.ifPresent(it -> {
            TestAnnotationDetails.Aggregator aggregator = new TestAnnotationDetails.Aggregator();
            new TestAnnotationDetails().visit(methodDeclaration.getAnnotations(), aggregator);
            if (null != aggregator.exceptionClass) {
                wrapBodyInAssertThrows(methodDeclaration, aggregator.exceptionClass.toString());
            }
            if (null != aggregator.timeout) {
                wrapBodyInAssertTimeout(methodDeclaration, aggregator.timeout);
            }
        });
        return methodDeclaration;
    }

    private void updated(){
        updated = true;
    }

    private void wrapBodyInAssertTimeout(MethodDeclaration methodDeclaration, Long timeoutInMillis) {
        methodDeclaration.getBody().ifPresent(body -> {
            if (body.getStatements().isEmpty()) {
                return;
            }
            String junit4TestMethodBody = body.toString();

            String importable = Assertions.class.getCanonicalName() + ".assertTimeout";


            ImportDeclarations.addImportTo(methodDeclaration, Duration.class);
            ImportDeclarations.addStaticImportTo(methodDeclaration, importable);

            Statement statement = JavaParser.parseStatement("assertTimeout(Duration.ofMillis(" + timeoutInMillis + "L) ,()->" +
                    "" + junit4TestMethodBody +
                    ");\n");
            NodeList<Statement> statements = new NodeList<>();
            statements.add(statement);
            body.setStatements(statements);
            updated();
        });
    }

    private void wrapBodyInAssertThrows(MethodDeclaration methodDeclaration, String exceptionClassAsString) {
        methodDeclaration.getBody().ifPresent(body -> {
            if (body.getStatements().isEmpty()) {
                return;
            }
            ImportDeclarations.addStaticImportTo(methodDeclaration, Assertions.class.getCanonicalName() + ".assertThrows");
            Statement statement = JavaParser.parseStatement("assertThrows(" + exceptionClassAsString + ",()->" +
                    "" + body.toString() +
                    ");\n");
            NodeList<Statement> statements = new NodeList<>();
            statements.add(statement);
            body.setStatements(statements);
            updated();
        });
    }

    public static class TestAnnotationDetails extends ModifierVisitor<TestAnnotationDetails.Aggregator> {
        static class Aggregator {
            Long timeout;
            ClassExpr exceptionClass;
        }

        @Override
        public Visitable visit(NormalAnnotationExpr n, Aggregator arg) {
            super.visit(n, arg);
            return new MarkerAnnotationExpr("Test");
        }

        @Override
        public Visitable visit(MemberValuePair n, Aggregator arg) {
            String propertyName = n.getName().toString();
            Expression value = n.getValue();
            if ("expected".equals(propertyName)) {
                arg.exceptionClass = (ClassExpr) value;
                return null;
            }
            if ("timeout".equals(propertyName)) {
                if (value instanceof IntegerLiteralExpr) {
                    IntegerLiteralExpr integerLiteralExpr = (IntegerLiteralExpr) value;
                    arg.timeout = (long) integerLiteralExpr.asInt();
                } else if (value instanceof LongLiteralExpr) {
                    LongLiteralExpr longLiteral = (LongLiteralExpr) value;
                    arg.timeout = longLiteral.asLong();
                } else {
                    throw new RuntimeException("unexpected " + value.getClass());
                }
                return null;
            }
            throw new RuntimeException("you should handle this one");
        }
    }
}
