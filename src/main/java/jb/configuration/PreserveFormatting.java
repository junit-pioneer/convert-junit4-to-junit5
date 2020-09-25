package jb.configuration;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

public class PreserveFormatting implements JavaParserAdapter {
    @Override
    public CompilationUnit parse(String source) {
        StaticJavaParser.setConfiguration(new ParserConfiguration().setLexicalPreservationEnabled(true));
        return StaticJavaParser.parse(source);
    }

    @Override
    public String print(Node compilationUnit) {
        return LexicalPreservingPrinter.print(compilationUnit);
    }
}
