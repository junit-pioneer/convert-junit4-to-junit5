package jb.convert.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static jb.JunitConversionLogicFixture.assertPrettyPrintEqual;
import static jb.JunitConversionLogicFixture.converted;

class SetupMethodConversionTest {

    @Test
    void ignoreToDisabled() {
        String junit4 = "import org.junit.Ignore;\n" +
                "import org.junit.Test;\n" +
                "public class A { \n" +
                "@Test @Ignore\n" +
                "public void m() { }}";
        String junit5 = "import org.junit.jupiter.api.Disabled;\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "class A { \n" +
                "@Test @Disabled\n" +
                "void m() { }}";

        assertPrettyPrintEqual(junit5, converted(junit4));
    }

    @ParameterizedTest
    @MethodSource("annotationsProvider")
    void newAnnotationNames(String oldAnnotationName, String newAnnotationName) {
        String junit4 = "import org.junit." + oldAnnotationName + ";\n"
                + "public class A { \n"
                + "@" + oldAnnotationName + "\n"
                + "public void m() { }}";
        String junit5 = "import org.junit.jupiter.api." + newAnnotationName + ";\n"
                + "public class A { \n"
                + "@" + newAnnotationName + "\n"
                + "void m() { }}";

        assertPrettyPrintEqual(junit5, converted(junit4));
    }

    static Stream<Arguments> annotationsProvider() {
        return Stream.of(Arguments.of("Before", "BeforeEach"),
                Arguments.of("BeforeClass", "BeforeAll"),
                Arguments.of("After", "AfterEach"),
                Arguments.of("AfterClass", "AfterAll")
        );
    }

    @Test
    void doNotTouchJBehaveAnnotations() {
        String compatibleCode = "import org.jbehave.core.annotations.BeforeScenario;\n"
                + "public class A { \n"
                + "@BeforeScenario\n"
                + "public void m() { }}";

        assertPrettyPrintEqual(compatibleCode, converted(compatibleCode));
    }

}