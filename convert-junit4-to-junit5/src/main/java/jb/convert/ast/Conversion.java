package jb.convert.ast;

import com.github.javaparser.ast.CompilationUnit;

public interface Conversion {
    boolean convert(CompilationUnit cu);
}
