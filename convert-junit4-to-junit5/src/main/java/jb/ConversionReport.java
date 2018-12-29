package jb;

import jb.convert.ConversionOutcome;
import jb.convert.ConversionResult;
import jb.convert.UsedFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

class ConversionReport {

    private final List<ConversionResult> results;

    ConversionReport(List<ConversionResult> results) {
        this.results = results;
    }

    String print() {
        List<String> reportLines = new ArrayList<>();

        reportLines.add("");
        reportLines.add("Report");
        reportLines.add("unsupported feature usage");
        appendUnsupportedFeatureUsage(reportLines);

        reportLines.add(" ");
        reportLines.add("numbers by outcome");
        appendNumbersByOutcome(reportLines);

        return String.join("\n", reportLines);
    }

    private void appendUnsupportedFeatureUsage(List<String> reportLines) {
        List<UsedFeature> unsupportedFeatures = results.stream().flatMap(result -> result.unsupportedFeatures().stream()).collect(toList());
        if (unsupportedFeatures.isEmpty()) {
            reportLines.add("   " + "no unsupported features detected");
            return;
        }
        Map<String, List<UsedFeature>> collect = unsupportedFeatures.stream().collect(groupingBy(it -> it.name));
        collect.forEach((featureName, use) -> {
            reportLines.add("  " + featureName + ":");
            Map<String, List<UsedFeature>> details = use.stream().collect(groupingBy(it -> it.details));
            details.forEach((detail, list) -> {
                reportLines.add(format("   " + "%4d %s", list.size(), detail));
                resultsWithUsedFeatureMatching(featureName, detail).forEach(result -> reportLines.add("        " + result.path));
            });
        });
    }

    private List<ConversionResult> resultsWithUsedFeatureMatching(String featureName, String detail) {
        return results.stream().filter(result -> {
                        return result.unsupportedFeatures().stream().anyMatch(usedFeature -> {
                            return detail.equals(usedFeature.details) && featureName.equals(usedFeature.name);
                        });
                    }).collect(toList());
    }

    private void appendNumbersByOutcome(List<String> reportLines) {
        Map<ConversionOutcome, List<ConversionResult>> byOutcome = this.results.stream().collect(Collectors.groupingBy(it -> it.outcome));

        List<ConversionResult> unchanged = byOutcome.getOrDefault(ConversionOutcome.Unchanged, emptyList());
        addOutcomeLines(reportLines, unchanged, "unchanged", nothing());

        List<ConversionResult> converted = byOutcome.getOrDefault(ConversionOutcome.Converted, emptyList());
        addOutcomeLines(reportLines, converted, "converted", nothing());

        List<ConversionResult> skipped = byOutcome.getOrDefault(ConversionOutcome.Skipped, emptyList());
        addOutcomeLines(reportLines, skipped, "skipped", nothing());

        List<ConversionResult> failed = byOutcome.getOrDefault(ConversionOutcome.Failed, emptyList());
        addOutcomeLines(reportLines, failed, "failed", printPath(reportLines));
    }

    private void addOutcomeLines(List<String> reportLines, List<ConversionResult> results, String name, Consumer<List<ConversionResult>> details) {
        reportLines.add(format("%4d " + name, results.size()));
        Map<String, List<ConversionResult>> byDetails = results.stream().collect(Collectors.groupingBy(it -> it.details.orElse("no details")));
        byDetails.forEach((key, value) -> {
            reportLines.add(String.format("     %4d %s", value.size(), key));
            details.accept(value);
        });
    }

    private static Consumer<List<ConversionResult>> nothing() {
        return (ignore) -> {
        };
    }

    private static Consumer<List<ConversionResult>> printPath(List<String> reportLines) {
        return conversionResults -> conversionResults.forEach(result -> reportLines.add("          " + result.path));
    }

}
