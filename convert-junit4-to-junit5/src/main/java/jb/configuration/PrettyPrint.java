package jb.configuration;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

public class PrettyPrint implements JavaParserAdapter {
    @Override
    public CompilationUnit parse(String source) {
        return JavaParser.parse(source);
    }

    @Override
    public String print(CompilationUnit compilationUnit) {
        return compilationUnit.toString();
    }
}
