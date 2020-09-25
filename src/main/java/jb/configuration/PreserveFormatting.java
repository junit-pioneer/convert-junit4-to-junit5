package jb.configuration;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

public class PreserveFormatting implements JavaParserAdapter {
    private final JavaParser javaParser = new JavaParser(new ParserConfiguration().setLexicalPreservationEnabled(true));
    @Override
    public CompilationUnit parse(String source) {
        return extractResultFrom(javaParser.parse(source));
    }

    @Override
    public String print(Node compilationUnit) {
        return LexicalPreservingPrinter.print(compilationUnit);
    }

    private static <T extends Node> T extractResultFrom(ParseResult<T> result) {
        if (result.isSuccessful()) {
            return result.getResult().get();
        }
        throw new ParseProblemException(result.getProblems());
    }

}
