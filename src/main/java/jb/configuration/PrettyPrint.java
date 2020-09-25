package jb.configuration;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

public class PrettyPrint implements JavaParserAdapter {
    @Override
    public CompilationUnit parse(String source) {
        return StaticJavaParser.parse(source);
    }

    @Override
    public String print(Node compilationUnit) {
        return compilationUnit.toString();
    }
}
