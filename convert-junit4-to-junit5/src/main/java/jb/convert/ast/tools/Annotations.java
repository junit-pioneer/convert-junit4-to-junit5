package jb.convert.ast.tools;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class Annotations {
    public static void replace(NodeWithAnnotations<?> n, Class<? extends Annotation> junit4, Class<? extends Annotation> junit5, Callback callback) {
        Optional<AnnotationExpr> annotationByClass = n.getAnnotationByClass(junit4);
        annotationByClass.ifPresent(it -> {
            it.replace(JavaParser.parseAnnotation("@" + junit5.getSimpleName()));
            callback.call();
        });
    }
}
