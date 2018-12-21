package jb.configuration;

import com.github.javaparser.ast.CompilationUnit;

public interface JavaParserAdapter {
    CompilationUnit parse(String source);

    String print(CompilationUnit compilationUnit);
}
