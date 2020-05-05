package jb.convert.ast;

import com.github.javaparser.ast.CompilationUnit;

public interface Conversion {
    /**
     * Try to apply a specific Conversion on a CompilationUnit.
     *
     * @param cu the compilation unit as parsed from javaparser
     * @return true if the conversation changed code, false otherwise
     */
    boolean convert(CompilationUnit cu);
}
