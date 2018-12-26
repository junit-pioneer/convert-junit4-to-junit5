package jb.convert.ast;

import com.github.javaparser.HasParentNode;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

class PackageDeclarations {

    static String packageNameFor(HasParentNode<?> n) {
        return n.findAncestor(CompilationUnit.class)
                .map(cu -> cu.getPackageDeclaration().map(NodeWithName::getNameAsString)
                        .orElse(""))
                .orElse("");
    }
}
