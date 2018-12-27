package jb.convert.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jb.convert.ast.tools.ImportDeclarations;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static jb.convert.ast.tools.ImportDeclarations.importDeclarationFor;
import static jb.convert.ast.tools.StaticImportBuilder.staticImportFrom;

public class AssertMigration extends VoidVisitorAdapter<Object> {

    private static final String assertEquals = "assertEquals";

    private static final Set<String> ONE_PARAM_METHODS = new HashSet<>(
            Arrays.asList("assertFalse", "assertTrue", "assertNull", "assertNotNull", "assumeTrue", "assumeFalse",
                    "assumeNoException", "assumeNull", "assumeNotNull"));

    private static final Set<String> TWO_PARAM_METHODS = new HashSet<>(
            Arrays.asList(assertEquals, "assertArrayEquals", "assertNotEquals", "assertNotSame", "assertSame"));

    private static final Set<String> THREE_PARAM_METHODS = new HashSet<>(Collections.singletonList(assertEquals));

    private boolean updated = false;

    public boolean performedUpdate() {
        return updated;
    }

    @Override
    public void visit(ImportDeclaration n, Object arg) {
        super.visit(n, arg);
        ImportDeclarations.replace(n, Assert.class, Assertions.class, this::updated);
        ImportDeclarations.replace(n, staticImportFrom(Assert.class).star(), staticImportFrom(Assertions.class).star(), this::updated);
        // TODO remove the next one once search and replace is gone
        ImportDeclarations.replace(n, JavaParser.parseImport("import org.junit.jupiter.api.Assert;"), importDeclarationFor(Assertions.class), this::updated);
        ImportDeclarations.replace(n, JavaParser.parseImport("import static org.junit.jupiter.api.Assert.*;"), importDeclarationFor(staticImportFrom(Assertions.class).star()), this::updated);
    }

    @Override
    public void visit(final MethodCallExpr methodCall, final Object arg) {
        String methodName = methodCall.getNameAsString();
        if (scopeMatchesAssert(methodCall) && Stream.of("assert", "fail").anyMatch(methodName::startsWith)){
            methodCall.getScope().ifPresent(scope -> {
                scope.ifNameExpr(name -> name.setName("Assertions"));
                scope.ifFieldAccessExpr( fieldAccessExpr -> fieldAccessExpr.setName("Assertions"));
            });
            updated = true;
        }
        if (failMessageNeedsUpdating(methodCall, methodName)) {
            if (assertEquals.equals(methodName)) {
                migrateAssertEquals(methodCall);
            } else {
                moveMessageArgumentToTheEnd(methodCall);
            }
        }
        super.visit(methodCall, arg);
    }

    private boolean scopeMatchesAssert(MethodCallExpr methodCall) {
        String scopeAsString = methodCall.getScope().map(Node::toString).orElse("");
        // todo remove the org.junit.jupiter once search and replace is gone
        return Arrays.asList("Assert", "org.junit.Assert", "org.junit.jupiter.api.Assert").contains(scopeAsString);
    }

    private void migrateAssertEquals(MethodCallExpr methodCall) {
        NodeList<Expression> arguments = methodCall.getArguments();
        if (arguments.size() == 3) {
            Expression expression = arguments.get(2);
            if (expression instanceof DoubleLiteralExpr) {
                DoubleLiteralExpr deltaArgument = (DoubleLiteralExpr) expression;
                if (deltaArgument.asDouble() == 0.0) {
                    methodCall.remove(deltaArgument);
                    updated();
                    return;
                }
            }
        } else if (arguments.size() == 4) {
            throw new RuntimeException("this looks like a float/double assert with message and delta, not implemented yet");
        }

        moveMessageArgumentToTheEnd(methodCall);
    }

    private void moveMessageArgumentToTheEnd(MethodCallExpr methodCall) {
        Expression messageArgument = methodCall.getArgument(0);
        methodCall.remove(messageArgument);
        methodCall.addArgument(messageArgument);
        updated();
    }

    private boolean failMessageNeedsUpdating(MethodCallExpr methodCall, String methodName) {
        int numParams = methodCall.getArguments().size();
        return (numParams == 2 && ONE_PARAM_METHODS.contains(methodName))
                || (numParams == 3 && TWO_PARAM_METHODS.contains(methodName))
                || (numParams == 4 && THREE_PARAM_METHODS.contains(methodName));
    }

    private void updated(){
        this.updated = true;
    }

}
