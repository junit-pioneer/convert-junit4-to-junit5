package jb;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

import org.junit.jupiter.api.*;
import org.opentest4j.*;

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
	public void updateImports() throws IOException {
		String contents = "import static org.junit.Assert.*;\n" +
				"import java.util.*;\n" +
				"import org.junit.*;\n";
		Files.write(path, contents.getBytes());
		updater.update(path);
		String actual = new String(Files.readAllBytes(path));
		assertJunit5Styleimports(actual);
	}

	@Test
	public void noChangeToFileIfOnJunit5() throws IOException {
		String contents = "import static org.junit.jupiter.api.Assertions.*;\n" +
				"import java.util.*;\n" +
				"import org.junit.jupiter.api.*;\n";
		Files.write(path, contents.getBytes());
		FileTime lastModified = Files.getLastModifiedTime(path);
		updater.update(path);
		String actual = new String(Files.readAllBytes(path));
		assertFileTimestampNotUpdated(lastModified);
		assertJunit5Styleimports(actual);
	}

	private void assertFileTimestampNotUpdated(FileTime originallyLastModified) throws IOException {
		FileTime currentLastModified = Files.getLastModifiedTime(path);
		assertEquals(originallyLastModified.toMillis(), currentLastModified.toMillis(),
				"file should not have been written to");
	}

	private void assertJunit5Styleimports(String actual) throws MultipleFailuresError {
		assertAll("junit 5 style imports",
				() -> assertThat(actual, containsString("import static org.junit.jupiter.api.Assertions.*;")),
				() -> assertThat(actual, containsString("import org.junit.jupiter.api.*;")),
				() -> assertThat(actual, not(containsString("import static org.junit.Assert."))),
				() -> assertThat(actual, not(containsString("import org.junit.*"))),
				() -> assertThat(actual, not(containsString("import org.junit.a*"))));
	}
}
