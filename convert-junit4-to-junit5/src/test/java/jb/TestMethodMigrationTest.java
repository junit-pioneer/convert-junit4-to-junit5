package jb;

import jb.configuration.JavaParserAdapter;
import jb.configuration.JunitConversionLogicConfiguration;
import jb.configuration.PreserveFormatting;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class TestMethodMigrationTest {

    @Test
    void alternative() {
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
                "public class A {\n" +
                "    @Test\n" +
                "    public void m() {\n" +
                "        assertThrows(IllegalArgumentException.class, () -> {\n" +
                "            throw new IllegalArgumentException();\n" +
                "    });\n" +
                "    }\n" +
                "}";
        assertThat(migrated(junit4), equalTo(junit5));
    }

    private String migrated(String junit4) {
        JavaParserAdapter javaParserAdapter = new PreserveFormatting();
        return JunitConversionLogic.convert(new JunitConversionLogicConfiguration(javaParserAdapter), junit4);
    }
}