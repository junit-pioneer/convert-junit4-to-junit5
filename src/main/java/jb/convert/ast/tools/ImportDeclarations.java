package jb.convert.ast.tools;

import com.github.javaparser.HasParentNode;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Name;

import static com.github.javaparser.JavaParser.parseImport;

public class ImportDeclarations {

    public static NodeList<ImportDeclaration> imports(HasParentNode<?> n) {
        return n.findAncestor(CompilationUnit.class).map(CompilationUnit::getImports).orElseGet(NodeList::new);
    }

    public static void replace(ImportDeclaration toUpdate, StaticImportBuilder bluePrint, StaticImportBuilder replacement, Callback callback) {
        replace(toUpdate, importDeclarationFor(bluePrint), importDeclarationFor(replacement), callback);
    }

    public static void replace(ImportDeclaration toUpdate, Class<?> bluePrint, Class<?> replacement, Callback callback) {
        replace(toUpdate, importDeclarationFor(bluePrint), importDeclarationFor(replacement), callback);
    }

    public static void replace(ImportDeclaration toUpdate, ImportDeclaration bluePrint, ImportDeclaration replacement, Callback callback) {
        if (toUpdate.equals(bluePrint)) {
            toUpdate.setName(replacement.getName());
            callback.call();
        }
    }

    public static void addImportTo(HasParentNode<?> node, Class<?> clazz) {
        addImportTo(node, importDeclarationFor(clazz));
    }

    public static void addStaticImportTo(HasParentNode<?> target, String importable) {
        ImportDeclaration staticImport = parseImport("import static " + importable + ";");
        addImportTo(target, staticImport);
    }

    public static void addImportTo(HasParentNode<?> n, ImportDeclaration importDeclaration) {
        n.findAncestor(CompilationUnit.class).ifPresent(cu -> {
            cu.addImport(importDeclaration.getNameAsString(), importDeclaration.isStatic(), importDeclaration.isAsterisk());
        });
    }

    public static ImportDeclaration importDeclarationFor(Class<?> replacementInJunit5) {
        Name name = Names.createNameFor(replacementInJunit5);
        return new ImportDeclaration(name, false, false);
    }

    public static ImportDeclaration importDeclarationFor(StaticImportBuilder bluePrint) {
        StaticImport build = bluePrint.build();
        return parseImport("import static " + build.className + "." + build.method + ";");
    }
}
