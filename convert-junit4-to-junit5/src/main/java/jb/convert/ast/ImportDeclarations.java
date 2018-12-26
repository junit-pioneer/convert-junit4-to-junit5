package jb.convert.ast;

import com.github.javaparser.HasParentNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;

import static com.github.javaparser.JavaParser.parseImport;

class ImportDeclarations {

    static NodeList<ImportDeclaration> imports(HasParentNode<?> n) {
        return n.findAncestor(CompilationUnit.class).map(CompilationUnit::getImports).orElseGet(NodeList::new);
    }

    static void replace(ImportDeclaration importDeclaration, Class<?> junit4Import, Class<?> replacementInJunit5) {
        if (importDeclaration.getName().toString().equals(junit4Import.getCanonicalName())) {
            ImportDeclaration replacement = parseImport("import " + replacementInJunit5.getCanonicalName() + ";");
            importDeclaration.setName(replacement.getName());
        }
    }

    static void addImportTo(HasParentNode<?> node, Class<?> clazz) {
        addImport(node, parseImport("import " + clazz.getCanonicalName() + ";"));
    }

    static void addStaticImportTo(HasParentNode<?> target, String importable) {
        ImportDeclaration staticImport = parseImport("import static " + importable + ";");
        addImport(target, staticImport);
    }

    private static void addImport(HasParentNode<?> n, ImportDeclaration importDeclaration) {
        n.findAncestor(CompilationUnit.class).ifPresent(cu -> {
            cu.addImport(importDeclaration.getNameAsString(), importDeclaration.isStatic(), importDeclaration.isAsterisk());
        });
    }
}
