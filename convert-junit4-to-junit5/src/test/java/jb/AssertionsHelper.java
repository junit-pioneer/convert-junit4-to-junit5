package jb;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.*;

class AssertionsHelper {

	static void assertJunit5StyleImports(Path path) throws Exception {
		String actual = new String(Files.readAllBytes(path));
		assertAll("junit 5 style imports",
				() -> assertThat(actual, containsString("import static org.junit.jupiter.api.Assertions.*;")),
				() -> assertThat(actual, containsString("import org.junit.jupiter.api.*;")),
				() -> assertThat(actual, not(containsString("import static org.junit.Assert."))),
				() -> assertThat(actual, not(containsString("import org.junit.*"))));
	}
	
	static void assertJunit4StyleImports(Path path) throws Exception {
		String actual = new String(Files.readAllBytes(path));
		assertAll("not updated - junit 4 style imports",
				() -> assertThat(actual, not(containsString("import static org.junit.jupiter.api.Assertions.*;"))),
				() -> assertThat(actual, not(containsString("import org.junit.jupiter.api.*;"))),
				() -> assertThat(actual, containsString("import static org.junit.Assert.")),
				() -> assertThat(actual, containsString("import org.junit.*")));
	}

}
