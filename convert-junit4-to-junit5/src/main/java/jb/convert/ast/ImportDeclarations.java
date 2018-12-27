package jb.convert.ast;

import com.github.javaparser.HasParentNode;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;

import static com.github.javaparser.JavaParser.parse;
import static com.github.javaparser.JavaParser.parseImport;

class ImportDeclarations {

    static NodeList<ImportDeclaration> imports(HasParentNode<?> n) {
        return n.findAncestor(CompilationUnit.class).map(CompilationUnit::getImports).orElseGet(NodeList::new);
    }

    static void replace(ImportDeclaration importDeclaration, Class<?> junit4Import, Class<?> replacementInJunit5) {
        if (importDeclaration.getName().toString().equals(junit4Import.getCanonicalName())) {
            importDeclaration.setName(importDeclarationFor(replacementInJunit5).getName());
        }
    }

    public static void replace(ImportDeclaration toUpdate, StaticImportBuilder bluePrint, StaticImportBuilder replacement, Callback callback) {
        replace(toUpdate, importDeclarationFor(bluePrint), importDeclarationFor(replacement), callback);
    }

    static void replace(ImportDeclaration toUpdate, Class<?> bluePrint, Class<?> replacement, Callback callback) {
        replace(toUpdate, importDeclarationFor(bluePrint), importDeclarationFor(replacement), callback);
    }

    static void replace(ImportDeclaration toUpdate, ImportDeclaration bluePrint, ImportDeclaration replacement, Callback callback) {
        if (toUpdate.equals(bluePrint)) {
            toUpdate.setName(replacement.getName());
            callback.call();
        }
    }

    static void addImportTo(HasParentNode<?> node, Class<?> clazz) {
        addImport(node, importDeclarationFor(clazz));
    }

    static void addStaticImportTo(HasParentNode<?> target, String importable) {
        ImportDeclaration staticImport = parseImport("import static " + importable + ";");
        addImport(target, staticImport);
    }

    public static ImportDeclaration importDeclarationFor(Class<?> replacementInJunit5) {
        return parseImport("import " + replacementInJunit5.getCanonicalName() + ";");
    }

    public static ImportDeclaration importDeclarationFor(StaticImportBuilder bluePrint) {
        StaticImport build = bluePrint.build();
        return parseImport("import static " + build.className + "." + build.method+";");
    }

    private static void addImport(HasParentNode<?> n, ImportDeclaration importDeclaration) {
        n.findAncestor(CompilationUnit.class).ifPresent(cu -> {
            cu.addImport(importDeclaration.getNameAsString(), importDeclaration.isStatic(), importDeclaration.isAsterisk());
        });
    }
}
