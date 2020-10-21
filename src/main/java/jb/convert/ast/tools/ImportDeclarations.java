package jb.convert.ast.tools;

import com.github.javaparser.HasParentNode;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Name;

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

    public static void addStaticImportTo(HasParentNode<?> target, StaticImportBuilder toAdd) {
        addImportTo(target, importDeclarationFor(toAdd));
    }

    public static void addImportTo(HasParentNode<?> n, ImportDeclaration importDeclaration) {
        n.findAncestor(CompilationUnit.class).ifPresent(cu -> {
            cu.addImport(importDeclaration);
        });
    }

    public static ImportDeclaration importDeclarationFor(Class<?> replacementInJunit5) {
        Name name = Names.createNameFor(replacementInJunit5);
        return new ImportDeclaration(name, false, false);
    }

    public static ImportDeclaration importDeclarationFor(StaticImportBuilder bluePrint) {
        StaticImport build = bluePrint.build();
        // in case it is a star import this information is passed in a flag and not in the name
        String method = build.isStarImport() ? "" : "." + build.method;
        Name name = Names.createNameFor(build.className.string + method);
        return new ImportDeclaration(name, true, build.isStarImport());
    }

}
