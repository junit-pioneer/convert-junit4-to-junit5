package jb.convert.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.lang.annotation.Annotation;

public class ReduceToDefaultScopeConversion extends GenericVisitorAdapter<Void, ReduceToDefaultScopeConversion.Accumulator> implements Conversion {

    static class Accumulator {
        boolean containsTests = false;
    }

    private boolean updated = false;

    @Override
    public boolean convert(CompilationUnit cu) {
        visit(cu, new ReduceToDefaultScopeConversion.Accumulator());
        return updated;
    }

    @Override
    public Void visit(ClassOrInterfaceDeclaration n, Accumulator arg) {
        Void visit = super.visit(n, arg);
        if (arg.containsTests && !n.isNestedType()) {
            n.removeModifier(Modifier.Keyword.PUBLIC);
            updated();
        }
        return visit;
    }

    @Override
    public Void visit(MethodDeclaration method, Accumulator arg) {
        Void visit = super.visit(method, arg);
        method.getAnnotationByClass(org.junit.jupiter.api.Test.class).ifPresent(__ -> arg.containsTests = true);
        reduceToDefaultScopeIfAnnotationIsPresent(org.junit.jupiter.api.Test.class, method);
        reduceToDefaultScopeIfAnnotationIsPresent(BeforeAll.class, method);
        reduceToDefaultScopeIfAnnotationIsPresent(BeforeEach.class, method);
        reduceToDefaultScopeIfAnnotationIsPresent(AfterEach.class, method);
        reduceToDefaultScopeIfAnnotationIsPresent(AfterAll.class, method);
        return visit;
    }

    private void reduceToDefaultScopeIfAnnotationIsPresent(Class<? extends Annotation> annotationClass, MethodDeclaration n) {
        n.getAnnotationByClass(annotationClass).ifPresent(doNotCare -> {
            n.removeModifier(Modifier.Keyword.PUBLIC);
            updated();
        });
    }

    private void updated() {
        updated = true;
    }

    @Override
    public String toString() {
        return "updated: " + updated;
    }

}
