package jb.configuration;

public class JunitConversionLogicConfiguration {

    private final JavaParserAdapter javaParserAdapter;

    JunitConversionLogicConfiguration(JavaParserAdapter javaParserAdapter) {
        this.javaParserAdapter = javaParserAdapter;
    }

    public JavaParserAdapter javaParser(){
        return javaParserAdapter;
    }
}
