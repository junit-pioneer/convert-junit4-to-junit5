package jb;

import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

import org.junit.jupiter.api.*;

public class UpdaterTest {

	private Updater updater;
	private Path path;

	@BeforeEach
	public void createUpdater() {
		updater = new Updater();
	}

	@BeforeEach
	public void createTestFile() throws IOException {
		String contents = "import static org.junit.Assert.*;\n" +
				"import java.util.*;\n" +
				"import org.junit.*;\n";
		Random random = new Random();
		path = Paths.get("target/junit-test-" + random.nextInt());
		Files.write(path, contents.getBytes());
	}

	@AfterEach
	public void deleteTestFile() throws IOException {
		Files.deleteIfExists(path);
	}

	@Test
	public void updateImports() throws IOException {
		updater.update(path);
		String actual = new String(Files.readAllBytes(path));
		assertAll("junit 5 style imports",
				() -> assertThat(actual, containsString("import static org.junit.jupiter.api.Assertions.*;")),
				() -> assertThat(actual, containsString("import org.junit.jupiter.api.*;")),
				() -> assertThat(actual, not(containsString("import static org.junit.Assert."))),
				() -> assertThat(actual, not(containsString("import org.junit.*"))),
				() -> assertThat(actual, not(containsString("import org.junit.a*"))));

	}
}
