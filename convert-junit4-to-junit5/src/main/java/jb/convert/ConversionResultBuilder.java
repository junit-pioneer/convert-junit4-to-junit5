package jb.convert;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConversionResultBuilder {
    private List<UsedFeature> usedFeatures = new ArrayList<>();
    private ConversionOutcome outcome;
    private String details;
    private String code;
    private Path path;
    private Exception exception;

    public ConversionResultBuilder outcome(ConversionOutcome outcome) {
        this.outcome = outcome;
        return this;
    }

    public ConversionResultBuilder details(String details) {
        this.details = details;
        return this;
    }

    public ConversionResultBuilder usedFeature(UsedFeature usedFeature) {
        this.usedFeatures.add(usedFeature);
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

    public ConversionResultBuilder failedWith(Exception exception) {
        this.exception = exception;
        return outcome(ConversionOutcome.Failed).details(exception.getClass().getName());
    }

    public ConversionResult build() {
        return new ConversionResult(outcome, details, code, path, usedFeatures, exception);
    }
}
