package jb;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import jb.convert.ConversionOutcome;
import jb.convert.ConversionResult;
import jb.convert.JunitConversionLogic;

import static jb.configuration.Configuration.prettyPrintAndPersistChanges;
import static org.junit.jupiter.api.Assertions.assertEquals;

class JunitConversionLogicFixture {
    private static final String importJunit4 = "import org.junit.Assert;";
    private static final String importJunit5 = "import org.junit.jupiter.api.Assertions;";

    static String prettyPrint(String string) {
        CompilationUnit cu = JavaParser.parse(string);
        return cu.toString();
    }

    // wrap in class so well formed for parser
    static void assertWrappingInClass(String code, String expected) {
        String prefix = "public class A { ";
        String postfix = " }";
        String codeWrapped = importJunit4 + prefix + code + postfix;
        String expectedWrapped = importJunit5 + prefix + expected + postfix;
        String actual = convertAndPrettyPrint(codeWrapped);
        assertEquals(prettyPrint(expectedWrapped), actual);
    }

    // wrap in class so well formed for parser
    static void assertUnchangedWrappingInClass(String code) {
        String prefix = "public class A { ";
        String postfix = " }";
        String codeWrapped = prefix + code + postfix;
        String actual = convertAndPrettyPrint(codeWrapped);
        assertEquals(prettyPrint(codeWrapped), actual);
    }

    // wrap in class/method so well formed for parser
    static void assertAfterWrappingInMethod(String code, String expected) {
        String prefix = "public class A { public void m() { ";
        String postfix = " }}";
        String codeWrapped = importJunit4 + prefix + code + postfix;
        String expectedWrapped = importJunit5 + prefix + expected + postfix;
        String actual = convertAndPrettyPrint(codeWrapped);
        assertEquals(prettyPrint(expectedWrapped), actual);
    }

    // wrap in class/method so well formed for parser
    static void assertUnchangedAfterWrappingInMethod(String code) {
        String prefix = "public class A { public void m() { ";
        String postfix = " }}";
        String codeWrapped = prefix + code + postfix;
        String actual = convertAndPrettyPrint(codeWrapped);
        assertEquals(prettyPrint(codeWrapped), actual);
    }

    // add class after import so well formed for parser
    static void assertAfterAddingClassAfter(String code, String expected) {
        String postfix = "public class A {}";
        String codeWrapped = code + postfix;
        String expectedWrapped = expected + postfix;
        String actual = convertAndPrettyPrint(codeWrapped);
        assertEquals(prettyPrint(expectedWrapped), actual);
    }

    // wrap in class/method so well formed for parser
    static void assertAfterWrappingInMethod(String originalImport, String originalMethod, String expectedImport,
                                            String expectedMethod) {
        String codeWrapped = originalImport + "public class A { public void m() { " + originalMethod + "}}";
        String expectedWrapped = expectedImport + "public class A { public void m() { " + expectedMethod + "}}";
        String actual = convertAndPrettyPrint(codeWrapped);
        assertEquals(prettyPrint(expectedWrapped), actual);
    }

    // wrap in class/method so well formed for parser
    static void assertUnchangedAfterWrappingInMethod(String originalImport, String originalMethod) {
        String codeWrapped = originalImport + "public class A { public void m() { " + originalMethod + "}}";
        String actual = convertAndPrettyPrint(codeWrapped);
        assertEquals(prettyPrint(codeWrapped), actual);
    }

    private static String convertAndPrettyPrint(String code) {
        ConversionResult result = new JunitConversionLogic(prettyPrintAndPersistChanges()).convert(code).build();
        String convertedCode = code;
        if (result.outcome == ConversionOutcome.Converted) {
            convertedCode = result.code;
        }
        return prettyPrint(convertedCode);
    }

}
