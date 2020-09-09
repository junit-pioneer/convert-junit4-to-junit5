package jb.configuration;

import java.io.IOException;
import java.nio.file.Path;

public interface ChangeWriter {
    void write(Path path, String updatedText) throws IOException;
}
