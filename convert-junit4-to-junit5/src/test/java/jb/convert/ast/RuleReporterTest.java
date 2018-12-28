package jb.convert.ast;

import jb.JunitConversionLogicFixture;
import jb.convert.ConversionResult;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class RuleReporterTest {

    @Test
    void name() {
        String junit4Rule = "import org.junit.Rule;\n" +
                "import org.junit.rules.TemporaryFolder;\n" +
                "import org.junit.rules.TestName;\n" +
                "\n" +
                "class RuleReporterTest {\n" +
                "\n" +
                "    @Rule\n" +
                "    public final TemporaryFolder temporaryFolder = new TemporaryFolder();\n" +
                "    @Rule\n" +
                "    public final TestName testName = new TestName();\n" +
                "\n" +
                "}";
        ConversionResult conversionResult = JunitConversionLogicFixture.conversionResultWithPreservedFormatting(junit4Rule);
        assertThat(conversionResult.usedFeatures.get(0).details, equalTo("TemporaryFolder"));
        assertThat(conversionResult.usedFeatures.get(1).details, equalTo("TestName"));
    }
}