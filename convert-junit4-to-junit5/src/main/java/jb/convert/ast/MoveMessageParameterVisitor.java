package jb.convert.ast;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MoveMessageParameterVisitor extends VoidVisitorAdapter<Object> {

	private static final Set<String> ONE_PARAM_METHODS = new HashSet<>(
			Arrays.asList("assertFalse", "assertTrue", "assertNull", "assertNotNull", "assumeTrue", "assumeFalse",
					"assumeNoException", "assumeNull", "assumeNotNull"));

	private static final Set<String> TWO_PARAM_METHODS = new HashSet<>(
			Arrays.asList("assertEquals", "assertArrayEquals", "assertNotEquals", "assertNotSame", "assertSame"));

	private static final Set<String> THREE_PARAM_METHODS = new HashSet<>(Arrays.asList("assertEquals"));

	private boolean updated = false;

    public boolean performedUpdate() {
        return updated;
    }

	@Override
	public void visit(final MethodCallExpr n, final Object arg) {
		String methodName = n.getNameAsString();
        if (needsUpdating(n, methodName)) {
			Expression message;
            message = n.getArgument(0);
			n.remove(message);
			n.addArgument(message);
			updated = true;
		}
		super.visit(n, arg);
	}

	private boolean needsUpdating(MethodCallExpr methodCall, String methodName) {
        int numParams = methodCall.getArguments().size();

        return (numParams == 2 && ONE_PARAM_METHODS.contains(methodName))
				|| (numParams == 3 && TWO_PARAM_METHODS.contains(methodName))
				|| (numParams == 4 && THREE_PARAM_METHODS.contains(methodName));
	}
}
