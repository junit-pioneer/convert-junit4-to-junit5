package jb.convert;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ConversionResult {
    public static ConversionResultBuilder skipped(String details) {
        return new ConversionResultBuilder().outcome(ConversionOutcome.Skipped).details(details);
    }

    public final ConversionOutcome outcome;
    public final String details;
    public final String code;
    public final Path path;
    public final List<String> unsupportedFeatures;
    public final List<UsedFeature> usedFeatures;

    public ConversionResult(ConversionOutcome outcome, String details, String code, Path path, List<String> unsupportedFeatures, List<UsedFeature> usedFeatures) {
        this.outcome = outcome;
        this.details = details;
        this.code = code;
        this.path = path;
        this.unsupportedFeatures = unsupportedFeatures;
        this.usedFeatures = usedFeatures;
    }

    public List<UsedFeature> unsupporedFeatures(){
        return usedFeatures.stream().filter(usedFeature -> !usedFeature.convertible).collect(Collectors.toList());
    }
}
