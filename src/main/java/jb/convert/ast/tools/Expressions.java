package jb.convert.ast.tools;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;

public class Expressions {

    public static Expression fieldAccessExpressionFor(String input) {
        String[] parts = input.split("\\.");
        Expression current = null;
        for (String part : parts) {
            if (current == null) {
                current = new NameExpr(part);
                continue;
            }
            current = new FieldAccessExpr(current, part);
        }
        return current;
    }
}
