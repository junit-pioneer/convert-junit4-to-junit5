package jb.convert.ast.tools;

import com.github.javaparser.ast.expr.Name;

public class Names {
    public static Name createNameFor(Class<?> theClass) {
        return createNameFor(theClass.getCanonicalName());
    }

    public static Name createNameFor(String canonicalName) {
        String[] parts = canonicalName.split("\\.");
        Name current = null;
        for (String part : parts) {
            if (current == null) {
                current = new Name(part);
                continue;
            }
            current = new Name(current, part);
        }
        return current;
    }
}
