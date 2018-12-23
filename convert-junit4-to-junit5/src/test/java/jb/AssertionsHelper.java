package jb;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

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
