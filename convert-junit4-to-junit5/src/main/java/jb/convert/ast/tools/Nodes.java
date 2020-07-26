package jb.convert.ast.tools;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;

public class Nodes {

    public static Position beginOrStartOfFile(Node node) {
        return node.getRange().map(range -> range.begin).orElseGet(() -> Position.pos(1, 1));
    }
}
