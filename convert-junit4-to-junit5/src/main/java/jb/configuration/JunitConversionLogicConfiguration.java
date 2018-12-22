package jb.configuration;

public class JunitConversionLogicConfiguration {

    private final JavaParserAdapter javaParserAdapter;
    private final ChangeWriter changeWriter;
    private final boolean skipFilesWithUnsupportedFeatures;

    JunitConversionLogicConfiguration(JavaParserAdapter javaParserAdapter, ChangeWriter changeWriter, boolean skipFilesWithUnsupportedFeatures) {
        this.javaParserAdapter = javaParserAdapter;
        this.changeWriter = changeWriter;
        this.skipFilesWithUnsupportedFeatures = skipFilesWithUnsupportedFeatures;
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

}
