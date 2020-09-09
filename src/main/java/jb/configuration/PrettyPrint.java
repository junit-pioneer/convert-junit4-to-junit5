package jb.configuration;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public class PrettyPrint implements JavaParserAdapter {
    @Override
    public CompilationUnit parse(String source) {
        return JavaParser.parse(source);
    }

    @Override
    public String print(Node compilationUnit) {
        return compilationUnit.toString();
    }
}
