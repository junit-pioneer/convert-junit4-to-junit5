package jb.configuration;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;

import static jb.configuration.JavaParserHelper.extractResultFrom;

public class PrettyPrint implements JavaParserAdapter {
    private final JavaParser javaParser = new JavaParser(new ParserConfiguration());
    @Override
    public CompilationUnit parse(String source) {
        return extractResultFrom(this.javaParser.parse(source));
    }

    @Override
    public String print(Node compilationUnit) {
        return compilationUnit.toString();
    }
}
