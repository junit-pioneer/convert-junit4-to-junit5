package jb.convert.ast;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;

import static com.github.javaparser.JavaParser.parseImport;

public class AssertThatMigration extends ModifierVisitor<Void> {

    private boolean updated = false;

    public boolean performedUpdate() {
        return updated;
    }

    @Override
    public Visitable visit(MethodCallExpr methodCall, Void arg) {
        Visitable visit = super.visit(methodCall, arg);
        if ("assertThat".equals(methodCall.getNameAsString())) {
            NodeList<ImportDeclaration> imports = ImportDeclarations.imports(methodCall);
            if (imports.contains(parseImport("import static org.junit.Assert.*;"))
                    || imports.contains(parseImport("import static org.junit.jupiter.api.Assert.*;")) // todo this should be removed once we move away from search and replace
                    || imports.contains(parseImport("import static org.junit.jupiter.api.Assertions.*;")) // todo this should be removed once we move away from search and replace
            ) {
                updated = true;
                imports.add(0, parseImport("import static org.hamcrest.MatcherAssert.assertThat;"));
            }
        }
        return visit;
    }
}
