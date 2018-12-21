package jb.configuration;

public class JunitConversionLogicConfiguration {

    private final JavaParserAdapter javaParserAdapter;

    public JunitConversionLogicConfiguration(JavaParserAdapter javaParserAdapter) {
        this.javaParserAdapter = javaParserAdapter;
    }

    public JavaParserAdapter javaParser(){
        return javaParserAdapter;
    }
}
