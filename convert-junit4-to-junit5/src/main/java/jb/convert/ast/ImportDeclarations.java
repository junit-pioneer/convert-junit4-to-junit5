package jb.convert.ast;

import com.github.javaparser.HasParentNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;

class ImportDeclarations {
    static void addImportTo(HasParentNode<?> n, Class<?> clazz) {
        n.findAncestor(CompilationUnit.class).ifPresent(cu -> {
            ImportDeclaration importDeclaration = JavaParser.parseImport("import " + clazz.getCanonicalName() + ";");
            cu.addImport(importDeclaration);
        });
    }

    static void replace(ImportDeclaration n, Class<?> junit4Import, Class<?> replacementInJunit5) {
        if (n.getName().toString().equals(junit4Import.getCanonicalName())) {
            ImportDeclaration node = JavaParser.parseImport("import " + replacementInJunit5.getCanonicalName() + ";");
            n.setName(node.getName());
        }
    }

    public static NodeList<ImportDeclaration> imports(HasParentNode<?> n) {
        return n.findAncestor(CompilationUnit.class).map(CompilationUnit::getImports).orElseGet(NodeList::new);
    }
}
