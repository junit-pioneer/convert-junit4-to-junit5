package jb;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoveMessageParameterVisitor extends VoidVisitorAdapter<Object> {

	private static final Set<String> ONE_PARAM_METHODS = new HashSet<>(
			Arrays.asList("assertFalse", "assertTrue", "assertNull", "assertNotNull", "assumeTrue", "assumeFalse",
					"assumeNoException", "assumeNull", "assumeNotNull"));

	private static final Set<String> TWO_PARAM_METHODS = new HashSet<>(
			Arrays.asList("assertEquals", "assertArrayEquals", "assertNotEquals", "assertNotSame", "assertSame"));

	private static final Set<String> THREE_PARAM_METHODS = new HashSet<>(Arrays.asList("assertEquals"));
	
	private boolean updated = false;

	@Override
	public void visit(final MethodCallExpr n, final Object arg) {

		String methodName = n.getNameAsString();
		List<Node> children = n.getChildNodes();
		if (needsUpdating(methodName, children)) {
			Expression message = (Expression) getMessageParam(methodName, children);
			n.remove(message);
			n.addArgument(message);
			updated = true;
		}
		super.visit(n, arg);
	}
	
	boolean performedUpdate() {
		return updated;
	}

	private boolean needsUpdating(String methodName, List<Node> children) {
		int numParams = getNumberParameters(methodName, children);

		return (numParams == 2 && ONE_PARAM_METHODS.contains(methodName))
				|| (numParams == 3 && TWO_PARAM_METHODS.contains(methodName))
				|| (numParams == 4 && THREE_PARAM_METHODS.contains(methodName));
	}

	private Node getMessageParam(String methodName, List<Node> children) {

		boolean foundMethodName = false;
		for (Node node : children) {
			if (foundMethodName) {
				return node;
			}
			if (methodName.equals(node.toString())) {
				foundMethodName = true;
			}
		}
		throw new RuntimeException("Message parameter for " + methodName + " should be found in " + children);
	}

	// child nodes could be [assertTrue, a, b] or [Assertions, assertTrue, a, b]
	// list is the method name and parameters
	private int getNumberParameters(String methodName, List<Node> children) {
		int numPositionsBeforeParams = 0;
		for (Node node : children) {
			numPositionsBeforeParams++;
			if (methodName.equals(node.toString())) {
				return children.size() - numPositionsBeforeParams;
			}
		}
		throw new RuntimeException("Method name " + methodName + " should be found in " + children);
	}

}
