package jb;

import jb.configuration.Configuration;
import org.junit.jupiter.api.Test;

import static jb.configuration.Configuration.preserverFormatting;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class TestMethodMigrationTest {

    @Test
    void migrateExpectedPropertyFromTestAnnotation() {
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

    @Test
    void migrateTimeoutPropertyFromTestAnnotation() {
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
                "import static org.junit.jupiter.api.Assertions.assertTimeout;\n" +
                "\n" +
                "public class A {\n" +
                "    @Test\n" +
                "    public void m() {\n" +
                "        assertTimeout(Duration.ofMillis(42L), () -> {\n" +
                "            System.out.println(\"I'm fast\");\n" +
                "    });\n" +
                "    }\n" +
                "}";
        assertThat(migrated(junit4), equalTo(junit5));
    }

    private String migrated(String junit4) {
        return JunitConversionLogic.convert(preserverFormatting().build(), junit4);
    }

}