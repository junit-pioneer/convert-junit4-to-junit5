package jb.configuration;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public interface JavaParserAdapter {
    CompilationUnit parse(String source);

    String print(Node compilationUnit);
}
