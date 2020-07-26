package jb.configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileWriter implements ChangeWriter {
    @Override
    public void write(Path path, String updatedText) throws IOException {
        Files.write(path, updatedText.getBytes());
    }
}
