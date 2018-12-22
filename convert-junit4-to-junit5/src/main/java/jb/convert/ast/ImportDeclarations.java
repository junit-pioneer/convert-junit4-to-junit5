package jb.convert.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;

class ImportDeclarations {
    static void replace(ImportDeclaration n, Class<?> junit4Import, Class<?> replacementInJunit5){
        if( n.getName().toString().equals(junit4Import.getCanonicalName()) ){
            ImportDeclaration node = JavaParser.parseImport("import " + replacementInJunit5.getCanonicalName() + ";");
            n.setName(node.getName());
        }
    }
}
