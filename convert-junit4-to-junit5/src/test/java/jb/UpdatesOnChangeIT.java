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
class UpdatesOnChangeIT {

	private Path path;

	@BeforeEach
	void createTestFile() {
		Random random = new Random();
		path = Paths.get("target/junit-test-" + random.nextInt());
	}

	@AfterEach
	void deleteTestFile() throws IOException {
		Files.deleteIfExists(path);
	}

	// -------------------------------------------------------

	@Test
	void missingArgument() {
		Throwable actual = assertThrows(IllegalArgumentException.class, () -> {
			Updater.main();
		});
		assertEquals("Please pass the absolute path of the file or directory you want to update.",
				actual.getMessage());
	}

	@Test
	void nonExistentFile() {
		Throwable actual = assertThrows(IllegalArgumentException.class, () -> {
			Updater.main("/this/is/not/a/file/or/directory");
		});
		assertEquals("Please point to a valid file or directory.", actual.getMessage());
	}

	@Test
	void updateImports() throws Exception {
		String contents = "import static org.junit.Assert.*;\n" +
				"import java.util.*;\n" +
				"import org.junit.*;\n";
		Files.write(path, contents.getBytes());
		Updater.main(path.toAbsolutePath().toString());
		assertJunit5StyleImports(path);
	}

	@Test
	void noChangeToFileIfOnJunit5() throws Exception {
		String contents = "import static org.junit.jupiter.api.Assertions.*;\n" +
				"import java.util.*;\n" +
				"import org.junit.jupiter.api.*;\n";
		Files.write(path, contents.getBytes());
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
