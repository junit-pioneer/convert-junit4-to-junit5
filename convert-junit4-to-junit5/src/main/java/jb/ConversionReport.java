package jb;

import jb.convert.ConversionOutcome;
import jb.convert.ConversionResult;
import jb.convert.UsedFeature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

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
            System.out.println("   "+ "no unsupported features detected");
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
        List<ConversionResult> unchanged = byOutcome.getOrDefault(ConversionOutcome.Unchanged, Collections.emptyList());
        List<ConversionResult> converted = byOutcome.getOrDefault(ConversionOutcome.Converted, Collections.emptyList());
        List<ConversionResult> skipped = byOutcome.getOrDefault(ConversionOutcome.Skipped, Collections.emptyList());

        Map<String, List<ConversionResult>> skippedByDetails = skipped.stream().collect(Collectors.groupingBy(it -> it.details));
        reportLines.add(format("%4d unchanged", unchanged.size()));
        reportLines.add(format("%4d converted", converted.size()));
        reportLines.add(format("%4d skipped", skipped.size()));
        skippedByDetails.forEach((key, value) -> reportLines.add("   " + value.size() + " " + key));
    }

}
