package jb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UpdaterTestHelper {
    public static void writeExampleTo(Path path, String contents) throws IOException {
        Files.createDirectories(path.getParent());
        Files.write(path, contents.getBytes());
    }
}
