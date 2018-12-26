package jb.convert.ast;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MoveMessageParameterVisitor extends VoidVisitorAdapter<Object> {

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
    public void visit(final MethodCallExpr methodCall, final Object arg) {
        String methodName = methodCall.getNameAsString();
        if (failMessageNeedsUpdating(methodCall, methodName)) {
            if (assertEquals.equals(methodName)) {
                migrateAssertEquals(methodCall);
            } else {
                moveMessageArgumentToTheEnd(methodCall);
            }
        }
        super.visit(methodCall, arg);
    }

    private void migrateAssertEquals(MethodCallExpr methodCall) {
        NodeList<Expression> arguments = methodCall.getArguments();
        if (arguments.size() == 3) {
            Expression expression = arguments.get(2);
            if (expression instanceof DoubleLiteralExpr) {
                DoubleLiteralExpr deltaArgument = (DoubleLiteralExpr) expression;
                if (deltaArgument.asDouble() == 0.0) {
                    methodCall.remove(deltaArgument);
                    updated = true;
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
        updated = true;
    }

    private boolean failMessageNeedsUpdating(MethodCallExpr methodCall, String methodName) {
        int numParams = methodCall.getArguments().size();
        return (numParams == 2 && ONE_PARAM_METHODS.contains(methodName))
                || (numParams == 3 && TWO_PARAM_METHODS.contains(methodName))
                || (numParams == 4 && THREE_PARAM_METHODS.contains(methodName));
    }
}
