package jb.convert.ast;

import org.junit.jupiter.api.Test;

import static jb.JunitConversionLogicFixture.convertedWithPreservedFormatting;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class CategoryConversionTest {

    @Test
    void reduceClassScopeToDefaultIfItContainsTests() {
        String junit4 = "import org.junit.experimental.categories.Category;\n" +
                "import org.example.Fast;\n" +
                "\n" +
                "@Category(Fast.class)\n" +
                "@AnotherAnnotation(Fast.class)\n" +
                "class A {\n" +
                "    @Category(Fast.class)\n" +
                "    void m() {\n" +
                "    }\n" +
                "}";
        String junit5 = "import org.example.Fast;\n" +
                "\n" +
                "@Fast\n" +
                "@AnotherAnnotation(Fast.class)\n" +
                "class A {\n" +
                "    @Fast\n" +
                "    void m() {\n" +
                "    }\n" +
                "}";

        assertThat(convertedWithPreservedFormatting(junit4), equalTo(junit5));
    }

}