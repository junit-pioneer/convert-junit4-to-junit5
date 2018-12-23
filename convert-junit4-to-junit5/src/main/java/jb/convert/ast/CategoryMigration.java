package jb.convert.ast;

import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

public class CategoryMigration extends ModifierVisitor<Void> {
    private boolean updated = false;

    public boolean performedUpdate() {
        return updated;
    }

    @Override
    public Visitable visit(SingleMemberAnnotationExpr n, Void arg) {
        super.visit(n, arg);
        if ("Category".equals(n.getNameAsString())) {
            updated = true;
            ClassOrInterfaceType type = (ClassOrInterfaceType) ((ClassExpr) n.getMemberValue()).getType();
            SimpleName simpleName = type.getName();
            return new MarkerAnnotationExpr(new Name(simpleName.getIdentifier()));
        }
        return n;
    }

}
