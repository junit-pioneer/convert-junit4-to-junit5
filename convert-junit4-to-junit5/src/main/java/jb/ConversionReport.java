package jb;

import jb.convert.ConversionOutcome;
import jb.convert.ConversionResult;
import jb.convert.UsedFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            System.out.println("   " + "no unsupported features detected");
            return;
        }
        Map<String, List<UsedFeature>> collect = unsupportedFeatures.stream().collect(groupingBy(it -> it.name));
        collect.forEach((featureName, use) -> {
            reportLines.add("  " + featureName + ":");
            Map<String, List<UsedFeature>> details = use.stream().collect(groupingBy(it -> it.details));
            details.forEach((detail, list) -> {
                String blub = format("   " + "%4d %s", list.size(), detail);
                reportLines.add(blub);
            });
        });
    }

    private void appendNumbersByOutcome(List<String> reportLines) {
        Map<ConversionOutcome, List<ConversionResult>> byOutcome = this.results.stream().collect(Collectors.groupingBy(it -> it.outcome));

        List<ConversionResult> unchanged = byOutcome.getOrDefault(ConversionOutcome.Unchanged, emptyList());
        addOutcomeLines(reportLines, unchanged, "unchanged");

        List<ConversionResult> converted = byOutcome.getOrDefault(ConversionOutcome.Converted, emptyList());
        addOutcomeLines(reportLines, converted, "converted");

        List<ConversionResult> skipped = byOutcome.getOrDefault(ConversionOutcome.Skipped, emptyList());
        addOutcomeLines(reportLines, skipped, "skipped");
    }

    private void addOutcomeLines(List<String> reportLines, List<ConversionResult> results, String name) {
        reportLines.add(format("%4d " + name, results.size()));
        Map<String, List<ConversionResult>> skippedByDetails = results.stream().collect(Collectors.groupingBy(it -> it.details.orElse("no details")));
        skippedByDetails.forEach((key, value) -> reportLines.add("     " + value.size() + " " + key));
    }

}
