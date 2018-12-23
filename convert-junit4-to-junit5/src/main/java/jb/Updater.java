package jb;

import jb.configuration.Configuration;
import jb.configuration.JunitConversionLogicConfiguration;
import jb.convert.ConversionOutcome;
import jb.convert.ConversionResult;
import jb.convert.ConversionResultBuilder;
import jb.convert.JunitConversionLogic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

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
        ConversionReport report = convertAll(javaFilesIn(path));
        System.out.println(report.print());
    }

    private ConversionReport convertAll(List<Path> filesToMigrate) {
        List<ConversionResult> result = filesToMigrate.stream()
                .filter(configuration.exclude().negate())
                .map(this::updateSingleFile)
                .collect(toList());
        return new ConversionReport(result);
    }

    private List<Path> javaFilesIn(Path path) throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            // only update java files
            return stream.filter(p -> p.toFile().isFile())
                    .filter(p -> p.toString().endsWith(".java"))
                    .collect(toList());
        }
    }

    private ConversionResult updateSingleFile(Path path) {
        try {
            System.out.println("__________" + path);
            String originalText = new String(Files.readAllBytes(path));
            ConversionResultBuilder resultBuilder = new JunitConversionLogic(configuration).convert(originalText).path(path);
            ConversionResult result = resultBuilder.build();
            if (!result.unsupportedFeatures.isEmpty() && configuration.skipFilesWithUnsupportedFeatures()) {
                resultBuilder.outcome(ConversionOutcome.Skipped);
                resultBuilder.details("configuration says to skip files with unsupported features");
                result = resultBuilder.build();
            }

            if (result.outcome == ConversionOutcome.Converted && (result.unsupportedFeatures.isEmpty() || !configuration.skipFilesWithUnsupportedFeatures())) {
                configuration.changeWriter().write(path, result.code);
            }
            System.out.println(result.outcome);
            return result;
        } catch (IOException | RuntimeException e) {
            System.out.println("Failed " + path.toAbsolutePath());
            // convert to runtime exception so can use inside stream operation
            throw new RuntimeException(e);
        }
    }

}
