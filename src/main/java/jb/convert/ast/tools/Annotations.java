package jb.convert.ast.tools;

import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;

import java.lang.annotation.Annotation;
import java.util.Optional;

public class Annotations {
    public static void replace(NodeWithAnnotations<?> n, Class<? extends Annotation> junit4, Class<? extends Annotation> junit5, Callback callback) {
        Optional<AnnotationExpr> annotationByClass = n.getAnnotationByClass(junit4);
        annotationByClass.ifPresent(it -> {
            AnnotationExpr node = new MarkerAnnotationExpr(junit5.getSimpleName());
            it.replace(node);
            callback.call();
        });
    }
}
