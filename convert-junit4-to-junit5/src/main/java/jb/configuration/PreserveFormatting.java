package jb.configuration;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

public class PreserveFormatting implements JavaParserAdapter {
    @Override
    public CompilationUnit parse(String source) {
        CompilationUnit compilationUnit = JavaParser.parse(source);
        LexicalPreservingPrinter.setup(compilationUnit);
        return compilationUnit;
    }

    @Override
    public String print(Node compilationUnit) {
        return LexicalPreservingPrinter.print(compilationUnit);
    }
}
