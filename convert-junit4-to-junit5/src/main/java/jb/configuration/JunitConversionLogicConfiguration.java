package jb.configuration;

public class JunitConversionLogicConfiguration {

    private final JavaParserAdapter javaParserAdapter;
    private final ChangeWriter changeWriter;

    JunitConversionLogicConfiguration(JavaParserAdapter javaParserAdapter, ChangeWriter changeWriter) {
        this.javaParserAdapter = javaParserAdapter;
        this.changeWriter = changeWriter;
    }

    public JavaParserAdapter javaParser(){
        return javaParserAdapter;
    }

    public ChangeWriter changeWriter() {
        return changeWriter;
    }
}
