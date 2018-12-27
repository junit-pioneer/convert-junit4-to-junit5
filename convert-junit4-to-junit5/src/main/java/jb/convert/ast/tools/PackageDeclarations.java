package jb.convert.ast.tools;

import com.github.javaparser.HasParentNode;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

public class PackageDeclarations {

    public static String packageNameFor(HasParentNode<?> n) {
        return n.findAncestor(CompilationUnit.class)
                .map(cu -> cu.getPackageDeclaration().map(NodeWithName::getNameAsString)
                        .orElse(""))
                .orElse("");
    }
}
