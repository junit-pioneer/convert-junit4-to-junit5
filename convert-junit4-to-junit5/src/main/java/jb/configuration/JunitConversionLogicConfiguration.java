package jb.configuration;

import java.nio.file.Path;
import java.util.function.Predicate;

public class JunitConversionLogicConfiguration {

    private final JavaParserAdapter javaParserAdapter;
    private final ChangeWriter changeWriter;
    private final boolean skipFilesWithUnsupportedFeatures;
    private final Predicate<Path> exclude;

    JunitConversionLogicConfiguration(JavaParserAdapter javaParserAdapter, ChangeWriter changeWriter, boolean skipFilesWithUnsupportedFeatures, Predicate<Path> exclude) {
        this.javaParserAdapter = javaParserAdapter;
        this.changeWriter = changeWriter;
        this.skipFilesWithUnsupportedFeatures = skipFilesWithUnsupportedFeatures;
        this.exclude = exclude;
    }

    public JavaParserAdapter javaParser(){
        return javaParserAdapter;
    }

    public ChangeWriter changeWriter() {
        return changeWriter;
    }

    public boolean skipFilesWithUnsupportedFeatures(){
        return skipFilesWithUnsupportedFeatures;
    }

    public Predicate<Path> exclude(){
        return exclude;
    }

}
