package jb.convert.ast;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import jb.convert.MatchDetector;
import jb.convert.ast.tools.ImportDeclarations;
import org.junit.Assume;
import org.junit.jupiter.api.Assumptions;

import java.util.Arrays;
import java.util.Set;

import static jb.convert.ast.tools.StaticImportBuilder.staticImportFrom;

public class AssumeConversion extends VoidVisitorAdapter<Object> {

    private static final MatchDetector matchDetector = new MatchDetector();
    private static final Set<String> convertibleAssumeMethods = matchDetector.publicStaticMethodsWithMatchingNames(Assume.class, Assumptions.class);
    private boolean updated = false;

    public boolean performedUpdate() {
        return updated;
    }

    @Override
    public void visit(ImportDeclaration n, Object arg) {
        super.visit(n, arg);
        ImportDeclarations.replace(n, Assume.class, Assumptions.class, this::updated);
        ImportDeclarations.replace(n, staticImportFrom(Assume.class).star(), staticImportFrom(Assumptions.class).star(), this::updated);
        convertibleAssumeMethods.forEach(methodName -> {
            ImportDeclarations.replace(n, staticImportFrom(Assume.class).method(methodName), staticImportFrom(Assumptions.class).method(methodName), this::updated);
        });
    }

    @Override
    public void visit(final MethodCallExpr methodCall, final Object arg) {
        String methodName = methodCall.getNameAsString();
        if (convertibleAssumeMethods.contains(methodName)) {
            if (scopeMatchesAssert(methodCall)) {
                methodCall.getScope().ifPresent(scope -> {
                    scope.ifNameExpr(name -> name.setName("Assumptions"));
                    scope.ifFieldAccessExpr(fieldAccessExpr -> fieldAccessExpr.setName("Assumptions"));
                    updated();
                });
            }
            int argumentCount = methodCall.getArguments().size();
            if (argumentCount == 2) {
                moveMessageArgumentToTheEnd(methodCall);
            } else if (argumentCount > 2) {
                throw new RuntimeException("Not supported yet");
            }
        }
        super.visit(methodCall, arg);
    }

    private boolean scopeMatchesAssert(MethodCallExpr methodCall) {
        String scopeAsString = methodCall.getScope().map(Node::toString).orElse("");
        return Arrays.asList("Assume", "org.junit.Assume").contains(scopeAsString);
    }

    private void moveMessageArgumentToTheEnd(MethodCallExpr methodCall) {
        Expression messageArgument = methodCall.getArgument(0);
        methodCall.remove(messageArgument);
        methodCall.addArgument(messageArgument);
        updated();
    }

    private void updated() {
        this.updated = true;
    }

}
