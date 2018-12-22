package jb.configuration;

import java.nio.file.Path;
import java.util.function.Predicate;

public class Configuration {

    public static class ConfigurationBuilder {

        private JavaParserAdapter javaParserAdapter = new PrettyPrint();
        private ChangeWriter changeWriter = new FileWriter();
        private boolean skipFilesWithUnsupportedFeatures = false;
        private Predicate<Path> exclude = path -> false;

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

        public ConfigurationBuilder excludeMatching(Predicate<Path> exclude) {
            this.exclude = exclude;
            return this;
        }

        public JunitConversionLogicConfiguration build(){
            return new JunitConversionLogicConfiguration(javaParserAdapter, changeWriter, skipFilesWithUnsupportedFeatures, exclude);
        }
    }

    public static JunitConversionLogicConfiguration prettyPrintAndPersistChanges() {
        return new ConfigurationBuilder().build();
    }

}
