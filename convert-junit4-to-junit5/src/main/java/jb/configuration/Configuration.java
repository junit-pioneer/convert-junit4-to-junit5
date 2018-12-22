package jb.configuration;

public class Configuration {

    public static class ConfigurationBuilder {

        private JavaParserAdapter javaParserAdapter = new PrettyPrint();
        private ChangeWriter changeWriter = new FileWriter();
        private boolean skipFilesWithUnsupportedFeatures = false;

        public ConfigurationBuilder dryRun() {
            changeWriter = new DryRun();
            return this;
        }

        public ConfigurationBuilder preserverFormatting(){
            this.javaParserAdapter = new PreserveFormatting();
            return this;
        }

        public ConfigurationBuilder skipFilesWithUnsupportedFeatures() {
            skipFilesWithUnsupportedFeatures = true;
            return this;
        }

        public JunitConversionLogicConfiguration build(){
            return new JunitConversionLogicConfiguration(javaParserAdapter, changeWriter, skipFilesWithUnsupportedFeatures);
        }
    }

    public static JunitConversionLogicConfiguration prettyPrintAndPersistChanges() {
        return new ConfigurationBuilder().build();
    }

    public static ConfigurationBuilder preserverFormatting() {
        return new ConfigurationBuilder().preserverFormatting();
    }

}
