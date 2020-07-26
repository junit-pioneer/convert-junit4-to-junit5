package jb;

import jb.configuration.Configuration;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import static jb.AssertionsHelper.assertJunit4StyleImports;
import static jb.AssertionsHelper.assertJunit5StyleImports;

/**
 * Tests the program can be run against a single file, directory or nested
 * directories to update any java files under them.
 *
 * @author jeanne
 */
class ScanAndUpdateDirIT {

    private Updater updater;
    private Path path;

    @BeforeEach
    void createUpdater() {
        updater = new Updater(Configuration.prettyPrintAndPersistChanges());
    }

    @BeforeEach
    void createTestFile() {
        Random random = new Random();
        path = Paths.get("target/junit-test-" + random.nextInt() + ".java");
    }

    @AfterEach
    void deleteTestFileOrDirectory() throws IOException {
        if (Files.isRegularFile(path)) {
            Files.deleteIfExists(path);
        } else {
            FileUtils.deleteDirectory(path.toFile());
        }
    }

    // -------------------------------------------------------

    @Test
    void singleFile() throws Exception {
        Path source = Paths.get("src/test/resources/dir/subdir/Class.java");
        Files.copy(source, path);
        updater.update(path);
        assertJunit5StyleImports(path);
    }

    @Test
    void directory() throws Exception {
        File source = new File("src/test/resources/dir/subdir");
        FileUtils.copyDirectory(source, path.toFile());
        updater.update(path);
        Path javaFile = Paths.get(path.toString(), "Class.java");
        Path textFile = Paths.get(path.toString(), "readme.txt");
        assertJunit5StyleImports(javaFile);
        assertJunit4StyleImports(textFile);
    }

    @Test
    void nestedDirectories() throws Exception {
        File source = new File("src/test/resources/dir");
        FileUtils.copyDirectory(source, path.toFile());
        updater.update(path);
        Path javaFile = Paths.get(path.toString(), "subdir", "Class.java");
        Path textFile = Paths.get(path.toString(), "subdir", "readme.txt");
        assertJunit5StyleImports(javaFile);
        assertJunit4StyleImports(textFile);
    }

}
