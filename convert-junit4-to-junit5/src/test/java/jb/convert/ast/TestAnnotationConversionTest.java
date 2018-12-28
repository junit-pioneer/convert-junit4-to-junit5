package jb.convert.ast;

import org.junit.jupiter.api.Test;

import static jb.JunitConversionLogicFixture.assertAfterAddingClassAfter;
import static jb.JunitConversionLogicFixture.convertedWithPreservedFormatting;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class TestAnnotationConversionTest {

    @Test
    void convertExpectedPropertyFromTestAnnotation() {
        String junit4 = "import java.lang.IllegalArgumentException;\n" +
                "import org.junit.Test;\n" +
                "\n" +
                "public class A {\n" +
                "    @Test(expected = IllegalArgumentException.class)\n" +
                "    public void m() {\n" +
                "        throw new IllegalArgumentException();\n" +
                "    }\n" +
                "}";
        String junit5 = "import java.lang.IllegalArgumentException;\n" +
                "import org.junit.jupiter.api.Test;\n" +
                "import static org.junit.jupiter.api.Assertions.assertThrows;\n" +
                "\n" +
                "class A {\n" +
                "    @Test\n" +
                "    void m() {\n" +
                "        assertThrows(IllegalArgumentException.class, () -> {\n" +
                "            throw new IllegalArgumentException();\n" +
                "    });\n" +
                "    }\n" +
                "}";
        assertThat(convertedWithPreservedFormatting(junit4), equalTo(junit5));
    }

    @Test
    @org.junit.Test
    void convertTimeoutPropertyFromTestAnnotation() {
        String junit4 = "import org.junit.Test;\n" +
                "\n" +
                "public class A {\n" +
                "    @Test(timeout = 42L)\n" +
                "    public void m() {\n" +
                "        System.out.println(\"I'm fast\");\n" +
                "    }\n" +
                "}";
        String junit5 = "import org.junit.jupiter.api.Test;\n" +
                "import java.time.Duration;\n" +
                "import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;\n" +
                "\n" +
                "class A {\n" +
                "    @Test\n" +
                "    void m() {\n" +
                "        assertTimeoutPreemptively(Duration.ofMillis(42L), () -> {\n" +
                "            System.out.println(\"I'm fast\");\n" +
                "    });\n" +
                "    }\n" +
                "}";
        assertThat(convertedWithPreservedFormatting(junit4), equalTo(junit5));
    }

    @Test
    void specificTestAnnotation() {
        String annotation = "import org.junit.Test;";
        String expected = "import org.junit.jupiter.api.Test;";
        assertAfterAddingClassAfter(annotation, expected);
    }

}