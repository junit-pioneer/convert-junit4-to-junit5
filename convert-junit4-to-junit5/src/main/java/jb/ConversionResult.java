package jb;

import java.nio.file.Path;

class ConversionResult {
    static ConversionResultBuilder skipped(String details) {
        return new ConversionResultBuilder().outcome(ConversionOutcome.Skipped).details(details);
    }

    static ConversionResultBuilder unchanged() {
        return new ConversionResultBuilder().outcome(ConversionOutcome.Unchanged);
    }

    static ConversionResultBuilder converted(String code) {
        return new ConversionResultBuilder().outcome(ConversionOutcome.Converted).code(code);
    }

    final ConversionOutcome outcome;
    final String details;
    final String code;
    final Path path;

    ConversionResult(ConversionOutcome outcome, String details, String code, Path path) {
        this.outcome = outcome;
        this.details = details;
        this.code = code;
        this.path = path;
    }
}
