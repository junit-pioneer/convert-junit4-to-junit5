package jb.convert.ast;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class CategoryClassToTagMetaAnnotationMigrationTest {

    @Test
    void convertACategoryClassIntoAMetaAnnotationWithAMatchingTag() {
        String categoryClassSource = "package org.example.category;\n" +
                "\n" +
                "public class Category {\n" +
                "}\n";

        String expectedTagMetaAnnotation = "package org.example.category;\n" +
                "\n" +
                "import java.lang.annotation.ElementType;\n" +
                "import java.lang.annotation.Retention;\n" +
                "import java.lang.annotation.RetentionPolicy;\n" +
                "import java.lang.annotation.Target;\n" +
                "import org.junit.jupiter.api.Tag;\n" +
                "\n" +
                "@Target({ ElementType.TYPE, ElementType.METHOD })\n" +
                "@Retention(RetentionPolicy.RUNTIME)\n" +
                "@Tag(\"org.example.category.Category\")\n" +
                "public @interface Category {\n" +
                "}\n";

        CompilationUnit cu = JavaParser.parse(categoryClassSource);
        new CategoryClassToTagMetaAnnotationMigration().visit(cu, null);

        assertThat(cu.toString(), equalTo(expectedTagMetaAnnotation));
    }
}