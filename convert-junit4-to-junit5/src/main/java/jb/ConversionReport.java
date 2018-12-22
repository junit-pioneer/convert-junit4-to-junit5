package jb;

import jb.convert.ConversionOutcome;
import jb.convert.ConversionResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ConversionReport {

    private final List<ConversionResult> results;

    ConversionReport(List<ConversionResult> results) {
        this.results = results;
    }

    String print() {
        Map<ConversionOutcome, List<ConversionResult>> byOutcome = this.results.stream().collect(Collectors.groupingBy(it -> it.outcome));
        List<ConversionResult> unchanged = byOutcome.getOrDefault(ConversionOutcome.Unchanged, Collections.emptyList());
        List<ConversionResult> converted = byOutcome.getOrDefault(ConversionOutcome.Converted, Collections.emptyList());
        Map<String, List<ConversionResult>> convertedByUnsupportedFeatures = new HashMap<>();
        converted.forEach(it -> it.unsupportedFeatures
                .forEach(feature -> convertedByUnsupportedFeatures.computeIfAbsent(feature, (key) -> new ArrayList<>()).add(it)));
        List<ConversionResult> skipped = byOutcome.getOrDefault(ConversionOutcome.Skipped, Collections.emptyList());
        Map<String, List<ConversionResult>> skippedByDetails = skipped.stream().collect(Collectors.groupingBy(it -> it.details));

        List<String> reportLines1 = new ArrayList<>();
        reportLines1.add(unchanged.size() + " unchanged");
        reportLines1.add(converted.size() + " converted");
        convertedByUnsupportedFeatures.forEach((key, value) -> reportLines1.add("   " + value.size() + " " + key));

        reportLines1.add(skipped.size() + " skipped");
        skippedByDetails.forEach((key, value) -> reportLines1.add("   " + value.size() + " " + key));

        return String.join("\n", reportLines1);
    }

}
