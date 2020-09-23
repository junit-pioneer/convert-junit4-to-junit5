package jb.convert.ast;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import jb.convert.ast.tools.ImportDeclarations;
import jb.convert.ast.tools.StaticImportBuilder;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static jb.convert.ast.tools.ImportDeclarations.importDeclarationFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;

class ImportDeclarationsTest {

    @Test
    void doNotAddDuplicateImports() {
        CompilationUnit cu = new CompilationUnit("org");
        ClassOrInterfaceDeclaration type = cu.addClass("Type");
        ImportDeclarations.addStaticImportTo(type, "org.example.method");
        ImportDeclarations.addStaticImportTo(type, "org.example.method");

        assertThat(cu.getImports(), hasSize(1));
    }

    @Test
    void deriveProperImportDeclarationForStaticStarImport() {
        ImportDeclaration importDeclaration = importDeclarationFor(StaticImportBuilder.staticImportFrom(Tag.class).star());

        assertThat(importDeclaration.toString(), startsWith("import static org.junit.jupiter.api.Tag.*;"));
    }

    @Test
    void deriveProperImportDeclarationForStaticMethodImport() {
        ImportDeclaration importDeclaration = importDeclarationFor(StaticImportBuilder.staticImportFrom(Tag.class).method("banana"));

        assertThat(importDeclaration.toString(), startsWith("import static org.junit.jupiter.api.Tag.banana;"));
    }
}
