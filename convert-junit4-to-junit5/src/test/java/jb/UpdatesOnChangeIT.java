package jb;

import static jb.AssertionsHelper.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

import org.junit.jupiter.api.*;

/**
 * Tests either a single file is updated (or not) based on whether it has JUnit
 * 4 syntax in it.
 * 
 * @author jeanne
 *
 */
public class UpdatesOnChangeIT {

	private Updater updater;
	private Path path;

	@BeforeEach
	public void createUpdater() {
		updater = new Updater();
	}

	@BeforeEach
	public void createTestFile() {
		Random random = new Random();
		path = Paths.get("target/junit-test-" + random.nextInt());
	}

	@AfterEach
	public void deleteTestFile() throws IOException {
		Files.deleteIfExists(path);
	}

	// -------------------------------------------------------

	@Test
	public void updateImports() throws Exception {
		String contents = "import static org.junit.Assert.*;\n" +
				"import java.util.*;\n" +
				"import org.junit.*;\n";
		Files.write(path, contents.getBytes());
		updater.update(path);
		assertJunit5StyleImports(path);
	}

	@Test
	public void noChangeToFileIfOnJunit5() throws Exception {
		String contents = "import static org.junit.jupiter.api.Assertions.*;\n" +
				"import java.util.*;\n" +
				"import org.junit.jupiter.api.*;\n";
		Files.write(path, contents.getBytes());
		FileTime lastModified = Files.getLastModifiedTime(path);
		updater.update(path);
		assertFileTimestampNotUpdated(lastModified);
		assertJunit5StyleImports(path);
	}

	private void assertFileTimestampNotUpdated(FileTime originallyLastModified) throws IOException {
		FileTime currentLastModified = Files.getLastModifiedTime(path);
		assertEquals(originallyLastModified.toMillis(), currentLastModified.toMillis(),
				"file should not have been written to");
	}

}
