package jb.convert;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ConversionResultBuilder {
    private List<String> unsupportedFeatures = new ArrayList<>();
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

    ConversionResultBuilder unsupportedFeature(String feature) {
        unsupportedFeatures.add(feature);
        return this;
    }

    public ConversionResultBuilder path(Path path) {
        this.path = path;
        return this;
    }

    public ConversionResult build() {
        return new ConversionResult(outcome, details, code, path, unsupportedFeatures);
    }
}
