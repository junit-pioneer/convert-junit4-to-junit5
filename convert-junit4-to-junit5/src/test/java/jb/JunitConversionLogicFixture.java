package jb;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import jb.configuration.Configuration;
import jb.configuration.JunitConversionLogicConfiguration;
import jb.convert.ConversionOutcome;
import jb.convert.ConversionResult;
import jb.convert.JunitConversionLogic;

import static jb.configuration.Configuration.prettyPrintAndPersistChanges;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JunitConversionLogicFixture {
    private static final String importJunit4 = "import org.junit.Assert;";
    private static final String importJunit5 = "import org.junit.jupiter.api.Assertions;";

    static void assertAfterAddingClassAfter(String code, String expected) {
        String postfix = "public class A {}";
        String junit4 = code + postfix;
        String junit5 = expected + postfix;
        assertPrettyPrintEqual(junit5, converted(junit4));
    }

    static void assertUnchangedWrappingInClass(String code) {
        String prefix = "public class A { ";
        String postfix = " }";
        String compatibleCode = prefix + code + postfix;
        assertPrettyPrintEqual(compatibleCode, converted(compatibleCode));
    }

    static void assertWrappingInClass(String code, String expected) {
        String prefix = "public class A { ";
        String postfix = " }";
        String junit4 = importJunit4 + prefix + code + postfix;
        String junit5 = importJunit5 + prefix + expected + postfix;
        assertPrettyPrintEqual(junit5, converted(junit4));
    }

    static void assertUnchangedAfterWrappingInMethod(String originalImport, String originalMethod) {
        String compatibleCode = originalImport + importJunit4 + "public class A { public void m() { " + originalMethod + "}}";
        String compatibleCodeFlup = originalImport + importJunit5 + "public class A { public void m() { " + originalMethod + "}}";
        assertPrettyPrintEqual(compatibleCodeFlup, converted(compatibleCode));
    }

    static void assertAfterWrappingInMethod(String code, String expected) {
        String prefix = "public class A { public void m() { ";
        String postfix = " }}";
        String junit4 = importJunit4 + prefix + code + postfix;
        String junit5 = importJunit5 + prefix + expected + postfix;
        assertPrettyPrintEqual(junit5, converted(junit4));
    }

    static void assertUnchangedAfterWrappingInMethod(String code) {
        String prefix = "public class A { public void m() { ";
        String postfix = " }}";
        String compatibleCode = prefix + code + postfix;
        assertPrettyPrintEqual(compatibleCode, converted(compatibleCode));
    }

    static void assertAfterWrappingInMethod(String originalImport, String originalMethod, String expectedImport, String expectedMethod) {
        String junit4 = originalImport + "public class A { public void m() { " + originalMethod + "}}";
        String junit5 = expectedImport + "public class A { public void m() { " + expectedMethod + "}}";
        assertPrettyPrintEqual(junit5, converted(junit4));
    }

    static void assertPrettyPrintEqual(String expected, String actual) {
        assertEquals(prettyPrint(expected), prettyPrint(actual));
    }

    static String converted(String code) {
        ConversionResult result = convert(code);
        String convertedCode = code;
        if (result.outcome == ConversionOutcome.Converted) {
            convertedCode = result.code;
        }
        return convertedCode;
    }

    static ConversionResult convert(String code) {
        JunitConversionLogic junitConversionLogic = new JunitConversionLogic(prettyPrintAndPersistChanges());
        return junitConversionLogic.convert(code).build();
    }

    private static String prettyPrint(String string) {
        CompilationUnit cu = JavaParser.parse(string);
        return cu.toString();
    }

    public static String convertedWithPreservedFormatting(String junit4) {
        JunitConversionLogicConfiguration configuration = new Configuration.ConfigurationBuilder().preserverFormatting().build();
        return new JunitConversionLogic(configuration).convert(junit4).build().code;
    }
}
