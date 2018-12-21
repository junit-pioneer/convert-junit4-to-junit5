package jb.configuration;

public class Configuration {

    public static JunitConversionLogicConfiguration prettyPrint() {
        return new JunitConversionLogicConfiguration(new PrettyPrint());
    }

    public static  JunitConversionLogicConfiguration preserveFormatting() {
        return new JunitConversionLogicConfiguration(new PreserveFormatting());
    }
}
