package jb.convert.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import jb.convert.ast.tools.Annotations;
import jb.convert.ast.tools.ImportDeclarations;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;

public class SetupMethodConversion extends ModifierVisitor<Void> implements Conversion {
    private boolean updated = false;

    @Override
    public boolean convert(CompilationUnit cu) {
        visit(cu, null);
        return updated;
    }

    @Override
    public Node visit(ImportDeclaration n, Void arg) {
        ImportDeclarations.replace(n, BeforeClass.class, BeforeAll.class, this::updated);
        ImportDeclarations.replace(n, Before.class, BeforeEach.class, this::updated);
        ImportDeclarations.replace(n, After.class, AfterEach.class, this::updated);
        ImportDeclarations.replace(n, AfterClass.class, AfterAll.class, this::updated);
        ImportDeclarations.replace(n, Ignore.class, Disabled.class, this::updated);
        return n;
    }

    @Override
    public Visitable visit(MethodDeclaration n, Void arg) {
        Annotations.replace(n, BeforeClass.class, BeforeAll.class, this::updated);
        Annotations.replace(n, Before.class, BeforeEach.class, this::updated);
        Annotations.replace(n, After.class, AfterEach.class, this::updated);
        Annotations.replace(n, AfterClass.class, AfterAll.class, this::updated);
        Annotations.replace(n, AfterClass.class, AfterAll.class, this::updated);
        Annotations.replace(n, Ignore.class, Disabled.class, this::updated);
        return n;
    }

    private void updated() {
        this.updated = true;
    }

    @Override
    public String toString() {
        return "updated: " + updated;
    }

}
