package jb.configuration;

import java.io.IOException;
import java.nio.file.Path;

public class DryRun implements ChangeWriter {
    @Override
    public void write(Path path, String updatedText) throws IOException {

    }
}
