package jb.convert.ast;

import org.junit.jupiter.api.Test;

import static jb.JunitConversionLogicFixture.assertAfterWrappingInMethod;
import static jb.JunitConversionLogicFixture.assertUnchangedAfterWrappingInMethod;

class AssertThatMigrationTest {

    @Test
    void assertThat() {
        String code = "org.junit.Assert.assertThat(a, b);";
        String expected = "org.hamcrest.MatcherAssert.assertThat(a, b);";
        assertAfterWrappingInMethod(code, expected);
    }

    @Test
    void importAlreadyPresentForAssertThat() {
        String originalImport = "import static org.hamcrest.MatcherAssert.assertThat;\n";
        String originalMethod = "assertThat(xxx);";
        assertUnchangedAfterWrappingInMethod(originalImport, originalMethod);
    }

    @Test
    void assertJImportForAssertThatPresent() {
        String originalImport = "import static org.assertj.core.api.Assertions.assertThat;\n";
        String originalMethod = "assertThat(xxx);";
        assertUnchangedAfterWrappingInMethod(originalImport, originalMethod);
    }

    @Test
    void assertJStarImportForAssertThatPresent() {
        String originalImport = "import static org.assertj.core.api.Assertions.*;\n";
        String originalMethod = "assertThat(xxx);";
        assertUnchangedAfterWrappingInMethod(originalImport, originalMethod);
    }

    @Test
    void newImportForAssertThat() {
        String originalImport = "import static org.junit.Assert.*;\n";
        String originalMethod = "assertThat(\"message\", xxx, yyy);";
        String expectedImport = "import static org.hamcrest.MatcherAssert.assertThat;\n"
                + "import static org.junit.jupiter.api.Assertions.*;\n";
        assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, originalMethod);
    }

    @Test
    void replaceFullQualifiedStaticImportToMatcherAssert() {
        String originalImport = "import static org.junit.Assert.assertThat;\n";
        String originalMethod = "assertThat(xxx);";
        String expectedImport = "import static org.hamcrest.MatcherAssert.assertThat;\n";
        assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, originalMethod);
    }

    @Test
    void keepMatcherAssertImportUnchanged() {
        String originalImport = "import org.hamcrest.MatcherAssert;\n";
        String originalMethod = "MatcherAssert.assertThat(xxx);";
        String expectedImport = "import org.hamcrest.MatcherAssert;\n";
        assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, originalMethod);
    }

    @Test
    void doNotAddMatcherAssertImportIfThereIsAStaticImportToAssertJ() {
        String originalImport = "import org.assertj.core.api.Assertions.assertThat;\n";
        String originalMethod = "assertThat(xxx);";
        String expectedImport = "import org.assertj.core.api.Assertions.assertThat;\n";
        assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, originalMethod);
    }

    @Test
    void doNotAddMatcherAssertImportIfThereIsAnImportToAssertJ() {
        String originalImport = "import org.assertj.core.api.Assertions.*;\n";
        String originalMethod = "assertThat(xxx);";
        String expectedImport = "import org.assertj.core.api.Assertions.*;\n";
        assertAfterWrappingInMethod(originalImport, originalMethod, expectedImport, originalMethod);
    }

}