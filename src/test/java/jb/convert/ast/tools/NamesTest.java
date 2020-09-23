package jb.convert.ast.tools;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static jb.convert.ast.tools.Names.createNameFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class NamesTest {

    @Test
    void constructANameFromAClass() {
        assertThat(createNameFor(Tag.class).asString(), equalTo(Tag.class.getCanonicalName()) );
    }

}
