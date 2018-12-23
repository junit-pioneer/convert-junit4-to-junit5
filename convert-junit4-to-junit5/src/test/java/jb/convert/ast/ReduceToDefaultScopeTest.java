package jb.convert.ast;

import org.junit.jupiter.api.Test;

import static jb.JunitConversionLogicFixture.convertedWithPreservedFormatting;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class ReduceToDefaultScopeTest {

    @Test
    void reduceClassScopeToDefaultIfItContainsTests() {
        String junit4 = "import org.junit.Test;\n" +
                "\n" +
                "public class A {\n" +
                "    @Test\n" +
                "    void m() {\n" +
                "    }\n" +
                "}";
        String junit5 = "import org.junit.jupiter.api.Test;\n" +
                "\n" +
                "class A {\n" +
                "    @Test\n" +
                "    void m() {\n" +
                "    }\n" +
                "}";
        assertThat(convertedWithPreservedFormatting(junit4), equalTo(junit5));
    }

    @Test
    void keepPublicClassScopeIfItDoesNotContainTests() {
        String junit4 = "import org.junit.Test;\n" +
                "\n" +
                "public class A {\n" +
                "    @Before\n" +
                "    void m() {\n" +
                "    }\n" +
                "}";
        String junit5 = "import org.junit.jupiter.api.Test;\n" +
                "\n" +
                "public class A {\n" +
                "    @BeforeEach\n" +
                "    void m() {\n" +
                "    }\n" +
                "}";
        assertThat(convertedWithPreservedFormatting(junit4), equalTo(junit5));
    }


}