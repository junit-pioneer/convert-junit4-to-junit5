package jb;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JunitConversionLogicFixture {

    static String convertWhitespaceForJavaParser(String string) {
        CompilationUnit cu = JavaParser.parse(string);
        return cu.toString();
    }

    // wrap in class so well formed for parser
    static void assertWrappingInClass(String code, String expected) {
        String prefix = "public class A { ";
        String postfix = " }";
        String codeWrapped = prefix + code + postfix;
        String expectedWrapped = prefix + expected + postfix;
        String actual = JunitConversionLogic.convert(codeWrapped);
        assertEquals(convertWhitespaceForJavaParser(expectedWrapped), actual);
    }

    // wrap in class so well formed for parser
    static void assertUnchangedWrappingInClass(String code) {
        String prefix = "public class A { ";
        String postfix = " }";
        String codeWrapped = prefix + code + postfix;
        String actual = JunitConversionLogic.convert(codeWrapped);
        assertEquals(codeWrapped, actual);
    }

    // wrap in class/method so well formed for parser
    static void assertAfterWrappingInMethod(String code, String expected) {
        String prefix = "public class A { public void m() { ";
        String postfix = " }}";
        String codeWrapped = prefix + code + postfix;
        String expectedWrapped = prefix + expected + postfix;
        String actual = JunitConversionLogic.convert(codeWrapped);
        assertEquals(convertWhitespaceForJavaParser(expectedWrapped), actual);
    }

    // wrap in class/method so well formed for parser
    static void assertUnchangedAfterWrappingInMethod(String code) {
        String prefix = "public class A { public void m() { ";
        String postfix = " }}";
        String codeWrapped = prefix + code + postfix;
        String actual = JunitConversionLogic.convert(codeWrapped);
        assertEquals(codeWrapped, actual);
    }

    // add class after import so well formed for parser
    static void assertAfterAddingClassAfter(String code, String expected) {
        String postfix = "public class A {}";
        String codeWrapped = code + postfix;
        String expectedWrapped = expected + postfix;
        String actual = JunitConversionLogic.convert(codeWrapped);
        assertEquals(convertWhitespaceForJavaParser(expectedWrapped), actual);
    }

    // wrap in class/method so well formed for parser
    static void assertAfterWrappingInMethod(String originalImport, String originalMethod, String expectedImport,
                                            String expectedMethod) {
        String codeWrapped = originalImport + "public class A { public void m() { " + originalMethod + "}}";
        String expectedWrapped = expectedImport + "public class A { public void m() { " + expectedMethod + "}}";
        String actual = JunitConversionLogic.convert(codeWrapped);
        assertEquals(convertWhitespaceForJavaParser(expectedWrapped), actual);
    }

    // wrap in class/method so well formed for parser
    static void assertUnchangedAfterWrappingInMethod(String originalImport, String originalMethod) {
            String codeWrapped = originalImport + "public class A { public void m() { " + originalMethod + "}}";
            String actual = JunitConversionLogic.convert(codeWrapped);
            assertEquals(codeWrapped, actual);
        }
}
