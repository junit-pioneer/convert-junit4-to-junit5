package jb;

import jb.configuration.Configuration;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;

public class UpdateWithAdditionalOptions {
    public static void main(String[] args) throws IOException {
        Configuration.ConfigurationBuilder configuration = new Configuration.ConfigurationBuilder()
                .dryRun()
                .excludeMatching(path -> false)
                .skipFilesWithUnsupportedFeatures()
                .preserveFormatting();

        for (String arg: args) {
            try {
                new Updater(configuration.build()).update(Paths.get(arg));
            }
            catch(NoSuchFileException nsfe) {
                System.err.printf("Error: Directory '%s' not found", nsfe.getMessage());
            }
        }
    }
}
