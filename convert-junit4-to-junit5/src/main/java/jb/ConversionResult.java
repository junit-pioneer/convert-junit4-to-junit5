package jb;

class ConversionResult {
    static ConversionResult skipped(String details) {
        return new ConversionResult(ConversionOutcome.Skipped, details, null);
    }

    static ConversionResult unchanged() {
        return new ConversionResult(ConversionOutcome.Unchanged, null, null);
    }

    static ConversionResult converted(String code){
        return new ConversionResult(ConversionOutcome.Converted, null, code);
    }

    final ConversionOutcome outcome;
    final String details;
    final String code;

    private ConversionResult(ConversionOutcome outcome, String details, String code) {
        this.outcome = outcome;
        this.details = details;
        this.code = code;
    }
}
