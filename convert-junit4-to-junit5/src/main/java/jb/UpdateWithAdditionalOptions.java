package jb;

import jb.configuration.Configuration;

import java.io.IOException;
import java.nio.file.Paths;

public class UpdateWithAdditionalOptions {
    public static void main(String[] args) throws IOException {
        Configuration.ConfigurationBuilder configuration = new Configuration.ConfigurationBuilder()
                .dryRun()
                .excludeMatching(path -> false)
                .skipFilesWithUnsupportedFeatures()
                .preserverFormatting();
        new Updater(configuration.build()).update(Paths.get("/path/to/your/test/directory"));
    }
}
