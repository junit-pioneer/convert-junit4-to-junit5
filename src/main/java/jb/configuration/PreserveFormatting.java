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
        CompilationUnit compilationUnit = StaticJavaParser.parse(source);
        LexicalPreservingPrinter.setup(compilationUnit);
        return compilationUnit;
    }

    @Override
    public String print(Node compilationUnit) {
        return LexicalPreservingPrinter.print(compilationUnit);
    }
}
