package jb;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Random;

import static jb.AssertionsHelper.assertJunit5StyleImports;
import static jb.UpdaterTestHelper.writeExampleTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests either a single file is updated (or not) based on whether it has JUnit
 * 4 syntax in it.
 *
 * @author jeanne
 */
class UpdatesOnChangeTest {

    private Path path;

    @BeforeEach
    void createTestFile() {
        Random random = new Random();
        path = Paths.get("build/it/junit-test-" + random.nextInt() + ".java");
    }

    @AfterEach
    void deleteTestFile() throws IOException {
        Files.deleteIfExists(path);
    }

    // -------------------------------------------------------

    @Test
    void missingArgument() {
        Throwable actual = assertThrows(IllegalArgumentException.class, Updater::main);
        assertEquals("Please pass the absolute path of the file or directory you want to update.",
                actual.getMessage());
    }

    @Test
    void nonExistentFile() {
        Throwable actual = assertThrows(IllegalArgumentException.class, () -> Updater.main("/this/is/not/a/file/or/directory"));
        assertEquals("Please point to a valid file or directory.", actual.getMessage());
    }

    @Test
    void updateImports() throws Exception {
        String contents = "import static org.junit.Assert.*;\n" +
                "import java.util.*;\n" +
                "import org.junit.*;\n";
        writeExampleTo(path, contents);
        Updater.main(path.toAbsolutePath().toString());
        assertJunit5StyleImports(path);
    }

    @Test
    void noChangeToFileIfOnJunit5() throws Exception {
        String contents = "import static org.junit.jupiter.api.Assertions.*;\n" +
                "import java.util.*;\n" +
                "import org.junit.jupiter.api.*;\n";
        writeExampleTo(path, contents);
        FileTime lastModified = Files.getLastModifiedTime(path);
        Updater.main(path.toAbsolutePath().toString());
        assertFileTimestampNotUpdated(lastModified);
        assertJunit5StyleImports(path);
    }

    private void assertFileTimestampNotUpdated(FileTime originallyLastModified) throws IOException {
        FileTime currentLastModified = Files.getLastModifiedTime(path);
        assertEquals(originallyLastModified.toMillis(), currentLastModified.toMillis(),
                "file should not have been written to");
    }

}
