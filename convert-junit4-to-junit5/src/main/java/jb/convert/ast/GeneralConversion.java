package jb.convert.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.ModifierVisitor;

public class GeneralConversion extends ModifierVisitor<Void> implements Conversion {
    private final ImportDeclaration junitStar = JavaParser.parseImport("import org.junit.*;");
    private boolean updated;

    @Override
    public boolean convert(CompilationUnit cu) {
        visit(cu, null);
        return updated;
    }

    @Override
    public Node visit(ImportDeclaration importDeclaration, Void arg) {
        if (importDeclaration.equals(junitStar)) {
            updated();
            return JavaParser.parseImport("import org.junit.jupiter.api.*;");
        }
        return importDeclaration;
    }

    private void updated() {
        this.updated = true;
    }

    @Override
    public String toString() {
        return "updated: " + updated;
    }

}
