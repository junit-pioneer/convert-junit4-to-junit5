package jb;

import java.nio.file.Path;
import java.util.List;

class ConversionResult {
    static ConversionResultBuilder skipped(String details) {
        return new ConversionResultBuilder().outcome(ConversionOutcome.Skipped).details(details);
    }

    final ConversionOutcome outcome;
    final String details;
    final String code;
    final Path path;
    final List<String> unsupportedFeatures;

    ConversionResult(ConversionOutcome outcome, String details, String code, Path path, List<String> unsupportedFeatures) {
        this.outcome = outcome;
        this.details = details;
        this.code = code;
        this.path = path;
        this.unsupportedFeatures = unsupportedFeatures;
    }
}
