package jb;

import java.nio.file.Path;

public class ConversionResultBuilder {
    private ConversionOutcome outcome;
    private String details;
    private String code;
    private Path path;

    ConversionResultBuilder outcome(ConversionOutcome outcome) {
        this.outcome = outcome;
        return this;
    }

    ConversionResultBuilder details(String details) {
        this.details = details;
        return this;
    }

    ConversionResultBuilder code(String code) {
        this.code = code;
        return this;
    }

    public ConversionResultBuilder path(Path path) {
        this.path = path;
        return this;
    }

    ConversionResult build() {
        return new ConversionResult(outcome, details, code, path);
    }
}
