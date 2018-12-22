package jb;

import jb.configuration.Configuration;
import jb.configuration.JunitConversionLogicConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
            List<ConversionResult> results = convertAll(path);
            Map<ConversionOutcome, List<ConversionResult>> byOutcome = results.stream().collect(Collectors.groupingBy(it -> it.outcome));
            List<ConversionResult> unchanged = byOutcome.getOrDefault(ConversionOutcome.Unchanged, Collections.emptyList());
            List<ConversionResult> converted = byOutcome.getOrDefault(ConversionOutcome.Converted, Collections.emptyList());
            List<ConversionResult> skipped = byOutcome.getOrDefault(ConversionOutcome.Skipped, Collections.emptyList());
            Map<String, List<ConversionResult>> skippedByDetails = skipped.stream().collect(Collectors.groupingBy(it -> it.details));

            List<String> reportLines = new ArrayList<>();
            reportLines.add(unchanged.size() + " unchanged");
            reportLines.add(converted.size() + " converted");
            reportLines.add(skipped.size() + " skipped");
            skippedByDetails.forEach((key, value) -> reportLines.add("   " + value.size() + " " + key));

            System.out.println(String.join("\n", reportLines));
        }
    }

    private List<ConversionResult> convertAll(Path path) throws IOException {
        try (Stream<Path> stream = Files.walk(path)) {
            // only update java files
            return stream.filter(p -> p.toFile().isFile())
                    .filter(p -> p.toString().endsWith(".java"))
                    .map(this::updateSingleFile)
                    .collect(Collectors.toList());
        }
    }

    private ConversionResult updateSingleFile(Path path) {
        try {
            String originalText = new String(Files.readAllBytes(path));
            ConversionResult result = new JunitConversionLogic(configuration).convert(originalText);
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
