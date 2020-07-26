package jb.convert.ast;

import jb.JunitConversionLogicFixture;
import jb.convert.ConversionResult;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;

class RunnerReporterTest {

    @Test
    void reportRunnerClassInResult() {
        String junit4Runner ="package org.example;\n" +
                "import org.junit.experimental.runners.Enclosed;\n" +
                "import org.junit.runner.RunWith;\n" +
                "\n" +
                "@RunWith(Enclosed.class)\n" +
                "public class Tests {\n" +
                "}";
        ConversionResult conversionResult = JunitConversionLogicFixture.conversionResultWithPreservedFormatting(junit4Runner);
        assertThat(conversionResult.usedFeatures.get(0).details, CoreMatchers.equalTo("Enclosed"));
    }

}