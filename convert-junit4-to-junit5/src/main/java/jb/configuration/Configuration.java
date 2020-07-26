package jb.configuration;

import java.nio.file.Path;
import java.util.function.Predicate;

public class Configuration {

    public static class ConfigurationBuilder {

        private JavaParserAdapter javaParserAdapter = new PrettyPrint();
        private ChangeWriter changeWriter = new FileWriter();
        private boolean skipFilesWithUnsupportedFeatures = false;
        private Predicate<Path> exclude = path -> false;

        /**
         * run the complete conversion but do not update the files
         */
        public ConfigurationBuilder dryRun() {
            changeWriter = new DryRun();
            return this;
        }

        /**
         * preserves the formatting of the source files as good as this
         * is supported by the java-parser library
         */
        public ConfigurationBuilder preserveFormatting() {
            this.javaParserAdapter = new PreserveFormatting();
            return this;
        }

        /**
         * This detection is very limited. Right now only @RunWith and @Rule
         * usages are detected and reported as unsupported. Have a look at
         *  {@link jb.convert.ast.RunnerReporter} and  {@link jb.convert.ast.RuleReporter}
         */
        public ConfigurationBuilder skipFilesWithUnsupportedFeatures() {
            skipFilesWithUnsupportedFeatures = true;
            return this;
        }

        /**
         * You may have some java files in your code that can not be properly handled
         * by the conversion. Use this to exclude them from being processed.
         *
         * @param exclude all files this predicated matches are excluded from the conversion
         */
        public ConfigurationBuilder excludeMatching(Predicate<Path> exclude) {
            this.exclude = exclude;
            return this;
        }

        public JunitConversionLogicConfiguration build() {
            return new JunitConversionLogicConfiguration(javaParserAdapter, changeWriter, skipFilesWithUnsupportedFeatures, exclude);
        }
    }

    public static JunitConversionLogicConfiguration prettyPrintAndPersistChanges() {
        return new ConfigurationBuilder().build();
    }

}
