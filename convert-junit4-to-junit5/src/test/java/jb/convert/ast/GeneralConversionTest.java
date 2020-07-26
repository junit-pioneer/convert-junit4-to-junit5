package jb.convert.ast;

import org.junit.jupiter.api.Test;

import static jb.JunitConversionLogicFixture.assertAfterAddingClassAfter;

class GeneralConversionTest {

    @Test
    void importWildcardMainPackage() {
        String originalImport = "import org.junit.*;";
        String expectedImport = "import org.junit.jupiter.api.*;";
        assertAfterAddingClassAfter(originalImport, expectedImport);
    }

}