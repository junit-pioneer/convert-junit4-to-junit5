package jb.convert.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import jb.convert.ast.tools.ImportDeclarations;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

class ImportDeclarationsTest {

    @Test
    void doNotAddDuplicateImports() {
        CompilationUnit cu = new CompilationUnit("org");
        ClassOrInterfaceDeclaration type = cu.addClass("Type");
        ImportDeclarations.addStaticImportTo(type, "org.example.method");
        ImportDeclarations.addStaticImportTo(type, "org.example.method");

        assertThat(cu.getImports(), hasSize(1));
    }
}