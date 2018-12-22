package jb;

import jb.configuration.Configuration;
import jb.configuration.JunitConversionLogicConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts a file or nested directories to use JUnit 5 syntax where possible.
 * The resulting code may not compile, but it will be closer than the original
 * code so less to convert manually.
 *
 * @author jeanne
 */
public class Updater {

    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            throw new IllegalArgumentException(
                    "Please pass the absolute path of the file or directory you want to update.");
        }

        Path path = Paths.get(args[0]);
        if (!path.toFile().exists()) {
            throw new IllegalArgumentException(
                    "Please point to a valid file or directory.");
        }
        new Updater(Configuration.prettyPrintAndPersistChanges()).update(path);
    }

    Updater(JunitConversionLogicConfiguration configuration) {
        this.configuration = configuration;
    }

    private final JunitConversionLogicConfiguration configuration;

    /**
     * Update to use JUnit 5 syntax where possible
     *
     * @param path a file or directory to update recursively
     */
    void update(Path path) throws IOException {
        if (path.toFile().isFile()) {
            updateSingleFile(path);
        } else {
            ConversionReport report = convertAll(path);
            System.out.println(report.print());
        }
    }

    private ConversionReport convertAll(Path path) throws IOException {
        List<ConversionResult> result;
        try (Stream<Path> stream = Files.walk(path)) {
            // only update java files
            result = stream.filter(p -> p.toFile().isFile())
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(this::updateSingleFile)
                    .collect(Collectors.toList());
        }
        return new ConversionReport(result);
    }

    private ConversionResult updateSingleFile(Path path) {
        try {
            String originalText = new String(Files.readAllBytes(path));
            ConversionResult result = new JunitConversionLogic(configuration).convert(originalText).path(path).build();
            System.out.println(result.outcome + " " + path);

            if (result.outcome == ConversionOutcome.Converted) {
                configuration.changeWriter().write(path, result.code);
            }
            return result;
        } catch (IOException | RuntimeException e) {
            System.out.println("Failed " + path.toAbsolutePath());
            // convert to runtime exception so can use inside stream operation
            throw new RuntimeException(e);
        }
    }

}
