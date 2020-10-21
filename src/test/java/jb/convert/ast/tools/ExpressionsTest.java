package jb.convert.ast.tools;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.expr.Expression;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class ExpressionsTest {

    @ParameterizedTest
    @ValueSource(strings = {"org.hamcrest.MatcherAssert", "org.junit.jupiter.api.Assertions"})
    void ensureTheGeneratedExpressionsAreAtLeastEqual(String input) {
        Expression replacement = Expressions.fieldAccessExpressionFor(input);
        Expression parsedByJavaParser = StaticJavaParser.parseExpression(input);

        assertThat(replacement, equalTo(parsedByJavaParser));
    }
}
