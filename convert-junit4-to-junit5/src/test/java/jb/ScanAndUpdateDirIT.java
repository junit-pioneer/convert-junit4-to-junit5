package jb;

import static jb.AssertionsHelper.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.apache.commons.io.*;
import org.junit.jupiter.api.*;

/**
 * Tests the program can be run against a single file, directory or nested
 * directories to update any java files under them.
 * 
 * @author jeanne
 *
 */
class ScanAndUpdateDirIT {

	private Updater updater;
	private Path path;

	@BeforeEach
	void createUpdater() {
		updater = new Updater();
	}

	@BeforeEach
	void createTestFile() {
		Random random = new Random();
		path = Paths.get("target/junit-test-" + random.nextInt());
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
