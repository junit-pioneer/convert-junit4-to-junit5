package jb.configuration;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import static jb.configuration.JavaParserHelper.extractResultFrom;

public class PreserveFormatting implements JavaParserAdapter {
    private final JavaParser javaParser = new JavaParser(new ParserConfiguration().setLexicalPreservationEnabled(true));

    public PreserveFormatting() {
        StaticJavaParser.setConfiguration(new ParserConfiguration().setLexicalPreservationEnabled(true));
    }

    @Override
    public CompilationUnit parse(String source) {
        return extractResultFrom(javaParser.parse(source));
    }

    @Override
    public String print(Node compilationUnit) {
        return LexicalPreservingPrinter.print(compilationUnit);
    }

}
